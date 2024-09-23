package uz.falconmobile.taxifood.ui.activity

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.FoodAdapter
import uz.falconmobile.taxifood.databinding.ActivitySearchBinding
import uz.falconmobile.taxifood.db.models.food_model2
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.model.food_model
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: FoodAdapter
    private val allFoodList = mutableListOf<food_model2>()  // All fetched food items
    private val filteredFoodList = mutableListOf<food_model2>() // Filtered food items for display
    lateinit var dbHelper2: FoodItemDatabaseHelper

    // Register voice search activity launcher
    private val voiceSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (matches != null && matches.isNotEmpty()) {
                    // Set the recognized text to the EditText for search
                    findViewById<EditText>(R.id.customSearchEditText).setText(matches[0])
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper2 = FoodItemDatabaseHelper(this)
        setupRecyclerView()
        setupSearchBar()
        loadAllFoods() // Load all foods on activity start
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FoodAdapter(
            emptyList(),
            this,
            filteredFoodList,
            object : FoodAdapter.ItemSetOnClickListener {
                override fun onClick(data: food_model2) {
                    if (dbHelper2.addFoodItem(
                            food_model(
                                data.name,
                                data.description,
                                data.banner,
                                data.price,
                                data.rate,
                                data.rate_count,
                                data.veg
                            )
                        ) != -1L
                    ) {

                        Toast.makeText(
                            this@SearchActivity, "Successfully added", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SearchActivity, "Food added already", Toast.LENGTH_SHORT
                        ).show()
                    }            // Handle item click

                }
            })
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchBar() {
        // Get references to custom search EditText and microphone button
        val searchEditText = findViewById<EditText>(R.id.customSearchEditText)
        val micButton = findViewById<ImageView>(R.id.micButton)

        // Listen for text changes in the EditText to handle search queries
        searchEditText.addTextChangedListener { text ->
            if (!text.isNullOrEmpty()) {
                filterFoods(text.toString()) // Filter the food list based on user input
            } else {
                // If the search query is empty, show all foods
                filteredFoodList.clear()
                filteredFoodList.addAll(allFoodList)
                adapter.notifyDataSetChanged()
            }
        }

        // Set up click listener for microphone button
        micButton.setOnClickListener {
            startVoiceSearch()
        }
    }

    private fun startVoiceSearch() {
        // Trigger voice search with RecognizerIntent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search")
        }
        try {
            voiceSearchLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice search not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAllFoods() {
        // Fetch all food items from Firestore and display them initially
        FirebaseFirestore.getInstance().collection("main_foods")
            .get()
            .addOnSuccessListener { documents ->
                allFoodList.clear()
                for (document in documents) {
                    val foodItem = document.toObject(food_model2::class.java)
                    allFoodList.add(foodItem)
                }
                // Initially, show all foods
                filteredFoodList.clear()
                filteredFoodList.addAll(allFoodList)
                adapter.notifyDataSetChanged()
                binding.recyclerView.visibility =
                    if (filteredFoodList.isNotEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterFoods(query: String) {
        // Filter the allFoodList based on the search query
        filteredFoodList.clear()
        val lowerCaseQuery = query.lowercase(Locale.getDefault())
        for (food in allFoodList) {
            if (food.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                filteredFoodList.add(food)
            }
        }
        adapter.notifyDataSetChanged()
        binding.recyclerView.visibility =
            if (filteredFoodList.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
