package uz.falconmobile.taxifood.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.MenusAdapter
import uz.falconmobile.taxifood.adapter.RestouranAdapter
import uz.falconmobile.taxifood.databinding.FragmentHomeBinding
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.restouran_model

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var database: FirebaseFirestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseFirestore.getInstance()


        readAllUsers()
        val list2 = arrayListOf<restouran_model>(

            restouran_model(
                "Botto the shake",
                R.drawable.food2,
                "4.4",
                "10-11 km",
                "India , Samarkand"
            ),

            restouran_model(
                "Botto the shake",
                R.drawable.food2,
                "4.4",
                "10-11 km",
                "India , Samarkand"
            ),

            restouran_model(
                "Botto the shake",
                R.drawable.food2,
                "4.4",
                "10-11 km",
                "India , Samarkand"
            ),

            restouran_model(
                "Botto the shake",
                R.drawable.food2,
                "4.4",
                "10-11 km",
                "India , Samarkand"
            ),

            restouran_model(
                "Botto the shake",
                R.drawable.food2,
                "4.4",
                "10-11 km",
                "India , Samarkand"
            ),

            restouran_model(
                "Botto the shake",
                R.drawable.food2,
                "4.4",
                "10-11 km",
                "India , Samarkand"
            ),

            restouran_model(
                "Botto the shake",
                R.drawable.food2,
                "4.4",
                "10-11 km",
                "India , Samarkand"
            ),

            )

        val adapter2 = RestouranAdapter(
            requireActivity(),
            list2,
            object : RestouranAdapter.ItemSetOnClickListener {
                override fun onClick(data: restouran_model) {

                }


            })

//        getLocationName(39.961125, 66.484008)
        binding.rvRestouran.adapter = adapter2

        return root
    }


    fun readAllUsers() {
        val userList = mutableListOf<category_model>()

        database.collection("category_food")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(category_model::class.java)
                    userList.add(user)
                    viewAdapter(userList)
                }
                Log.d("Firestore", "All users: $userList")
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }

    fun viewAdapter(list: MutableList<category_model>) {


        val adapter =
            MenusAdapter(requireActivity(), list, object : MenusAdapter.ItemSetOnClickListener {
                override fun onClick(data: category_model) {

                }
            })

        binding.rvCategory.adapter = adapter


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}