package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.falconmobile.taxifood.databinding.OuterItemBinding
import uz.falconmobile.taxifood.db.models.restouran_id_model
import uz.falconmobile.taxifood.db.models.transfer_array
import uz.falconmobile.taxifood.model.category_model
import uz.falconmobile.taxifood.model.food_model

class OuterAdapter(
    val context: Context,
    private var items: MutableList<category_model>,
    val list2: ArrayList<restouran_id_model>,
    val model: transfer_array,
    val listener: InnerAdapter.ItemSetOnClickListener,
    val listener2: InnerAdapter.ItemSetOnClickListener2,

    ) : RecyclerView.Adapter<OuterAdapter.OuterViewHolder>() {

    inner class OuterViewHolder(val binding: OuterItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OuterViewHolder {
        val binding = OuterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OuterViewHolder(binding)
    }

    fun updateList(newItems: MutableList<category_model>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: OuterViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleTextView.text = item.type

        val innerAdapter = InnerAdapter(
            context,
            items[position].foods,
            list2[position],
            object : InnerAdapter.ItemSetOnClickListener {
                override fun onClick(data: food_model) {
                    listener.onClick(data)
                }

            }, object : InnerAdapter.ItemSetOnClickListener2 {
                override fun onClick(data: food_model, count: Int) {
                    listener2.onClick(data,count)
                }

            },
            position,
            model
        )
        holder.binding.innerRecyclerView.adapter = innerAdapter
        holder.binding.innerRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context)

        var isExpanded = false
        holder.binding.outerItemLayout.setOnClickListener {
            isExpanded = !isExpanded
            holder.binding.innerRecyclerView.visibility =
                if (isExpanded) View.GONE else View.VISIBLE
            holder.binding.dropdownIcon.rotation = if (isExpanded) 0f else 180f
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
