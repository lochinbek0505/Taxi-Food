package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.databinding.FavoriteFoodBinding
import uz.falconmobile.taxifood.db.models.FavoriteFoods

class WIshlistAdapter(
    val context: Context,
    var items: MutableList<FavoriteFoods>,
    var listener: ItemSetOnClickListener
) : RecyclerView.Adapter<WIshlistAdapter.Holder>() {

    interface ItemSetOnClickListener {
        fun onClick(data: FavoriteFoods)
    }

    inner class Holder(var view: FavoriteFoodBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(data: FavoriteFoods) {
            view.apply {
                this.tvName.text = data.foodName
                this.tvPrice.text = "$ ${data.price}"
                this.tvDescription.text = data.description
                this.tvCountStar.text = "( ${data.star_count} )"
                this.tvStar.text = data.star
                Glide.with(context).load(data.image).into(this.ivFood)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            FavoriteFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        var btn = holder.itemView.findViewById(R.id.btn_add) as MaterialButton

        holder.bind(item)
        btn.setOnClickListener {
            listener.onClick(item)
        }
    }

    override fun getItemCount(): Int = items.count()

    // Function to remove an item from the list
    fun deleteItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}
