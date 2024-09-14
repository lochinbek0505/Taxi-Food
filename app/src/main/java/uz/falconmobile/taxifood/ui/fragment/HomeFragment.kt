package uz.falconmobile.taxifood.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.FoodAdapter
import uz.falconmobile.taxifood.adapter.MenusAdapter
import uz.falconmobile.taxifood.adapter.RestouranAdapter
import uz.falconmobile.taxifood.databinding.FragmentHomeBinding
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.category_model2
import uz.falconmobile.taxifood.model.food_model
import uz.falconmobile.taxifood.model.restouran_model
import uz.falconmobile.taxifood.ui.activity.RestouranActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var database: FirebaseFirestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var database2: AppDatabase
    private lateinit var dao: AppDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseFirestore.getInstance()

        database2 = AppDatabase.getDatabase(requireActivity())
        dao = database2.appDao()


        binding.tvLoc.text=getStringData("adress","")
        binding.tvLocatee.setOnClickListener {

            showAddressInputDialog()

        }

        readFoods()
        readCategory()
        fetchAllMainDocuments()


//        getLocationName(39.961125, 66.484008)
//        binding.rvRestouran.adapter = adapter2

        return root
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
                        requireActivity(),
                        "Please fill in all fields.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    saveData("adress", addressInput.text.toString())
                    binding.tvLoc.text=getStringData("adress","")

                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()  // Close the dialog
            }

        // Show the dialog
        builder.show()
    }


    private fun fetchAllMainDocuments() {
        CoroutineScope(Dispatchers.Main).launch {
            val mainDocuments = readAllMainCollections()

            Log.e("Res125", mainDocuments.toString())
//
            val adapter = RestouranAdapter(
                requireActivity(),
                mainDocuments,
                object : RestouranAdapter.ItemSetOnClickListener {
                    override fun onClick(data: restouran_model) {

                        var intent = Intent(requireActivity(), RestouranActivity::class.java)

                        intent.putExtra("Res", data)

                        startActivity(intent)

                    }
                })
            binding.rvRestouran.adapter = adapter

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
                Log.e("Res125", mainDocSnapshot.toString())

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

    fun readFoods() {
        val userList = mutableListOf<food_model>()

        database.collection("main_food")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(food_model::class.java)
                    userList.add(user)
                    viewAdapter2(userList)
                }
                Log.d("Firestore", "All users: $userList")
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }


    fun readCategory() {
        val userList = mutableListOf<category_model2>()

        database.collection("category_food")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(category_model2::class.java)
                    userList.add(user)
                    viewAdapter(userList)
                }
                Log.d("Firestore", "All users: $userList")
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }

    fun viewAdapter2(list: MutableList<food_model>) {


        val adapter =
            FoodAdapter(requireActivity(), list, object : FoodAdapter.ItemSetOnClickListener {
                override fun onClick(data: food_model) {

                }
            })

        binding.rvFood.adapter = adapter


    }

    fun viewAdapter(list: MutableList<category_model2>) {


        val adapter =
            MenusAdapter(requireActivity(), list, object : MenusAdapter.ItemSetOnClickListener {
                override fun onClick(data: category_model2) {

                }
            })

        binding.rvCategory.adapter = adapter


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}