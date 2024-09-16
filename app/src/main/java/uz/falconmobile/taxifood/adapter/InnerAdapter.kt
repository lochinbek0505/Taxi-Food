package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.databinding.FoodLayoutBinding
import uz.falconmobile.taxifood.db.models.FavoriteFoods
import uz.falconmobile.taxifood.db.models.restouran_id_model
import uz.falconmobile.taxifood.db.models.transfer_array
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.model.food_model
import java.util.Locale

//var listener: ItemSetOnClickListener,
//
//) :
//RecyclerView.Adapter<RestouranAdapter.Holder>() {
//


class InnerAdapter(
    val context: Context,
    private val items: List<food_model>,
    val list: restouran_id_model,
    var listener: ItemSetOnClickListener,
    val position1: Int,
    var model: transfer_array
) :
    RecyclerView.Adapter<InnerAdapter.InnerViewHolder>() {

    private lateinit var dao: AppDao
    private lateinit var database: AppDatabase
    private lateinit var rate_view: TextView
    private lateinit var rate_count_view: TextView

    interface ItemSetOnClickListener {
        fun onClick(data: food_model)
    }


    inner class InnerViewHolder(val binding: FoodLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val binding = FoodLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.binding.tvName.text = items[position].name
        var data = items[position]

        rate_view = holder.binding.tvStar
        rate_count_view = holder.binding.tvCountStar


        rate_view.text = data.rate
        rate_count_view.text = "${data.rate_count} ratings"
        Glide.with(context).load(data.banner).into(holder.binding.ivFood)
        holder.binding.ivIsVeg.setImageResource(if (data.is_veg.toBoolean()) R.drawable.ic_veg else R.drawable.ic_non_veg)
        holder.binding.tvPrice.text = "$" + data.price
        holder.binding.tvDescription.text = data.description
        holder.binding.btnAdd.setOnClickListener {
            listener.onClick(items[position])
        }


        database = AppDatabase.getDatabase(context)
        dao = database.appDao()

        var check = false

        CoroutineScope(Dispatchers.IO).launch {

            check = doesRestaurantExist(items[position].name)

            if (check) {
                holder.binding.tvSave.text = "Saved to eatlist"

            }
        }

        holder.binding.btnRating.setOnClickListener {

            showRatingDialog(model, data.rate.toDouble(), data.rate_count.toInt(), position)

        }
        holder.binding.btnWishlist.setOnClickListener {

            if (!check) {
                CoroutineScope(Dispatchers.Main).launch {
                    dao.insertFavoriteFood(
                        FavoriteFoods(
                            foodName = data.name,
                            image = data.banner,
                            price = data.price,
                            isVeg = data.is_veg.toBoolean(),
                            isFavorite = true,
                            star = data.rate,
                            star_count = data.rate_count.toString(),
                            description = data.description,
                        )
                    )
                    holder.binding.tvSave.text = "Saved to eatlist"
                    check = true
                }


            } else {

                CoroutineScope(Dispatchers.Main).launch {
                    dao.deleteFoodByName(data.name)
                    check = false
                    holder.binding.tvSave.text = "Save to eatlist"


                }
            }

        }
//        holder.itemView.setOnClickListener {
//            listener.onClick(items[position])
//        }

    }

    fun updateDocument(
        collectionName: String,
        documentId: transfer_array,
        updates: Map<String, Any>,
        position1: Int,
        position2: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val db = FirebaseFirestore.getInstance()

        val docRef =
            db.collection(collectionName).document(documentId.resId).collection("types_of_food")
                .document(
                    documentId.ids.get(position1).categoryId.toString()
                ).collection("foods")
                .document(documentId.ids.get(position1).foodId[position2].toString())


        // Update the document with the provided fields
        docRef.update(updates)
            .addOnSuccessListener {

                onSuccess()

            }
            .addOnFailureListener { exception ->
                // Call the failure callback if update fails
                onFailure(exception)
            }
    }

    private fun showRatingDialog(id: transfer_array, rate1: Double, count1: Int, position2: Int) {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rating, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        var rate = rate1
        var count = count1


        // Create and show the AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setTitle("Rate Us")
            .setView(dialogView)
            .setPositiveButton("Submit") { dialogInterface, _ ->
                val rating = ratingBar.rating


                rate = (rate * count + rating) / (count + 1)
                count++
                // Define the fields to update
                val updates = mapOf<String, Any>(
                    "rate" to String.format(Locale.US, "%.1f", rate).toString(),
                    "rate_count" to count.toString()
                )

                updateDocument(
                    collectionName = "restaurants",
                    documentId = id,
                    updates = updates,
                    position1 = position1,
                    position2 = position2,
                    onSuccess = {
//
                        rate_view.text = String.format("%.1f", rate).toString()
                        rate_count_view.text = "$count ratings"
                        Log.d("Firestoress", "Document successfully updated!")
                    },
                    onFailure = { exception ->
                        Log.w("Firestoress", "Error updating document", exception)
                    }
                )

                // Handle the rating submission
//                Toast.makeText(this, "You rated: $rating stars", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    suspend fun doesRestaurantExist(name: String): Boolean {
        return dao.isFoodExists(name) > 0
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
