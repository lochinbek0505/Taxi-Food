package uz.falconmobile.taxifood.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import uz.falconmobile.taxifood.adapter.FoodAdapter
import uz.falconmobile.taxifood.databinding.FragmentNotificationsBinding
import uz.falconmobile.taxifood.db.models.food_model2
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.model.food_model

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var dbHelper2: FoodItemDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper2 = FoodItemDatabaseHelper(requireActivity())
        readFoods()


        return root
    }

    fun readFoods() {

        var foodList = arrayListOf<food_model2>()

        var database = FirebaseFirestore.getInstance()

        val ids = arrayListOf<String>()
        database.collection("grossary ").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(food_model2::class.java)
                foodList.add(user)
                ids.add(document.id)
                viewAdapter2(foodList, ids)
            }
            Log.d("Firestore", "All users: $foodList")
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting documents: ", exception)
        }
    }


    fun viewAdapter2(list: MutableList<food_model2>, ids: ArrayList<String>) {


        var adapter5 =
            FoodAdapter(ids, requireActivity(), list, object : FoodAdapter.ItemSetOnClickListener {
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
                            requireActivity(), "Successfully added", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireActivity(), "Food added already", Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            })
        val layoutManager = LinearLayoutManager(requireActivity())

        binding.rvFood.layoutManager = layoutManager

        binding.rvFood.adapter = adapter5


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}