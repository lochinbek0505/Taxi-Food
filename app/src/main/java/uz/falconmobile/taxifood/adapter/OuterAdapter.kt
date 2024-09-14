package uz.falconmobile.taxifood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.falconmobile.taxifood.databinding.OuterItemBinding
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.food_model

class OuterAdapter(
    private val items: List<category_model>,
    val listener: InnerAdapter.ItemSetOnClickListener
) :
    RecyclerView.Adapter<OuterAdapter.OuterViewHolder>() {

    inner class OuterViewHolder(val binding: OuterItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OuterViewHolder {
        val binding = OuterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OuterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OuterViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleTextView.text = item.type

        val innerAdapter =
            InnerAdapter(items[position].foods, object : InnerAdapter.ItemSetOnClickListener {
                override fun onClick(data: food_model) {
                    listener.onClick(data)
                }

            })
        holder.binding.innerRecyclerView.adapter = innerAdapter
        holder.binding.innerRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context)

        var isExpanded = false
        holder.binding.outerItemLayout.setOnClickListener {
            isExpanded = !isExpanded
            holder.binding.innerRecyclerView.visibility =
                if (isExpanded) View.VISIBLE else View.GONE
            holder.binding.dropdownIcon.rotation = if (isExpanded) 180f else 0f
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
