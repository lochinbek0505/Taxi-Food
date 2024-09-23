package uz.falconmobile.taxifood.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.InnerAdapter
import uz.falconmobile.taxifood.adapter.OuterAdapter
import uz.falconmobile.taxifood.databinding.ActivityRestouranBinding
import uz.falconmobile.taxifood.db.models.FavoriteRestaurants
import uz.falconmobile.taxifood.db.models.restouran_id_model
import uz.falconmobile.taxifood.db.models.transfer_array
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.food_model
import uz.falconmobile.taxifood.model.restouran_model
import java.util.Locale

class RestouranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestouranBinding
    lateinit var dbHelper: FoodItemDatabaseHelper
    lateinit var rate_model: ArrayList<restouran_id_model>
    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao
    var check = false

    private lateinit var foodList: MutableList<category_model>
    private lateinit var outerAdapter: OuterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRestouranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = FoodItemDatabaseHelper(this)


        var data = intent.getSerializableExtra("Res") as restouran_model
        var id1 = intent.getSerializableExtra("Ids") as transfer_array
        Log.e("TAAG", data.toString())


        database = AppDatabase.getDatabase(this)

        dao = database.appDao()


        binding.btnBack.setOnClickListener {


            finish()

        }


        CoroutineScope(Dispatchers.IO).launch {

            check = doesRestaurantExist(data.name)

            if (check) {

                binding.ivWishlist.setImageResource(R.drawable.heart)

            }

        }

        var rate = data.rate.toDouble()
        var count = data.rate_count.toInt()

        binding.btnRate.setOnClickListener {

            showRatingDialog(id1.resId, rate, count)

        }

        viewAdapter(data.types_of_food.toMutableList(), id1.ids, id1)

        foodList = data.types_of_food.toMutableList()
        binding.ivWishlist.setOnClickListener {

            if (!check) {
                CoroutineScope(Dispatchers.Main).launch {
                    dao.insertFavoriteRestaurant(
                        FavoriteRestaurants(
                            name = data.name,
                            image = data.banner,
                            star = data.rate,
                            star_count = data.rate_count,
                            distance = data.distance,
                            locate = data.location,
                            isFavorite = true,
                            id = id1.resId.toString()
                        )
                    )
                }
                binding.ivWishlist.setImageResource(R.drawable.heart)
                check = true
            } else {

                CoroutineScope(Dispatchers.Main).launch {

                    dao.deleteRestaurantByName(data.name)

                }

                binding.ivWishlist.setImageResource(R.drawable.baseline_favorite_border_24)
                check = false
            }
        }




        binding.tvName.text = data.name
        binding.tvStar.text = data.rate
        binding.tvLocate.text = data.location
        binding.tvLenght.text ="${ data.distance } km"
        binding.tvRateCount.text = "${data.rate_count} ratings"

        binding.btnClear.setOnClickListener {


            outerAdapter.updateList(data.types_of_food.toMutableList())
//            viewAdapter(data.types_of_food.toMutableList(), id1.ids, id1)

        }

        binding.btnFilter.setOnClickListener {

            showFilterSortDialog()

        }

        binding.btnVeg.setOnClickListener {

            veg_sort()

        }

        binding.btnNonVeg.setOnClickListener {

            non_veg_sort()

        }
    }

    suspend fun doesRestaurantExist(name: String): Boolean {
        return dao.isRestaurantExists(name) > 0
    }

    fun updateDocument(
        collectionName: String,
        documentId: String,
        updates: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Initialize Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Get the reference to the document
        val docRef = db.collection(collectionName).document(documentId)

        // Update the document with the provided fields
        docRef.update(updates)
            .addOnSuccessListener {

                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Call the failure callback if update fails
                onFailure(exception)
            }
    }

    private fun showRatingDialog(id: String, rate1: Double, count1: Int) {
        // Inflate the custom layout for the dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_rating, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        var rate = rate1
        var count = count1


        // Create and show the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Rate Us")
            .setView(dialogView)
            .setPositiveButton("Submit") { dialogInterface, _ ->
                val rating = ratingBar.rating


                rate = (rate * count + rating) / (count + 1)
                count++
                // Define the fields to update
                val updates = mapOf<String, Any>(
                    "rate" to String.format(Locale.US, "%.1f", rate).toString(),
                    "rate_count" to count.toString()
                )

                updateDocument(
                    collectionName = "restaurants",
                    documentId = id,
                    updates = updates,
                    onSuccess = {

                        binding.tvStar.text = String.format("%.1f", rate).toString()
                        binding.tvRateCount.text = "$count ratings"
                        Log.d("Firestoress", "Document successfully updated!")
                    },
                    onFailure = { exception ->
                        Log.w("Firestoress", "Error updating document", exception)
                    }
                )

                // Handle the rating submission
                Toast.makeText(this, "You rated: $rating stars", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    fun viewAdapter(
        list: MutableList<category_model>,
        list2: ArrayList<restouran_id_model>,
        model: transfer_array
    ) {

        outerAdapter =
            OuterAdapter(this, list, list2, model, object : InnerAdapter.ItemSetOnClickListener {
                override fun onClick(data: food_model) {

                    if (dbHelper.addFoodItem(data) != -1L) {


                        Toast.makeText(
                            this@RestouranActivity,
                            "Successfully added",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@RestouranActivity,
                            "Failed to add item",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

        binding.recyclerView.adapter = outerAdapter


    }

    private suspend fun readAllMainCollections(id: String) {
        try {
            rate_model = arrayListOf()
            val db = FirebaseFirestore.getInstance()
            val ids = arrayListOf<String>()
            // Step 1: Get all documents from MainCollection

            // Step 2: Get SubCollection1 documents for this MainDocument
            val subCollection1Snapshots =
                db.collection("restaurants").document(id)
                    .collection("types_of_food").get().await()

            ids.add(id)
            val subCollection1List = mutableListOf<category_model>()

            for (subDocSnapshot1 in subCollection1Snapshots) {
                val subDocument1 = subDocSnapshot1.toObject(category_model::class.java)

                val ids = arrayListOf<String>()

                val subCollection2Snapshots =
                    db.collection("restaurants").document(id)
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

                Log.e("TAAG", subCollection1List.toString())

            }


//            viewAdapter(subCollection1List, rate_model)

        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
        }
    }

    fun filterByStar() {

        var sortedList = mutableListOf<category_model>()

        sortedList =
            foodList.sortedByDescending { it.foods.firstOrNull()?.rate?.toDouble() } as MutableList<category_model>
        outerAdapter.updateList(sortedList)
    }

    fun filterByPopular() {

        var sortedList = mutableListOf<category_model>()

        sortedList =
            foodList.sortedByDescending { it.foods.firstOrNull()?.rate_count?.toInt() } as MutableList<category_model>
        outerAdapter.updateList(sortedList)
    }

    fun non_veg_sort() {

        val sortedList = mutableListOf<category_model>()
        for (category in foodList) {
            val filteredFoods = category.foods.filter { !it.veg }
            val updatedCategory = category.copy(foods = filteredFoods)
            sortedList.add(updatedCategory)
        }
        outerAdapter.updateList(sortedList.toMutableList())


    }

    fun veg_sort() {

        val sortedList = mutableListOf<category_model>()
        for (category in foodList) {
            val filteredFoods = category.foods.filter { it.veg }
            val updatedCategory = category.copy(foods = filteredFoods)
            sortedList.add(updatedCategory)
        }
        outerAdapter.updateList(sortedList.toMutableList())


    }
    fun filterByPrice() {

        var sortedList = mutableListOf<category_model>()

        sortedList =
            foodList.sortedByDescending { it.foods.firstOrNull()?.price?.toInt() } as MutableList<category_model>
        outerAdapter.updateList(sortedList)

    }

    private fun showFilterSortDialog() {
        val dialog = BottomSheetDialog(this)
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

                    filterByPrice()

                }

                "popularity" -> {

                    filterByPopular()

                }

                "rating" -> {

                    filterByStar()

                }

            }
//            applyFiltersAndSort(selectedSort, minPrice, maxPrice, minRating, isVeg, isNonVeg)
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun applyFiltersAndSort(
        selectedSort: String,
        minPrice: Int,
        maxPrice: Int,
        minRating: Int,
        isVeg: Boolean,
        isNonVeg: Boolean
    ) {
        val filteredList = foodList.map { category ->
            val filteredFoods = category.foods.filter { food ->
                val price = food.price.toInt()
                val rating = food.rate.toDouble()
                val veg = food.veg
                Log.e("AAASSS", "$price , $rating , $veg")

                price in minPrice..maxPrice &&
                        rating >= minRating &&
                        (isVeg && veg || isNonVeg && !veg)

            }
            Log.e("AAASSS", filteredFoods.toString())

            category.copy(foods = filteredFoods)
        }

        val sortedList = when (selectedSort) {
            "price" -> filteredList.sortedBy { it.foods.firstOrNull()?.price?.toInt() }
            "popularity" -> filteredList.sortedByDescending { it.foods.firstOrNull()?.rate_count?.toInt() }
            "rating" -> filteredList.sortedByDescending { it.foods.firstOrNull()?.rate?.toDouble() }
            else -> filteredList
        }


        Log.e("AAASSS", sortedList.toString())

        outerAdapter.updateList(sortedList.toMutableList())
    }

    private fun getFoodList(): List<category_model> {
        // Fetch your food list here
        return listOf()
    }

}


