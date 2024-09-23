package uz.falconmobile.taxifood.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import uz.falconmobile.taxifood.adapter.CatAdapter
import uz.falconmobile.taxifood.databinding.FragmentDashboardBinding
import uz.falconmobile.taxifood.model.category_model2
import uz.falconmobile.taxifood.model.food_change
import uz.falconmobile.taxifood.ui.activity.OpenCategoryActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var database: FirebaseFirestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        database = FirebaseFirestore.getInstance()
        readCategory()

        return root
    }

    fun readCategory() {
        val userList = mutableListOf<category_model2>()
        val ids = mutableListOf<String>()
        database.collection("main_restaurants").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(category_model2::class.java)
                userList.add(user)
                ids.add(document.id)
            }
            viewAdapter(userList, ids)

            Log.d("Firestore", "All users: $userList")
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting documents: ", exception)
        }
    }


    fun viewAdapter(list: MutableList<category_model2>, ids: MutableList<String>) {


        val adapter =
            CatAdapter(requireActivity(), list, object : CatAdapter.ItemSetOnClickListener {
                override fun onClick(data: category_model2, position: Int) {

                    val change = food_change(data.type, ids[position])
                    val intent = Intent(requireActivity(), OpenCategoryActivity::class.java)
                    intent.putExtra("change", change)
                    startActivity(intent)

                }
            })

        binding.rvCategory.adapter = adapter


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}