package uz.falconmobile.taxifood

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import uz.falconmobile.taxifood.db.models.history_model
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper
import uz.falconmobile.taxifood.db.utilits.HisotiryDatabaseHelper
import uz.falconmobile.taxifood.model.order_model

class OrderRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ordersRef: DatabaseReference = database.getReference("orders")

    private lateinit var fruitHelper: FruitDatabaseHelper
    private lateinit var foodHelper: FoodItemDatabaseHelper
    fun restartActivity(context: Context) {
        if (context is Activity) {
            // Finish the current activity
            context.finish()

            // Restart the activity with the same intent
            val intent = context.intent
            context.startActivity(intent)
        }
    }

    // Function to write the order to Firebase
    fun writeOrderToFirebase(order: order_model, context: Context) {
        // Use the order ID as the key for each order
        val orderKey = ordersRef.push().key ?: return
        fruitHelper = FruitDatabaseHelper(context)
        foodHelper = FoodItemDatabaseHelper(context)
        val dbHelper = HisotiryDatabaseHelper(context)
        var foods = arrayListOf<String>()

        for (c in order.orderedFood) {

            foods.add(c.name)

        }

        val data = history_model(
            order.orderTime,
            foods,
            order.subTotal,
            order.taxPrice,
            order.deliveryPrice,
            order.total,
            order.phone,
            order.location
        )

        // Set the order in the database under the unique order ID
        ordersRef.child(order.orderId).setValue(order)
            .addOnSuccessListener {
                // Handle success
                fruitHelper.deleteFull()
                foodHelper.deleteFull()
                dbHelper.saveHistory(data)
                Toast.makeText(context, "Ordered successfully !!", Toast.LENGTH_SHORT).show()
                restartActivity(context)
                Log.d("OrderRepository12", "Order written to Firebase successfully")
            }

            .addOnFailureListener {
                // Handle failure
                Toast.makeText(context, "Error ${it.message}", Toast.LENGTH_SHORT).show()

                Log.e("OrderRepository12", "Failed to write order: ${it.message}")
                println("Failed to write order: ${it.message}")
            }
    }
}
