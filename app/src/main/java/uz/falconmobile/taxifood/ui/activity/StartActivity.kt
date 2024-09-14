package uz.falconmobile.taxifood.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.databinding.ActivityStartBinding
import java.io.IOException
import java.util.Locale

class StartActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.tvManually.setOnClickListener {

            showAddressInputDialog()

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        db = FirebaseFirestore.getInstance()


//
//        list.forEach {
//
//            wrtie_menu(it.foodName,it.image,it.description,it.price,it.star,it.star_count,it.isVeg)
//
//        }
        val check = getStringData("is_reg", "")

        if (check == "reg") {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.btnLocation.setOnClickListener {
            if (isLocationEnabled()) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this, "Iltimos GPS ni qo'shing", Toast.LENGTH_SHORT
                ).show()
                enableLocation()

            }

        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), PERMISSION_REQUEST_CODE
            )
            return
        }

    }



    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Handle location
                val latitude = location.latitude
                val longitude = location.longitude

//                createCustomAlertDialog(this, longitude.toString(), latitude.toString())

                getLocationName(latitude, longitude)
            }
        }.addOnFailureListener { e ->
            // Handle failure
            Toast.makeText(this, "Failed to get location: $e", Toast.LENGTH_SHORT).show()
        }
    }

    fun wrtie_menu(
        name: String,
        img: String,
        description: String,
        price: String,
        star: String,
        star_count: String,
        veg: Boolean
    ) {

//        val isFavorite:Boolean ,
//        val foodName: String,
//        val description:String,
//        val image:String,
//        val price :String,
//        val star : String,
//        val star_count:String,
//        val isVeg:Boolean

        val user = hashMapOf(
            "isFavorite" to false,
            "foodName" to name,
            "description" to description,
            "image" to img,
            "price" to price,
            "star" to star,
            "star_count" to star_count,
            "is_veg" to veg

        )

        db.collection("main_food").document(name)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data dave successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error saving user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }


    private fun saveData(key: String, value: String) {
        val sharedPreferences =
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply() // Apply asynchronously
    }

    private fun getStringData(key: String, defaultValue: String): String? {
        val sharedPreferences =
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue)
    }
    @SuppressLint("MissingInflatedId")
    private fun showAddressInputDialog() {
        // Inflate custom layout containing TextInputLayout and TextInputEditText
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_address_input, null)

        // Get reference to the TextInputEditText
        val addressInput: TextInputEditText = dialogView.findViewById(R.id.addressInput)

        // Create the MaterialAlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Input Address")
            .setView(dialogView)  // Set the custom layout with TextInputLayout
            .setPositiveButton("OK") { dialog, _ ->
                val address = addressInput.text.toString()
                // Handle the input here
                if (address.isEmpty()) {

                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()

                } else {
                    saveData("adress", addressInput.text.toString())
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()  // Close the dialog
            }

        // Show the dialog
        builder.show()
    }

    private fun getLocationName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                // Get various address elements
                val cityName = address.locality
                val stateName = address.adminArea
                val countryName = address.countryName
                val fullAddress = address.getAddressLine(0)
                val area = address.maxAddressLineIndex
//                https://www.google.com/maps/@39.6623872,66.9548544,6z?entry=ttu&g_ep=EgoyMDI0MDgyOC4wIKXMDSoASAFQAw%3D%3D
//                val intent = Intent(this, StartActivity::class.java)
//                intent.putExtra("adress", fullAddress)
//                startActivity(intent)
                saveData("adress", fullAddress)
                startActivity(Intent(this, MainActivity::class.java))
                Log.d(
                    "LocationInfo",
                    "City: $area, State: $stateName, Country: $countryName, Full Address: $fullAddress ${longitude} ${longitude}"
                )

                Toast.makeText(
                    this,
                    "Location: $fullAddress, $cityName, $stateName, $countryName",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(
                "GeocoderError", "Unable to get location ${
                    e.printStackTrace()
                }"
            )
        }
    }

    private fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
        finish()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


}