package uz.falconmobile.taxifood.db.utilits

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.falconmobile.taxifood.model.food_model

class FoodItemDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "food_items.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "FoodItem"

        // Columns (without ID)
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_BANNER = "banner"
        const val COLUMN_PRICE = "price"
        const val COLUMN_RATE = "rate"
        const val COLUMN_RATE_COUNT = "rate_count"
        const val COLUMN_IS_VEG = "is_veg"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_NAME TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_BANNER TEXT,
                $COLUMN_PRICE TEXT,
                $COLUMN_RATE TEXT,
                $COLUMN_RATE_COUNT INTEGER,
                $COLUMN_IS_VEG INTEGER
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert (Add) Food Item
    fun addFoodItem(foodModel: food_model): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, foodModel.name)
            put(COLUMN_DESCRIPTION, foodModel.description)
            put(COLUMN_BANNER, foodModel.banner)
            put(COLUMN_PRICE, foodModel.price)
            put(COLUMN_RATE, foodModel.rate)
            put(COLUMN_RATE_COUNT, foodModel.rate_count)
            put(COLUMN_IS_VEG, if (foodModel.is_veg.toBoolean()) 1 else 0)
        }

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result // Returns the new row ID
    }

    // Read (Get) All Food Items
    fun getAllFoodItems(): List<food_model> {
        val foodItems = mutableListOf<food_model>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val foodItem = food_model(
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    banner = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BANNER)),
                    price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    rate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                    rate_count = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RATE_COUNT)),
                    is_veg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IS_VEG))
                )
                foodItems.add(foodItem)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return foodItems
    }

    // Update Food Item
    fun updateFoodItem(foodModel: food_model, name: String): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, foodModel.name)
            put(COLUMN_DESCRIPTION, foodModel.description)
            put(COLUMN_BANNER, foodModel.banner)
            put(COLUMN_PRICE, foodModel.price)
            put(COLUMN_RATE, foodModel.rate)
            put(COLUMN_RATE_COUNT, foodModel.rate_count)
            put(COLUMN_IS_VEG, foodModel.is_veg)
        }

        val result = db.update(
            TABLE_NAME, contentValues, "$COLUMN_NAME = ?", arrayOf(name)
        )
        db.close()
        return result // Returns the number of rows affected
    }

    // Delete Food Item
    fun deleteFoodItem(name: String): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_NAME = ?", arrayOf(name))
        db.close()
        return result // Returns the number of rows affected
    }
}
