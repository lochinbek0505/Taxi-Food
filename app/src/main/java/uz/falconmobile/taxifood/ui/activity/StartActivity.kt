package uz.falconmobile.taxifood.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import uz.falconmobile.taxifood.databinding.ActivityStartBinding
import java.io.IOException
import java.util.Locale

class StartActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnLocation.setOnClickListener {
            if (isLocationEnabled()) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this, "Iltimos GPS ni qo'shing", Toast.LENGTH_SHORT
                ).show()
                enableLocation()
                val check = getStringData("is_reg", "")

                if (check == "reg") {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
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
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


}