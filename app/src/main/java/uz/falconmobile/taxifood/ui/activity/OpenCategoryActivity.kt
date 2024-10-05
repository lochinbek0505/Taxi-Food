package uz.falconmobile.taxifood.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.FoodAdapter
import uz.falconmobile.taxifood.databinding.ActivityOpenCategoryBinding
import uz.falconmobile.taxifood.db.models.food_model2
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.model.food_change
import uz.falconmobile.taxifood.model.food_model
import uz.falconmobile.taxifood.model.order_food_model

class OpenCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenCategoryBinding

    private lateinit var adapter: FoodAdapter
    lateinit var list: ArrayList<food_model2>
    lateinit var db: FirebaseFirestore
    lateinit var dbHelper: FruitDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpenCategoryBinding.inflate(layoutInflater)

        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        dbHelper = FruitDatabaseHelper(this)

        val data = intent.getSerializableExtra("change") as food_change

        binding.tvTitle.text = data.title

        readData(data)
        binding.btnClear.setOnClickListener {

            readData(data)
            //            readFoods()
//                        viewAdapter(foodList.toMutableList())

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

    fun readData(data: food_change) {
        val ids = arrayListOf<String>()
        list = arrayListOf<food_model2>()
        CoroutineScope(Dispatchers.Main).launch {
            try {


                val subCollection2Snapshots = db.collection("main_restaurants")
                    .document(data.id)
                    .collection("foods")
                    .get()
                    .await()

                val subCollection2List = mutableListOf<food_model2>()
                for (subDocSnapshot2 in subCollection2Snapshots) {

                    val subDocument2 = subDocSnapshot2.toObject(food_model2::class.java)
                    subCollection2List.add(subDocument2)
                    ids.add(subDocSnapshot2.id)

                }
                Log.e("SALOM", subCollection2List.toString())
                list.clear()
                list = subCollection2List as ArrayList<food_model2>
                viewAdapter2(list, ids)
            } catch (e: Exception) {
                Log.e("SALOM", e.toString())

            }
        }


    }

    fun viewAdapter2(list: MutableList<food_model2>, id: ArrayList<String>) {


        adapter =
            FoodAdapter(id, this, list,
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

        binding.rvFood.adapter = adapter


    }

    fun filterByStar() {

        var sortedList = mutableListOf<food_model2>()

        sortedList =
            list.sortedByDescending { it.rate?.toDouble() } as MutableList<food_model2>
//            foodList.sortedByDescending { it.firstOrNull()?.rate?.toDouble() } as MutableList<category_model>
        adapter.updateList(sortedList)

    }

    fun filterByPopular() {

        var sortedList = mutableListOf<food_model2>()

        sortedList =
            list.sortedByDescending { it.rate_count?.toInt() } as MutableList<food_model2>
        adapter.updateList(sortedList)

    }

    fun non_veg_sort() {

        val sortedList = mutableListOf<food_model2>()
        val filteredFoods = list.filter { !it.veg }
        sortedList.addAll(filteredFoods)
        adapter.updateList(sortedList)
//        for (category in foodList) {
//            val filteredFoods = category.filter { !it.veg }
////
////            val filteredFoods = category.foods.filter { !it.veg }
////            val updatedCategory = category.copy(foods = filteredFoods)
////            sortedList.add(updatedCategory)
//        }
//        outerAdapter.updateList(sortedList.toMutableList())


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


    fun veg_sort() {

        val sortedList = mutableListOf<food_model2>()
        val filteredFoods = list.filter { it.veg }
        sortedList.addAll(filteredFoods)
        adapter.updateList(sortedList)
    }

    fun filterByPrice() {

        var sortedList = mutableListOf<food_model2>()

        sortedList =
            list.sortedByDescending { it.price?.toInt() } as MutableList<food_model2>
        adapter.updateList(sortedList)

    }


}