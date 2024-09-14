package uz.falconmobile.taxifood.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.falconmobile.taxifood.adapter.InnerAdapter
import uz.falconmobile.taxifood.adapter.OuterAdapter
import uz.falconmobile.taxifood.databinding.ActivityRestouranBinding
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.food_model
import uz.falconmobile.taxifood.model.restouran_model

class RestouranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestouranBinding

    lateinit var dbHelper: FoodItemDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRestouranBinding.inflate(layoutInflater)
//        fetchAllMainDocuments()
        setContentView(binding.root)
        val dbHelper = FoodItemDatabaseHelper(this)

        var data = intent.getSerializableExtra("Res") as restouran_model

        Log.e("TAAG", data.toString())


        val adapter =
            OuterAdapter(data.types_of_food, object : InnerAdapter.ItemSetOnClickListener {
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

        binding.recyclerView.adapter = adapter

        binding.tvName.text = data.name
        binding.tvStar.text = data.rate
        binding.tvLocate.text = data.location
        binding.tvLenght.text = data.lenght
        binding.tvRateCount.text = "${data.rate_count} ratings"

    }


    private fun fetchAllMainDocuments() {
        CoroutineScope(Dispatchers.Main).launch {
            val mainDocuments = readAllMainCollections()


            if (mainDocuments != null) {
                // Process the list of MainDocument data here (e.g., update UI)
                for (document in mainDocuments) {
                    println(document)
                }
            } else {
                // Handle error (e.g., show error message)
                println("Error reading data")
            }
        }
    }


    private suspend fun readAllMainCollections(): List<restouran_model>? {
        try {
            val db = FirebaseFirestore.getInstance()

            // Step 1: Get all documents from MainCollection
            val mainCollectionSnapshots = db.collection("restaurants").get().await()

            val mainDocumentsList = mutableListOf<restouran_model>()

            for (mainDocSnapshot in mainCollectionSnapshots) {
                val mainDocument = mainDocSnapshot.toObject(restouran_model::class.java) ?: continue

                // Step 2: Get SubCollection1 documents for this MainDocument
                val subCollection1Snapshots = db.collection("restaurants")
                    .document(mainDocSnapshot.id)
                    .collection("types_of_food")
                    .get()
                    .await()

                val subCollection1List = mutableListOf<category_model>()

                for (subDocSnapshot1 in subCollection1Snapshots) {
                    val subDocument1 = subDocSnapshot1.toObject(category_model::class.java)

                    // Step 3: Get SubCollection2 documents for this SubDocument1
                    val subCollection2Snapshots = db.collection("restaurants")
                        .document(mainDocSnapshot.id)
                        .collection("types_of_food")
                        .document(subDocSnapshot1.id)
                        .collection("foods")
                        .get()
                        .await()

                    val subCollection2List = mutableListOf<food_model>()
                    for (subDocSnapshot2 in subCollection2Snapshots) {
                        val subDocument2 = subDocSnapshot2.toObject(food_model::class.java)
                        subCollection2List.add(subDocument2)
                    }

                    // Step 4: Add subCollection2 to subDocument1
                    val completeSubDocument1 = subDocument1.copy(foods = subCollection2List)
                    subCollection1List.add(completeSubDocument1)
                }

                // Step 5: Add subCollection1 to the mainDocument and add it to the list
                val completeMainDocument = mainDocument.copy(types_of_food = subCollection1List)
                mainDocumentsList.add(completeMainDocument)
            }

            return mainDocumentsList // Return the list of all MainDocuments

        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            return null
        }
    }
}

private suspend fun readNestedCollection(mainDocId: String): restouran_model? {
    try {
        val db = FirebaseFirestore.getInstance()

        // Step 1: Get the main document
        val mainDocSnapshot = db.collection("restaurants")
            .document(mainDocId)
            .get()
            .await()

        val mainDocument = mainDocSnapshot.toObject(restouran_model::class.java) ?: return null

        // Step 2: Get SubCollection1 documents
        val subCollection1Snapshots = db.collection("restaurants")
            .document(mainDocId)
            .collection("types_of_food")
            .get()
            .await()

        val subCollection1List = mutableListOf<category_model>()

        for (subDocSnapshot1 in subCollection1Snapshots) {
            val subDocument1 = subDocSnapshot1.toObject(category_model::class.java)

            // Step 3: Get SubCollection2 documents
            val subCollection2Snapshots = db.collection("restaurants")
                .document(mainDocId)
                .collection("types_of_food")
                .document(subDocSnapshot1.id)
                .collection("foods")
                .get()
                .await()

            val subCollection2List = mutableListOf<food_model>()
            for (subDocSnapshot2 in subCollection2Snapshots) {
                val subDocument2 = subDocSnapshot2.toObject(food_model::class.java)
                subCollection2List.add(subDocument2)
            }

            // Step 4: Add subCollection2 to subDocument1
            val completeSubDocument1 = subDocument1.copy(foods = subCollection2List)
            subCollection1List.add(completeSubDocument1)
        }

        // Step 5: Add subCollection1 to the mainDocument and return it
        return mainDocument.copy(types_of_food = subCollection1List)

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

