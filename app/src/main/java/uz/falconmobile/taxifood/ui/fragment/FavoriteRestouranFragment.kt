package uz.falconmobile.taxifood.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.falconmobile.taxifood.adapter.FavoriteRestouranAdapter
import uz.falconmobile.taxifood.databinding.FragmentFavoriteRestouranBinding
import uz.falconmobile.taxifood.db.models.FavoriteRestaurants
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.ui.activity.FavoriteRestouranActivity

class FavoriteRestouranFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteRestouranAdapter

    private lateinit var database2: AppDatabase
    private var binding: FragmentFavoriteRestouranBinding? = null

    private lateinit var dao: AppDao
    private var foodList: MutableList<FavoriteRestaurants> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentFavoriteRestouranBinding.inflate(inflater, container, false)

        val root: View = binding!!.root
        database2 = AppDatabase.getDatabase(requireActivity())
        dao = database2.appDao()

        CoroutineScope(Dispatchers.Main).launch {
            foodList = dao.getAllFavoriteRestaurants().toMutableList()

            Log.d("TAGgg", "onCreateView: $foodList")

            showAdapter(foodList as ArrayList<FavoriteRestaurants>)
        }
        // Inflate the layout for this fragment
        return root

    }

    fun showAdapter(foodLIst: List<FavoriteRestaurants>) {

        recyclerView = binding!!.recyclerView
        // Initialize adapter
        adapter = FavoriteRestouranAdapter(
            requireActivity(),
            foodLIst.toMutableList(),
            object : FavoriteRestouranAdapter.ItemSetOnClickListener {
                override fun onClick(data: FavoriteRestaurants, position: Int) {

                    var intent = Intent(requireActivity(), FavoriteRestouranActivity::class.java)
                    intent.putExtra("id123", data.id)
                    startActivity(intent)

                }
            })


        binding!!.recyclerView.adapter = adapter

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
                        dao.deleteFavoriteRestaurant(foodLIst[position])
                        adapter.deleteItem(position)
                        adapter.notifyItemRemoved(position)
                    }
                }
            }

        // Attach ItemTouchHelper to RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding!!.recyclerView)


    }

}