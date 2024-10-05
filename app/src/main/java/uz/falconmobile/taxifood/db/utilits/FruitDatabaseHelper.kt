package uz.falconmobile.taxifood.db.utilits

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.falconmobile.taxifood.db.models.fruit_model
import uz.falconmobile.taxifood.db.utilits.FoodItemDatabaseHelper.Companion.COLUMN_RATE_COUNT
import uz.falconmobile.taxifood.model.order_food_model

class FruitDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "fruit_items.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "FruitItem"

        // Columns
        const val COLUMN_NAME = "name"
        const val COLUMN_COUNT = "count"
        const val COLUMN_BANNER = "banner"
        const val COLUMN_PRICE = "price"
        const val RESTOURAN = "restouran"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_NAME TEXT PRIMARY KEY,
                $COLUMN_COUNT INTEGER,
                $COLUMN_BANNER TEXT,
                $COLUMN_PRICE TEXT,
                $RESTOURAN TEXT
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Check if a fruit item exists
    fun fruitItemExists(name: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME = ?",
            arrayOf(name)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    // Add a Fruit Item if it doesn't exist
    fun addFruitItem(fruit: order_food_model): Long {
        if (fruitItemExists(fruit.name)) {
            return -1L // Item already exists
        }

        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, fruit.name)
            put(COLUMN_COUNT, fruit.count)
            put(COLUMN_BANNER, fruit.imageUrl)
            put(COLUMN_PRICE, fruit.price)
            put(RESTOURAN, fruit.restouran)
        }

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result
    }

    // Get All Fruit Items
    fun getAllFruitItems(): List<order_food_model> {
        val fruitItems = mutableListOf<order_food_model>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val fruitItem = order_food_model(
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BANNER)),
                    price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    restouran = cursor.getString(cursor.getColumnIndexOrThrow(RESTOURAN))
                )
                fruitItems.add(fruitItem)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return fruitItems
    }

    // Update a Fruit Item
    fun updateFruitItem(fruit: order_food_model): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_COUNT, fruit.count)
            put(COLUMN_BANNER, fruit.imageUrl)
            put(COLUMN_PRICE, fruit.price)
            put(RESTOURAN, fruit.restouran)
            put(COLUMN_NAME,fruit.name)
        }

        val result = db.update(
            TABLE_NAME, contentValues, "$COLUMN_NAME = ?", arrayOf(fruit.name)
        )
        db.close()
        return result
    }

    fun deleteFull() {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Delete all data from each table
            db.delete(
                TABLE_NAME,
                null,
                null
            )  // Delete all rows from 'requirements' table

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Delete a Fruit Item
    fun deleteFruitItem(name: String): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_NAME = ?", arrayOf(name))
        db.close()
        return result
    }
}
