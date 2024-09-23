package uz.falconmobile.taxifood.ui.fragment

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
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.databinding.FragmentProfilBinding
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.db.utilits.DatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.HisotiryDatabaseHelper
import uz.falconmobile.taxifood.ui.activity.HistoryActivity
import java.io.IOException
import java.util.Locale

class ProfilFragment : Fragment() {
    private var binding: FragmentProfilBinding? = null
    private lateinit var database2: AppDatabase
    lateinit var dbHelper: FruitDatabaseHelper
    lateinit var dbHelper2: FoodItemDatabaseHelper
    lateinit var reqHelper: DatabaseHelper
    lateinit var hisotiryDatabaseHelper: HisotiryDatabaseHelper
    private lateinit var dao: AppDao
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        auth = FirebaseAuth.getInstance()
        database2 = AppDatabase.getDatabase(requireActivity())
        dao = database2.appDao()
        dbHelper = FruitDatabaseHelper(requireActivity())
        dbHelper2 = FoodItemDatabaseHelper(requireActivity())
        reqHelper = DatabaseHelper(requireActivity())
        hisotiryDatabaseHelper = HisotiryDatabaseHelper(requireActivity())

        CoroutineScope(Dispatchers.Main).launch {
            binding!!.tvName.text = dao.getAllUsers()[0].name
        }

        binding!!.tvLoc.text = getStringData("adress", "")

        binding!!.btnHistory.setOnClickListener {

            startActivity(Intent(requireActivity(), HistoryActivity::class.java))

        }

        binding!!.editLoc.setOnClickListener {

            showLocationDialog()

        }

        binding!!.btnDelete.setOnClickListener {

            deleteUser()

        }

        binding!!.btnLogout.setOnClickListener {

            clearSharedPreferences(requireActivity())

            CoroutineScope(Dispatchers.IO).launch {

                dao.deleteAllFavoriteFoods()
                dao.deleteAllFavoriteRestaurants()
                dao.deleteAllUsers()

            }

            dbHelper.deleteFull()
            dbHelper2.deleteFull()
            reqHelper.deleteFull()
            hisotiryDatabaseHelper.deleteAllHistory()

            requireActivity().finish()

        }

        // Inflate the layout for this fragment
        return root
    }

    private fun showLocationDialog() {
        val options = arrayOf("Use GPS", "Enter Manually")

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Change Location")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {

                    if (isLocationEnabled()) {
                        getLastLocation()
                    } else {
                        Toast.makeText(
                            requireActivity(), "Iltimos GPS ni qo'shing", Toast.LENGTH_SHORT
                        ).show()
                        enableLocation()

                    }

                } // Use GPS
                1 -> showAddressInputDialog()  // Manual entry
            }
        }
        builder.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddressInputDialog() {
        // Inflate custom layout containing TextInputLayout and TextInputEditText
        val inflater = LayoutInflater.from(requireActivity())
        val dialogView = inflater.inflate(R.layout.dialog_address_input, null)

        var loc = getStringData("adress", "")
        // Get reference to the TextInputEditText
        val addressInput: TextInputEditText = dialogView.findViewById(R.id.addressInput)

        addressInput.setText(loc.toString())
        // Create the MaterialAlertDialog
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Input Address")
            .setView(dialogView)  // Set the custom layout with TextInputLayout
            .setPositiveButton("OK") { dialog, _ ->
                val address = addressInput.text.toString()
                // Handle the input here
                if (address.isEmpty()) {

                    Toast.makeText(
                        requireActivity(), "Please fill in all fields.", Toast.LENGTH_SHORT
                    ).show()

                } else {

                    saveData("adress", addressInput.text.toString())
                    binding!!.tvLoc.text = getStringData("adress", "")

                }
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()  // Close the dialog
            }

        // Show the dialog
        builder.show()
    }

    private fun saveData(key: String, value: String) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply() // Apply asynchronously
    }

    private fun getStringData(key: String, defaultValue: String): String? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
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
            Toast.makeText(requireActivity(), "Failed to get location: $e", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getLocationName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
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
                saveData("lat", latitude.toString())
                saveData("long", longitude.toString())

                saveData("adress", fullAddress)
//                startActivity(Intent(this, MainActivity::class.java))
                Log.d(
                    "LocationInfo",
                    "City: $area, State: $stateName, Country: $countryName, Full Address: $fullAddress ${longitude} ${longitude}"
                )

                Toast.makeText(
                    requireActivity(),
                    "Location: $fullAddress, $cityName, $stateName, $countryName",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(requireActivity(), "No address found", Toast.LENGTH_SHORT).show()
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

        getLastLocation()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun deleteUser() {
        // Get the currently signed-in user
        val user: FirebaseUser? = auth.currentUser

        if (user != null) {
            // Delete the user
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireActivity(),
                            "User account deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Sign the user out after deleting
                        auth.signOut()
                        clearSharedPreferences(requireActivity())

                        CoroutineScope(Dispatchers.IO).launch {

                            dao.deleteAllFavoriteFoods()
                            dao.deleteAllFavoriteRestaurants()
                            dao.deleteAllUsers()

                        }

                        dbHelper.deleteFull()
                        dbHelper2.deleteFull()
                        reqHelper.deleteFull()
                        hisotiryDatabaseHelper.deleteAllHistory()

                        // Navigate to a different screen or close the app
                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Failed to delete user: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            Toast.makeText(requireActivity(), "No user is currently signed in", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun clearSharedPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear() // This clears all stored values
        editor.apply() // Apply changes
    }


}