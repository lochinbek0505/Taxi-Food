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
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.databinding.FruitBeverageLayoutBinding
import uz.falconmobile.taxifood.db.models.fruit_model
import java.util.Locale

class FruitAdapter(
    val ids: List<String>,
    val context: Context,
    var items: MutableList<fruit_model>,
    var listener: ItemSetOnClickListener,
    var listener2: ItemSetOnClickListener2
) :
    RecyclerView.Adapter<FruitAdapter.Holder>() {

    private lateinit var rate_view: TextView
//    private lateinit var rate_count_view: TextView

    interface ItemSetOnClickListener {
        fun onClick(data: fruit_model)
    }

    interface ItemSetOnClickListener2 {

        fun onClick(data: fruit_model, count: Int)

    }

    inner class Holder(var view: FruitBeverageLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(data: fruit_model) {

            view.apply {


                this.tvName.text = data.name
                this.tvPrice.text = "$ ${data.price} "
                this.tvQuanty.text = data.quanty
                if (data.tag1.isEmpty()) {
                    this.cvTag1.visibility = View.GONE
                }
                if (data.tag2.isEmpty()) {
                    this.cvTag2.visibility = View.GONE
                }
                this.tvTag1.text = data.tag1
                this.tvTag2.text = data.tag2
                Glide.with(context).load(data.banner)
                    .into(this.ivRestouran)
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            FruitBeverageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(
            binding
        )


    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        var count = 1
//        rate_view = holder.view.tvStar
//
//        holder.view.btnStar.setOnClickListener {
//            showRatingDialog(
//                item.rate.toDouble(),
//                item.rate_count.toInt(),
//                ids[position]
//            )
//
//        }
        holder.bind(item)

        holder.view.addButton.setOnClickListener {

            count++
            holder.view.foodCount.text = count.toString()
            listener2.onClick(item, count)
        }

        holder.view.removeButton.setOnClickListener {
            if (count > 1) {
                count--
                holder.view.foodCount.text = count.toString()
                listener2.onClick(item, count)
            }
        }

        holder.view.btnAdd.setOnClickListener {

            listener.onClick(item)

            holder.view.btnAdd.visibility = View.GONE
            holder.view.cvCounter.visibility = View.VISIBLE

        }
    }

    fun updateDocument(
        collectionName: String,
        updates: Map<String, Any>,
        id2: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val db = FirebaseFirestore.getInstance()

        val docRef =
            db.collection(collectionName).document(id2)


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

    fun updateList(newItems: MutableList<fruit_model>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    private fun showRatingDialog(
        rate1: Double,
        count1: Int,
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
                    collectionName = "fruits_vegetables",
                    updates = updates,
                    id2 = name,
                    onSuccess = {
//
//                        CoroutineScope(Dispatchers.Main).launch {
                        rate_view.text = String.format("%.1f", rate).toString()
//                        }
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


    override fun getItemCount(): Int = items.count()

}