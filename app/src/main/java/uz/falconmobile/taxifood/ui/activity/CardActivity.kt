package uz.falconmobile.taxifood.ui.activity

import CartAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.falconmobile.taxifood.OrderRepository
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.databinding.ActivityCardBinding
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.db.utilits.DatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.model.order_food_model
import uz.falconmobile.taxifood.model.order_model
import uz.falconmobile.taxifood.model.requerment_model
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardBinding


    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao
    private lateinit var mainList: MutableList<order_food_model>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var fruitHelper: FruitDatabaseHelper
    private lateinit var foodHelper: FoodItemDatabaseHelper
    lateinit var orderRepository: OrderRepository

    var priceList = mutableListOf<String>("", "", "", "", "")
    private lateinit var reqHelper: DatabaseHelper
    var latitude2: Double = 0.0
    var longitude2: Double = 0.0

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var data: requerment_model

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardBinding.inflate(layoutInflater)

        setContentView(binding.root)

        mainList = mutableListOf<order_food_model>()

        reqHelper = DatabaseHelper(this)
        fruitHelper = FruitDatabaseHelper(this)
        foodHelper = FoodItemDatabaseHelper(this)

        data = reqHelper.getRequerment(1)!!


        database = AppDatabase.getDatabase(this)
        dao = database.appDao()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.tvLocate.text = getStringData("adress", "")
        binding.btnChange.setOnClickListener {
            showLocationDialog()

        }


        var distance = calculateDistanceUsingLocation(
            getStringData("lat", "")!!.toDouble(),
            getStringData("long", "")!!.toDouble(),
            data.lat.toDouble(),
            data.lon.toDouble()
        )
        binding.btnChekout.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                showContact(dao.getAllUsers()[0].number)
            }
        }


        showFoods(distance, data)

        binding.btnFullLocate.setOnClickListener {

            showAddressInputDialog()

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
        CoroutineScope(Dispatchers.Main).launch {
            Log.e("USER_DATA", dao.getAllUsers().toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showContact(phone: String) {
        // Create a LinearLayout to wrap the TextInputLayout
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(50, 0, 50, 0) // Optional padding to make it look better

        // Create a TextInputLayout to wrap the MaterialEditText
        val textInputLayout = TextInputLayout(this)
        val editText = TextInputEditText(this)

        // Customize the TextInputLayout and EditText
        textInputLayout.hint = "Enter your phone number"
        editText.setText(phone)
        textInputLayout.addView(editText)

        // Add the TextInputLayout to the container
        container.addView(textInputLayout)

        // Create an AlertDialog
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Phone Number")
            .setMessage("    ")
            .setView(container) // Set the custom view
            .setPositiveButton("Ok") { dialog: DialogInterface, _: Int ->
                val inputText = editText.text.toString()
                // Handle the input text

                if (inputText.isNotEmpty()) {

                orderRepository = OrderRepository()
                Log.w("OrderRepository12", "1")

                val currentTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val formattedTime = currentTime.format(formatter)
                CoroutineScope(Dispatchers.Main).launch {
                    // Sample order
                    val order = order_model(
                        customerName = dao.getAllUsers()[0].name,
                        isConfirmed = false,
                        location = getStringData("adress", "").toString(),
                        latitute = getStringData("lat", "")!!.toDouble(),
                        longtute = getStringData("long", "")!!.toDouble(),
                        orderId = System.currentTimeMillis().toString(),
                        orderTime = formattedTime,
                        phone = inputText,
                        subTotal = priceList[0],
                        taxPrice = priceList[1],
                        deliveryPrice = priceList[2],
                        total = priceList[3],
                        orderedFood = mainList as ArrayList<order_food_model>
                    )
                    Log.w("OrderRepository12", "2")

                    // Write the order to Firebase
                    orderRepository.writeOrderToFirebase(order, this@CardActivity)
//                showFoods(distance, data)
                    dialog.dismiss()
                }


                }
                else{

                    Toast.makeText(this, "Please input phone number", Toast.LENGTH_SHORT).show()

                }



            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            .create()

        // Show the dialog
        alertDialog.show()
    }

    private fun showFoods(dintance: Float, model: requerment_model) {

        mainList.clear()
        Log.e("USER_DATA", "showFoods:$dintance")

        for (allFruitItem in fruitHelper.getAllFruitItems()) {
            mainList.add(
                order_food_model(
                    allFruitItem.imageUrl,
                    allFruitItem.name,
                    allFruitItem.price,
                    allFruitItem.count,
                    allFruitItem.restouran
                )
            )
        }

        for (foodItem in foodHelper.getAllFoodItems()) {

            mainList.add(
                order_food_model(
                    foodItem.banner,
                    foodItem.name,
                    foodItem.price,
                    1,
                    foodItem.restouran
                )
            )

        }

        if (mainList.isNotEmpty()) {

            binding.all.visibility = View.VISIBLE
            binding.empty.visibility = View.GONE

        } else {
            binding.all.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
        }

// In your Activity or Fragment
        val cartAdapter = CartAdapter(this, mainList.toMutableList(), { totalPrice ->
            // Update total price TextView or other UI elements
            binding.tvSubTotal.text = "$$totalPrice"
            binding.tvTaxee.text =
                "$${"%,.2f".format(Locale.ENGLISH, totalPrice * model.tax.toInt() / 100)}"
            binding.tvDeliver.text =
                "$${"%,.2f".format(Locale.ENGLISH, dintance * model.del.toInt() / 1000)}"
            binding.tvPrice.text = "$${
                "%,.2f".format(
                    Locale.ENGLISH,
                    totalPrice + totalPrice * model.tax.toInt() / 100 + dintance * model.del.toInt() / 1000
                )
            }"

            priceList[0] = "$$totalPrice"
            priceList[1] = "$${
                "%,.2f".format(
                    Locale.ENGLISH, totalPrice * model.tax.toInt() / 100
                )
            }"

            priceList[2] = "$${"%,.2f".format(Locale.ENGLISH, dintance * model.del.toInt() / 1000)}"
            priceList[3] = "$${
                "%,.2f".format(
                    Locale.ENGLISH,
                    totalPrice + totalPrice * model.tax.toInt() / 100 + dintance * model.del.toInt() / 1000
                )
            }"


        }, { size ->
            if (size != 0) {

                binding.all.visibility = View.VISIBLE
                binding.empty.visibility = View.GONE

            } else {
                binding.all.visibility = View.GONE
                binding.empty.visibility = View.VISIBLE
            }
            // Handle what happens when an item is removed, like showing a toast
        })

        binding.recyclerView.adapter = cartAdapter

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false // We don't want to handle drag & drop
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    cartAdapter.removeItem(position)
                }
            }
    }

    private fun showLocationDialog() {
        val options = arrayOf("Use GPS", "Enter Manually")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Location")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {

                    if (isLocationEnabled()) {
                        getLastLocation()
                    } else {
                        Toast.makeText(
                            this, "Iltimos GPS ni qo'shing", Toast.LENGTH_SHORT
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
        val inflater = LayoutInflater.from(this@CardActivity)
        val dialogView = inflater.inflate(R.layout.dialog_address_input, null)

        var loc = getStringData("adress", "")
        // Get reference to the TextInputEditText
        val addressInput: TextInputEditText = dialogView.findViewById(R.id.addressInput)

        addressInput.setText(loc.toString())
        // Create the MaterialAlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Input Address")
            .setView(dialogView)  // Set the custom layout with TextInputLayout
            .setPositiveButton("OK") { dialog, _ ->
                val address = addressInput.text.toString()
                // Handle the input here
                if (address.isEmpty()) {

                    Toast.makeText(
                        this@CardActivity, "Please fill in all fields.", Toast.LENGTH_SHORT
                    ).show()

                } else {

                    saveData("adress", addressInput.text.toString())
                    binding.tvLocate.text = getStringData("adress", "")

                }
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()  // Close the dialog
            }

        // Show the dialog
        builder.show()
    }

    private fun saveData(key: String, value: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply() // Apply asynchronously
    }

    private fun getStringData(key: String, defaultValue: String): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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

                latitude2 = latitude
                longitude2 = longitude

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
                saveData("lat", latitude.toString())
                saveData("long", longitude.toString())

                saveData("adress", fullAddress)
//                startActivity(Intent(this, MainActivity::class.java))
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

        getLastLocation()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun calculateDistanceUsingLocation(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Float {

        Log.e("LocationInfo", "lat1: $lat1, lon1: $lon1, lat2: $lat2, lon2: $lon2")
        val startPoint = Location("locationA")
        startPoint.latitude = lat1
        startPoint.longitude = lon1

        val endPoint = Location("locationB")
        endPoint.latitude = lat2
        endPoint.longitude = lon2

        return startPoint.distanceTo(endPoint) // distance in meters
    }


}