package uz.falconmobile.taxifood.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.falconmobile.taxifood.adapter.WIshlistAdapter
import uz.falconmobile.taxifood.databinding.FragmentFavoriteFoodBinding
import uz.falconmobile.taxifood.db.models.FavoriteFoods
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.model.order_food_model


class FavoriteFoodFragment : Fragment() {
    private var _binding: FragmentFavoriteFoodBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WIshlistAdapter
    private var foodList: MutableList<FavoriteFoods> = mutableListOf()
    lateinit var dbHelper: FruitDatabaseHelper

    private lateinit var database2: AppDatabase

    private lateinit var dao: AppDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteFoodBinding.inflate(inflater, container, false)

        val root: View = _binding!!.root
        // Inflate the layout for this fragment
        dbHelper = FruitDatabaseHelper(requireActivity())


        database2 = AppDatabase.getDatabase(requireActivity())
        dao = database2.appDao()
        CoroutineScope(Dispatchers.Main).launch {
            foodList = dao.getAllFavoriteFoods().toMutableList()

            showAdapter(foodList as ArrayList<FavoriteFoods>)
        }




        return root
    }

    fun showAdapter(foodLIst: ArrayList<FavoriteFoods>) {

        // Initialize adapter
        adapter = WIshlistAdapter(
            requireActivity(),
            foodLIst,
            object : WIshlistAdapter.ItemSetOnClickListener {
                override fun onClick(data: FavoriteFoods) {
                    // Handle click events

                    if (dbHelper.addFruitItem(
                            order_food_model(
                                name = data.foodName,
                                count = 1,
                                imageUrl = data.image,
                                price = data.price,
                                restouran = data.restouran,

                                )
                        ) != -1L
                    ) {


                    }

                }
            }, object : WIshlistAdapter.ItemSetOnClickListener2 {
                override fun onClick(data: FavoriteFoods, count: Int) {


                    if (dbHelper.updateFruitItem(
                            order_food_model(
                                name = data.foodName,
                                count = count,
                                imageUrl = data.image,
                                price = data.price,
                                restouran = data.restouran,

                                )
                        ) != -1
                    ) {


                    }
                }
            })

        _binding!!.recyclerView.adapter = adapter

        // Implement swipe-to-delete functionality
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition

                    CoroutineScope(Dispatchers.Main).launch {
                        dao.deleteFavoriteFood(foodLIst[position])
                        adapter.deleteItem(position)
                        adapter.notifyItemRemoved(position)
                    }
                }
            }

        // Attach ItemTouchHelper to RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(_binding!!.recyclerView)


    }

}