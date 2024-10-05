package uz.falconmobile.taxifood.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.FoodAdapter
import uz.falconmobile.taxifood.adapter.FruitAdapter
import uz.falconmobile.taxifood.adapter.MenusAdapter
import uz.falconmobile.taxifood.adapter.RestouranAdapter
import uz.falconmobile.taxifood.databinding.FragmentHomeBinding
import uz.falconmobile.taxifood.db.models.food_model2
import uz.falconmobile.taxifood.db.models.fruit_model
import uz.falconmobile.taxifood.db.models.restouran_id_model
import uz.falconmobile.taxifood.db.models.transfer_array
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.db.utilits.DatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.category_model2
import uz.falconmobile.taxifood.model.food_change
import uz.falconmobile.taxifood.model.food_model
import uz.falconmobile.taxifood.model.order_food_model
import uz.falconmobile.taxifood.model.requerment_model
import uz.falconmobile.taxifood.model.restouran_model
import uz.falconmobile.taxifood.ui.activity.OpenCategoryActivity
import uz.falconmobile.taxifood.ui.activity.RestouranActivity
import uz.falconmobile.taxifood.ui.activity.SearchActivity
import uz.falconmobile.taxifood.ui.activity.WishlistActivity
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var database: FirebaseFirestore

    private val binding get() = _binding!!

    lateinit var rate_model: ArrayList<restouran_id_model>

    private lateinit var textView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var counter = 0
    private val textList = listOf(
        "Search ('Pizza')",
        "Search ('Cake')",
        "Search ('Binyani') ",
        "Search ('Fast - Foods')"
    )

    var aa = true
    private lateinit var database2: AppDatabase
    lateinit var dbHelper: FruitDatabaseHelper
    lateinit var dbHelper2: FoodItemDatabaseHelper
    lateinit var reqHelper: DatabaseHelper
    lateinit var adapter5: FoodAdapter
    lateinit var adapter6: FruitAdapter

    private lateinit var dao: AppDao
    private lateinit var foodList: ArrayList<food_model2>
    private lateinit var fruitList: ArrayList<fruit_model>
    var catSize = 2;
    var resSize = 2;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseFirestore.getInstance()
        foodList = arrayListOf<food_model2>()
        fruitList = arrayListOf<fruit_model>()
        database2 = AppDatabase.getDatabase(requireActivity())
        dao = database2.appDao()
        dbHelper = FruitDatabaseHelper(requireActivity())
        dbHelper2 = FoodItemDatabaseHelper(requireActivity())
        reqHelper = DatabaseHelper(requireActivity())
        textView = binding.searchInput

        updateTextEvery3Seconds()
        // Initialize the RadioGroup and RadioButtons
        val radioGroup = binding.radioGroupFood
        val radioFoodBeverage = binding.radioFoodBeverage
        val radioFoodVegetables = binding.radioFoodVegetables
        readSingleDocument()

        resSize = reqHelper.getRequerment(1)!!.x2.toInt()
        catSize = reqHelper.getRequerment(1)!!.x1.toInt()

        binding.btnProfil.setOnClickListener {

            findNavController().navigate(R.id.action_navigation_home_to_profilFragment)


        }

        binding.btnSearch.setOnClickListener {

            startActivity(Intent(requireActivity(), SearchActivity::class.java))

        }


        binding.btnClear.setOnClickListener {

            checkf(aa)
//            readFoods()
//                        viewAdapter(foodList.toMutableList())

        }

        binding.btnFilter.setOnClickListener {

            showFilterSortDialog(aa)

        }

        binding.btnVeg.setOnClickListener {

            veg_sort()

        }

        binding.btnNonVeg.setOnClickListener {

            non_veg_sort()

        }

        // Set onCheckedChangeListener to handle selection logic
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_food_beverage -> {
                    // Handle "Food & Beverage" selection

                    showFoods()

                }

                R.id.radio_food_vegetables -> {
                    // Handle "Food & Vegetables" selection

                    showVegetables()

                }
            }
        }

        binding.btnWish.setOnClickListener {

            startActivity(Intent(requireActivity(), WishlistActivity::class.java))

        }

        binding.tvLoc.text = getStringData("adress", "")
        binding.tvLocatee.setOnClickListener {

            showAddressInputDialog()

        }

        readFoods()


//        getLocationName(39.961125, 66.484008)
//        binding.rvRestouran.adapter = adapter2

        return root
    }

    private fun checkf(b: Boolean) {

        if (b) {
            readFoods()
            adapter5.notifyDataSetChanged()

        } else {
            readFruits()
            adapter6.notifyDataSetChanged()
        }

    }

    private fun showFoods() {
        binding.tvMind.visibility = View.VISIBLE
        binding.rvCategory.visibility = View.VISIBLE
        binding.explore.visibility = View.VISIBLE
        binding.rvRestouran.visibility = View.VISIBLE
        binding.tvTop.visibility = View.VISIBLE
        binding.btnVeg.visibility = View.VISIBLE
        binding.btnNonVeg.visibility = View.VISIBLE
        aa = true


        readFoods()
        readCategory(catSize)
        CoroutineScope(
            Dispatchers.Main
        ).launch {


            readAllMainCollections(resSize)

        }
    }

    private fun showVegetables() {

        binding.tvMind.visibility = View.GONE
        binding.rvCategory.visibility = View.GONE
        binding.explore.visibility = View.GONE
        binding.rvRestouran.visibility = View.GONE
        binding.tvTop.visibility = View.GONE
        binding.btnVeg.visibility = View.GONE
        binding.btnNonVeg.visibility = View.GONE
        aa = false

        readFruits()
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


    private fun updateTextEvery3Seconds() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                animateTextChange()
                counter++
                // Repeat this task every 3 seconds
                handler.postDelayed(this, 3000)
            }
        }, 3000) // Start with a 3-second delay
    }


    private fun animateTextChange() {
        // Create fade-out animation
        ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f).apply {
            duration = 500 // Fade out duration (500 ms)
            start()
        }.addListener(onEnd = {
            // Change text after fade out
            textView.text = textList[counter % textList.size]

            // Create fade-in animation
            ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply {
                duration = 500 // Fade in duration (500 ms)
                start()
            }
        })
    }


    fun calculateDistanceUsingLocation(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val startPoint = Location("locationA")
        startPoint.latitude = lat1
        startPoint.longitude = lon1

        val endPoint = Location("locationB")
        endPoint.latitude = lat2
        endPoint.longitude = lon2

        return startPoint.distanceTo(endPoint) // distance in meters
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
                    binding.tvLoc.text = getStringData("adress", "")

                }
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()  // Close the dialog
            }

        // Show the dialog
        builder.show()
    }


    private fun fetchAllMainDocuments(
        list: List<restouran_model>,
        ids: List<String>,
        rate_model: ArrayList<restouran_id_model>,
        size: Int
    ) {

        Log.d("Firestoresss", "All users: $list")
//

        val adapter = RestouranAdapter(
            requireActivity(),
            list,
            object : RestouranAdapter.ItemSetOnClickListener {
                override fun onClick(data: restouran_model, position: Int) {

                    var intent = Intent(requireActivity(), RestouranActivity::class.java)

                    intent.putExtra("Res", data)
                    intent.putExtra("Ids", transfer_array(ids[position], rate_model))
                    startActivity(intent)

                }
            })

        var manager =
            GridLayoutManager(requireActivity(), size, GridLayoutManager.HORIZONTAL, false)
        binding.rvRestouran.layoutManager = manager
        binding.rvRestouran.adapter = adapter


    }

    private suspend fun readAllMainCollections(resSize: Int) {

        var lat1 = getStringData("lat", "")!!.toDouble()
        var long1 = getStringData("long", "")!!.toDouble()


        try {
            rate_model = arrayListOf()

            print("WORKED WORKED WORKED")
            val db = FirebaseFirestore.getInstance()
            val ids = arrayListOf<String>()
            // Step 1: Get all documents from MainCollection
            val mainCollectionSnapshots = db.collection("restaurants").get().await()

            val mainDocumentsList = mutableListOf<restouran_model>()

            for (mainDocSnapshot in mainCollectionSnapshots) {
                val mainDocument = mainDocSnapshot.toObject(restouran_model::class.java) ?: continue
                Log.e("Res125", mainDocSnapshot.toString())

                // Step 2: Get SubCollection1 documents for this MainDocument
                val subCollection1Snapshots =
                    db.collection("restaurants").document(mainDocSnapshot.id)
                        .collection("types_of_food").get().await()

                ids.add(mainDocSnapshot.id)
                val subCollection1List = mutableListOf<category_model>()

                for (subDocSnapshot1 in subCollection1Snapshots) {
                    val subDocument1 = subDocSnapshot1.toObject(category_model::class.java)
                    val ids = arrayListOf<String>()

                    val subCollection2Snapshots =
                        db.collection("restaurants").document(mainDocSnapshot.id)
                            .collection("types_of_food").document(subDocSnapshot1.id)
                            .collection("foods").get().await()

                    val subCollection2List = mutableListOf<food_model>()
                    for (subDocSnapshot2 in subCollection2Snapshots) {
                        ids.add(subDocSnapshot2.id)
                        val subDocument2 = subDocSnapshot2.toObject(food_model::class.java)
                        subCollection2List.add(subDocument2)

                    }
                    val restouran_id_model = restouran_id_model(subDocSnapshot1.id, ids)
                    rate_model.add(restouran_id_model)

                    // Step 4: Add subCollection2 to subDocument1
                    val completeSubDocument1 = subDocument1.copy(foods = subCollection2List)
                    subCollection1List.add(completeSubDocument1)
                }

                // Step 5: Add subCollection1 to the mainDocument and add it to the list
                val completeMainDocument = mainDocument.copy(types_of_food = subCollection1List)

                var lat2 = completeMainDocument.latitude.toDouble()
                var long2 = completeMainDocument.longtitude.toDouble()

                var distance = calculateDistanceUsingLocation(lat1, long1, lat2, long2)

                completeMainDocument.distance = (distance / 1000).roundToInt().toString()
                mainDocumentsList.add(completeMainDocument)
            }


            fetchAllMainDocuments(mainDocumentsList, ids, rate_model, resSize)

        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            Log.d("Firestore45", "All users:$e")

        }
    }

    //    fun readFoodsBeverage() {
//        val userList = mutableListOf<food_model>()
//
//        database.collection("fruits_vegetables").get().addOnSuccessListener { result ->
//            for (document in result) {
//                val user = document.toObject(food_model::class.java)
//                userList.add(user)
//                viewAdapter2(userList, ids)
//            }
//            Log.d("Firestore", "All users: $userList")
//        }.addOnFailureListener { exception ->
//            Log.d("Firestore", "Error getting documents: ", exception)
//        }
//    }
    private fun readSingleDocument() {
        // Reference to the 'requirements' collection and the specific document
        val docRef = database.collection("MAIN").document("REQ")

        // Fetch the document
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Convert the Firestore document to RequermentModel
                    val requermentModel = document.toObject<requerment_model>()
                    requermentModel?.let {
                        if (getStringData("req", "") == "req") {
                            reqHelper.updateRequerment(1, it)
                            saveData("req", "req")
                            readCategory(requermentModel.x1.toInt())
                            CoroutineScope(
                                Dispatchers.Main
                            ).launch {


                                readAllMainCollections(requermentModel.x2.toInt())

                            }
                            Log.e("ASDOC", it.toString())
                        } else {

                            reqHelper.addRequerment(it)
                        }
                        // Handle the retrieved data (e.g., display it)
                        println("Document data: $it")
                    }
                } else {
                    println("No such document with ID: ${document.id}")
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                println("Failed to read document: ${exception.message}")
            }
    }

    fun readFoods() {

        foodList.clear()

        val ids = arrayListOf<String>()
        database.collection("main_foods").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(food_model2::class.java)
                foodList.add(user)
                ids.add(document.id)
            }
            viewAdapter2(foodList, ids)

            Log.d("Firestore", "All users: $foodList")
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting documents: ", exception)
        }
    }


    fun viewAdapter2(list: MutableList<food_model2>, ids: ArrayList<String>) {

        adapter5 =
            FoodAdapter(ids, requireActivity(), list,
                object : FoodAdapter.ItemSetOnClickListener {
                    override fun onClick(data: food_model2) {


                        if (dbHelper.addFruitItem(
                                order_food_model(
                                    name = "${data.name}",
                                    price = data.price,
                                    count = 1,
                                    imageUrl = data.banner,
                                    restouran = data.restouran
                                )
                            ) != -1L
                        ) {


                        }

                    }
                }, object : FoodAdapter.ItemSetOnClickListener2 {
                    override fun onClick(count: Int, data: food_model2) {
                        if (dbHelper.updateFruitItem(
                                order_food_model(
                                    name = "${data.name}",
                                    price = data.price,
                                    count = count,
                                    imageUrl = data.banner,
                                    restouran = data.restouran
                                )
                            ) != -1
                        ) {


                        }


                    }
                })
        val layoutManager = LinearLayoutManager(requireActivity())

        binding.rvFood.layoutManager = layoutManager

        binding.rvFood.adapter = adapter5


    }

    fun viewAdapter5(list: MutableList<fruit_model>, ids2: List<String>) {


        adapter6 = FruitAdapter(ids2,
            requireActivity(),
            list,
            object : FruitAdapter.ItemSetOnClickListener {
                override fun onClick(data: fruit_model) {
                    if (dbHelper.addFruitItem(
                            order_food_model(
                                name = "${data.name} - ${data.quanty}",
                                price = data.price,
                                count = 1,
                                imageUrl = data.banner,
                                restouran = data.restouran
                            )
                        ) != -1L
                    ) {

                    }

                }
            }, object : FruitAdapter.ItemSetOnClickListener2 {
                override fun onClick(data: fruit_model, count: Int) {
                    if (dbHelper.updateFruitItem(
                            order_food_model(
                                name = "${data.name} - ${data.quanty}",
                                price = data.price,
                                count = count,
                                imageUrl = data.banner,
                                restouran = data.restouran
                            )
                        ) != -1
                    ) {

                    }
                }

            })
        val layoutManager = GridLayoutManager(requireActivity(), 2)

        binding.rvFood.layoutManager = layoutManager

        binding.rvFood.adapter = adapter6


    }

    fun readFruits() {
        fruitList.clear()

//        val userList = mutableListOf<fruit_model>()
        val idss = arrayListOf<String>()
        database.collection("fruits_vegetables").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(fruit_model::class.java)
                fruitList.add(user)
                idss.add(document.id)
                viewAdapter5(fruitList, idss)
            }
            Log.d("Firestore", "All users: $fruitList")
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting documents: ", exception)
        }
    }

    fun readCategory(catSize: Int) {
        val userList = mutableListOf<category_model2>()
        val ids = mutableListOf<String>()
        database.collection("main_restaurants").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(category_model2::class.java)
                userList.add(user)
                ids.add(document.id)
            }
            viewAdapter(userList, ids, catSize)

            Log.d("Firestore", "All users: $userList")
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting documents: ", exception)
        }
    }


    fun viewAdapter(list: MutableList<category_model2>, ids: MutableList<String>, size: Int) {


        val adapter =
            MenusAdapter(requireActivity(), list, object : MenusAdapter.ItemSetOnClickListener {
                override fun onClick(data: category_model2, position: Int) {

                    val change = food_change(data.type, ids[position])
                    val intent = Intent(requireActivity(), OpenCategoryActivity::class.java)
                    intent.putExtra("change", change)
                    startActivity(intent)

                }
            })

        var manager =
            GridLayoutManager(requireActivity(), size, GridLayoutManager.HORIZONTAL, false)
        binding.rvCategory.layoutManager = manager

        binding.rvCategory.adapter = adapter


    }

    fun filterByStar(check: Boolean) {

        if (check) {
            var sortedList = mutableListOf<food_model2>()

            sortedList =
                foodList.sortedByDescending { it.rate?.toDouble() } as MutableList<food_model2>
//            foodList.sortedByDescending { it.firstOrNull()?.rate?.toDouble() } as MutableList<category_model>
            adapter5.updateList(sortedList)
        } else {
//            var sortedList = mutableListOf<fruit_model>()
//
//            sortedList =
//                fruitList.sortedByDescending { it.rate_count?.toInt() } as MutableList<fruit_model>
//            adapter6.updateList(sortedList)
        }
    }

    fun filterByPopular(check: Boolean) {

        if (check) {
            var sortedList = mutableListOf<food_model2>()

            sortedList =
                foodList.sortedByDescending { it.rate_count?.toInt() } as MutableList<food_model2>
            adapter5.updateList(sortedList)
        } else {


//            var sortedList = mutableListOf<fruit_model>()
//
//            sortedList =
//                fruitList.sortedByDescending { it.rate_count?.toInt() } as MutableList<fruit_model>
//            adapter6.updateList(sortedList)
        }
    }

    fun non_veg_sort() {

        val sortedList = mutableListOf<food_model2>()
        val filteredFoods = foodList.filter { !it.veg }
        sortedList.addAll(filteredFoods)
        adapter5.updateList(sortedList)
//        for (category in foodList) {
//            val filteredFoods = category.filter { !it.veg }
////
////            val filteredFoods = category.foods.filter { !it.veg }
////            val updatedCategory = category.copy(foods = filteredFoods)
////            sortedList.add(updatedCategory)
//        }
//        outerAdapter.updateList(sortedList.toMutableList())


    }

    private fun showFilterSortDialog(check: Boolean) {
        val dialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.filter_sort_bottom_sheet, null)
        dialog.setContentView(view)

        // Sort options
        val radioGroupSort = view.findViewById<RadioGroup>(R.id.radioGroupSort)
//        val seekBarMinPrice = view.findViewById<SeekBar>(R.id.seekBarMinPrice)
//        val seekBarMaxPrice = view.findViewById<SeekBar>(R.id.seekBarMaxPrice)
//        val seekBarRating = view.findViewById<SeekBar>(R.id.seekBarRating)
//        val checkBoxVeg = view.findViewById<CheckBox>(R.id.checkBoxVeg)
//        val checkBoxNonVeg = view.findViewById<CheckBox>(R.id.checkBoxNonVeg)

        val btnApply = view.findViewById<Button>(R.id.btnApply)

        btnApply.setOnClickListener {
            val selectedSort = when (radioGroupSort.checkedRadioButtonId) {
                R.id.radioSortPrice -> "price"
                R.id.radioSortPopular -> "popularity"
                R.id.radioSortHighlyRated -> "rating"
                else -> ""
            }

//            val minPrice = seekBarMinPrice.progress
//            val maxPrice = seekBarMaxPrice.progress
//            val minRating = seekBarRating.progress
//            val isVeg = checkBoxVeg.isChecked
//            val isNonVeg = checkBoxNonVeg.isChecked

            when (selectedSort) {

                "price" -> {

                    filterByPrice(check)

                }

                "popularity" -> {

                    filterByPopular(check)

                }

                "rating" -> {

                    filterByStar(check)

                }

            }
//            applyFiltersAndSort(selectedSort, minPrice, maxPrice, minRating, isVeg, isNonVeg)
            dialog.dismiss()
        }

        dialog.show()
    }


    fun veg_sort() {

        val sortedList = mutableListOf<food_model2>()
        val filteredFoods = foodList.filter { it.veg }
        sortedList.addAll(filteredFoods)
        adapter5.updateList(sortedList)
    }

    fun filterByPrice(check: Boolean) {

        if (check) {
            var sortedList = mutableListOf<food_model2>()

            sortedList =
                foodList.sortedByDescending { it.price?.toInt() } as MutableList<food_model2>
            adapter5.updateList(sortedList)
        } else {
            var sortedList = mutableListOf<fruit_model>()

            sortedList =
                fruitList.sortedByDescending { it.price?.toInt() } as MutableList<fruit_model>
            adapter6.updateList(sortedList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null) // Stop the handler when the fragment view is destroyed

    }
}