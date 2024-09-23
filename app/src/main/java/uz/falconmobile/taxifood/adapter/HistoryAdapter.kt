package uz.falconmobile.taxifood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.falconmobile.taxifood.databinding.ItemOrderHistoryBinding
import uz.falconmobile.taxifood.db.models.history_model

class HistoryAdapter(
    private val context: Context,
    private var orderHistoryList: List<history_model>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(private val binding: ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: history_model) {
            binding.apply {
                tvOrderDate.text = "Order Date: ${order.date}"
                tvOrderFoods.text = "Foods: ${order.foods.joinToString(", ")}"
                tvOrderSubTotal.text = "Subtotal: ${order.subTotal}"
                tvOrderTax.text = "Tax: ${order.taxPrice}"
                tvOrderDelivery.text = "Delivery: ${order.deliveryPriceval}"
                tvOrderTotal.text = "Total: ${order.total}"
                tvOrderPhone.text = "Phone: ${order.phone}"
                tvOrderLocation.text = "Location: ${order.location}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(orderHistoryList[position])
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    fun updateList(newList: List<history_model>) {
        this.orderHistoryList = newList
        notifyDataSetChanged()
    }
}
