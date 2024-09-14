package uz.falconmobile.taxifood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.falconmobile.taxifood.databinding.FoodLayoutBinding
import uz.falconmobile.taxifood.model.food_model

//var listener: ItemSetOnClickListener,
//
//) :
//RecyclerView.Adapter<RestouranAdapter.Holder>() {
//


class InnerAdapter(private val items: List<food_model>, var listener: ItemSetOnClickListener) :
    RecyclerView.Adapter<InnerAdapter.InnerViewHolder>() {


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

        holder.binding.btnAdd.setOnClickListener {
            listener.onClick(items[position])
        }
//        holder.itemView.setOnClickListener {
//            listener.onClick(items[position])
//        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}
