package uz.falconmobile.taxifood.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.MenusAdapter
import uz.falconmobile.taxifood.adapter.RestouranAdapter
import uz.falconmobile.taxifood.databinding.FragmentHomeBinding
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.restouran_model

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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

        val list = arrayListOf<category_model>(
            category_model(1, "Pizza", R.drawable.food1),
            category_model(1, "Binyani", R.drawable.food1),
            category_model(1, "Cake", R.drawable.food1),
            category_model(1, "Rolls", R.drawable.food1),
            category_model(1, "Chicken", R.drawable.food1),
            category_model(1, "Momos", R.drawable.food1),
            category_model(1, "Pizza", R.drawable.food1),
            category_model(1, "Binyani", R.drawable.food1),
            category_model(1, "Cake", R.drawable.food1),
            category_model(1, "Rolls", R.drawable.food1),
            category_model(1, "Chicken", R.drawable.food1),
            category_model(1, "Momos", R.drawable.food1)

        )

        val adapter =
            MenusAdapter(requireActivity(), list, object : MenusAdapter.ItemSetOnClickListener {
                override fun onClick(data: category_model) {

                }
            })

        binding.rvCategory.adapter = adapter


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

        binding.rvRestouran.adapter=adapter2

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}