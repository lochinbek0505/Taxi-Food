package uz.falconmobile.taxifood.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import uz.falconmobile.taxifood.adapter.FoodAdapter
import uz.falconmobile.taxifood.databinding.FragmentNotificationsBinding
import uz.falconmobile.taxifood.db.models.food_model2
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.model.order_food_model

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var dbHelper: FruitDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = FruitDatabaseHelper(requireActivity())
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}