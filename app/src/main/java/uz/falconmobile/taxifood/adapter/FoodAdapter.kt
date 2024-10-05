package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import uz.falconmobile.taxifood.databinding.FoodLayout2Binding
import uz.falconmobile.taxifood.db.models.FavoriteFoods
import uz.falconmobile.taxifood.db.models.food_model2
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.model.food_model
import java.util.Locale

class FoodAdapter(
    val ids: List<String>,
    val context: Context,
    var items: MutableList<food_model2>,
    var listener: ItemSetOnClickListener,
    var listener2: ItemSetOnClickListener2,
) :
    RecyclerView.Adapter<FoodAdapter.Holder>() {


    private lateinit var dao: AppDao
    private lateinit var database: AppDatabase
    private lateinit var rate_view: TextView
    private lateinit var rate_count_view: TextView

    interface ItemSetOnClickListener {
        fun onClick(data: food_model2)
    }

    interface ItemSetOnClickListener2 {
        fun onClick(count: Int, data: food_model2)
    }


    inner class Holder(var view: FoodLayout2Binding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: food_model) {

            view.apply {


//                this.sekk.setOnTouchListener { _, _ ->
//                    true
//                }
                this.tvName.text = data.name
                this.tvPrice.text = "$ ${data.price}"
                this.tvDescription.text = data.description
                this.tvCountStar.text = "${data.rate_count}"
                this.tvStar.text = data.rate
                Glide.with(context).load(data.banner)
                    .into(this.ivFood)
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            FoodLayout2Binding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(
            binding
        )


    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.tvName.text = items[position].name
        var data = items[position]

        rate_view = holder.view.tvStar
        rate_count_view = holder.view.tvCountStar

        var count = 1

        rate_view.text = data.rate
        rate_count_view.text = "${data.rate_count} ratings"
        Glide.with(context).load(data.banner).into(holder.view.ivFood)
        holder.view.ivIsVeg.setImageResource(if (data.veg) R.drawable.ic_veg else R.drawable.ic_non_veg)
        holder.view.tvPrice.text = "$" + data.price
        holder.view.tvDescription.text = data.description
        holder.view.btnAdd.setOnClickListener {
            listener.onClick(items[position])
            holder.view.btnAdd.visibility = View.GONE
            holder.view.cvCounter.visibility = View.VISIBLE
        }


        holder.view.addButton.setOnClickListener {

            count++
            holder.view.foodCount.text = count.toString()
            listener2.onClick(count, data)
        }

        // Handle the remove button click
        holder.view.removeButton.setOnClickListener {
            if (count > 1) {
                count--
                holder.view.foodCount.text = count.toString()
                listener2.onClick(count, data)
            }
        }
        database = AppDatabase.getDatabase(context)
        dao = database.appDao()

        var check = false

        CoroutineScope(Dispatchers.Main).launch {

            check = doesRestaurantExist(items[position].name)

            if (check) {
                holder.view.ivSaved.setImageResource(R.drawable.baseline_bookmark_24)
                holder.view.tvSave.text = "Saved to eatlist"

            }
        }

        holder.view.btnRating.setOnClickListener {

            showRatingDialog(
                ids,
                data.rate.toDouble(),
                data.rate_count.toInt(),
                position,
                data.cat_id
            )

        }
        holder.view.btnWishlist.setOnClickListener {

            if (!check) {
                CoroutineScope(Dispatchers.Main).launch {
                    dao.insertFavoriteFood(
                        FavoriteFoods(
                            foodName = data.name,
                            image = data.banner,
                            price = data.price,
                            isVeg = data.veg,
                            isFavorite = true,
                            star = data.rate,
                            star_count = data.rate_count.toString(),
                            description = data.description,
                            restouran = data.restouran
                        )
                    )
                    holder.view.tvSave.text = "Saved to eatlist"
                    holder.view.ivSaved.setImageResource(R.drawable.baseline_bookmark_24)

                    check = true
                }


            } else {

                CoroutineScope(Dispatchers.Main).launch {
                    dao.deleteFoodByName(data.name)
                    check = false
                    holder.view.tvSave.text = "Save to eatlist"
                    holder.view.ivSaved.setImageResource(R.drawable.baseline_bookmark_border_24)


                }
            }

        }
    }

    fun updateDocument(
        collectionName: String,
        documentId: String,
        updates: Map<String, Any>,
        id2: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val db = FirebaseFirestore.getInstance()

        val docRef =
            db.collection(collectionName).document(id2).collection("foods")

                .document(documentId)
        val docRef2 =
            db.collection("main_foods").document(documentId)

        docRef2.update(updates)

        Log.e("testt", "updateDocument: $updates")
        Log.e("testt", "updateDocument: $docRef")

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

    fun updateList(newItems: MutableList<food_model2>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    private fun showRatingDialog(
        id: List<String>,
        rate1: Double,
        count1: Int,
        position2: Int,
        name: String,
    ) {
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
                    "rate_count" to count
                )

                updateDocument(
                    collectionName = "main_restaurants",
                    documentId = id[position2],
                    updates = updates,
                    id2 = name,
                    onSuccess = {
//
                        CoroutineScope(Dispatchers.Main).launch {
                            rate_view.text = String.format("%.1f", rate).toString()
                            rate_count_view.text = "$count ratings"
                        }
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


    override fun getItemCount(): Int = items.count()

}