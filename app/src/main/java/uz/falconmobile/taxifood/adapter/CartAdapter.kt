import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.model.order_food_model

class CartAdapter(
    private val context: Context,
    private var items: MutableList<order_food_model>,
    private val onTotalPriceCalculated: (Double) -> Unit,
    private val onItemRemoved: (Int) -> Unit // Callback when an item is removed
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    private lateinit var fruitHelper: FruitDatabaseHelper
    private lateinit var foodHelper: FoodItemDatabaseHelper

    // This will store the updated count for each item and track the total price
    private var totalCartPrice = 0.0

    init {
        // Calculate initial total price
        calculateTotalPrice()
    }

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val imageView: ImageView = view.findViewById(R.id.food_image)
        val nameTextView: TextView = view.findViewById(R.id.food_name)
        val priceTextView: TextView = view.findViewById(R.id.food_price)
        val countTextView: TextView = view.findViewById(R.id.food_count)

        val addButton: CardView = view.findViewById(R.id.add_button)
        val removeButton: CardView = view.findViewById(R.id.remove_button)

        fun bind(orderItem: order_food_model) {
            // Bind the data from the order item to the UI
            nameTextView.text = orderItem.name
            priceTextView.text = orderItem.price
            countTextView.text = orderItem.count.toString()
            // Assuming you have a way to load images (e.g., Glide or Picasso)
            Glide.with(imageView.context).load(orderItem.imageUrl).into(imageView)

            // Handle the add button click
            addButton.setOnClickListener {
                var currentCount = orderItem.count
                currentCount++
                orderItem.count = currentCount
                countTextView.text = currentCount.toString()
                calculateTotalPrice()
            }

            // Handle the remove button click
            removeButton.setOnClickListener {
                var currentCount = orderItem.count
                if (currentCount > 1) {
                    currentCount--
                    orderItem.count = currentCount
                    countTextView.text = currentCount.toString()
                    calculateTotalPrice()
                } else {

                    // Remove the item when count is zero
                    removeItem(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
        fruitHelper = FruitDatabaseHelper(context)
        foodHelper = FoodItemDatabaseHelper(context)
    }

    override fun getItemCount(): Int = items.size

    // Function to remove an item from the list
    fun removeItem(position: Int) {
        foodHelper.deleteFoodItem(items[position].name)
        fruitHelper.deleteFruitItem(items[position].name)
        items.removeAt(position)
        notifyItemRemoved(position)
        calculateTotalPrice()
        onItemRemoved(items.size)
    }

    // Function to calculate the total price based on item count and unit price
    private fun calculateTotalPrice() {
        totalCartPrice = items.sumOf { item ->
            item.count * item.price.toDouble()
        }
        // Notify the listener about the updated total price
        onTotalPriceCalculated(totalCartPrice)
    }
}
