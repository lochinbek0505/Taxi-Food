package uz.falconmobile.taxifood.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.falconmobile.taxifood.adapter.FoodAdapter
import uz.falconmobile.taxifood.databinding.ActivityOpenCategoryBinding
import uz.falconmobile.taxifood.model.food_change
import uz.falconmobile.taxifood.model.food_model

class OpenCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpenCategoryBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val db = FirebaseFirestore.getInstance()

        val data = intent.getSerializableExtra("change") as food_change

        binding.tvTitle.text = data.title
        CoroutineScope(Dispatchers.Main).launch {
            try {


                val subCollection2Snapshots = db.collection("main_restaurants")
                    .document(data.id)
                    .collection("foods")
                    .get()
                    .await()

                val subCollection2List = mutableListOf<food_model>()
                for (subDocSnapshot2 in subCollection2Snapshots) {

                    val subDocument2 = subDocSnapshot2.toObject(food_model::class.java)
                    subCollection2List.add(subDocument2)

                }
                Log.e("SALOM", subCollection2List.toString())
                viewAdapter2(subCollection2List)
            } catch (e: Exception) {
                Log.e("SALOM", e.toString())

            }
        }

    }

    fun viewAdapter2(list: MutableList<food_model>) {


        val adapter =
            FoodAdapter(this, list, object : FoodAdapter.ItemSetOnClickListener {
                override fun onClick(data: food_model) {

                }
            })

        binding.rvFood.adapter = adapter


    }


}