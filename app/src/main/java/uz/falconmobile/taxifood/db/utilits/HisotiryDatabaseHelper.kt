package uz.falconmobile.taxifood.db.utilits

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import uz.falconmobile.taxifood.db.models.history_model

class HisotiryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "taxifood.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_HISTORY = "history"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_FOODS = "foods"
        private const val COLUMN_SUBTOTAL = "subTotal"
        private const val COLUMN_TAX_PRICE = "taxPrice"
        private const val COLUMN_DELIVERY_PRICE = "deliveryPriceval"
        private const val COLUMN_TOTAL = "total"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_LOCATION = "location"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the history table
        val createTable = ("CREATE TABLE $TABLE_HISTORY ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_DATE TEXT, "
                + "$COLUMN_FOODS TEXT, "  // Store foods as a JSON string
                + "$COLUMN_SUBTOTAL TEXT, "
                + "$COLUMN_TAX_PRICE TEXT, "
                + "$COLUMN_DELIVERY_PRICE TEXT, "
                + "$COLUMN_TOTAL TEXT, "
                + "$COLUMN_PHONE TEXT, "
                + "$COLUMN_LOCATION TEXT)"
                )
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }

    // Insert a new history record
    fun saveHistory(history: history_model): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_DATE, history.date)
        values.put(COLUMN_FOODS, Gson().toJson(history.foods))  // Convert foods list to JSON string
        values.put(COLUMN_SUBTOTAL, history.subTotal)
        values.put(COLUMN_TAX_PRICE, history.taxPrice)
        values.put(COLUMN_DELIVERY_PRICE, history.deliveryPriceval)
        values.put(COLUMN_TOTAL, history.total)
        values.put(COLUMN_PHONE, history.phone)
        values.put(COLUMN_LOCATION, history.location)

        val success = db.insert(TABLE_HISTORY, null, values)
        db.close()
        return success
    }

    // Read all history records
    fun getAllHistory(): ArrayList<history_model> {
        val historyList: ArrayList<history_model> = arrayListOf()
        val selectQuery = "SELECT * FROM $TABLE_HISTORY"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val history = history_model(
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    foods = Gson().fromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOODS)), ArrayList::class.java) as ArrayList<String>,  // Convert JSON back to ArrayList
                    subTotal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBTOTAL)),
                    taxPrice = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAX_PRICE)),
                    deliveryPriceval = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELIVERY_PRICE)),
                    total = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
                )
                historyList.add(history)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return historyList
    }

    // Update a history record by ID
    fun updateHistory(id: Int, history: history_model): Int {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_DATE, history.date)
        values.put(COLUMN_FOODS, Gson().toJson(history.foods))  // Convert foods list to JSON string
        values.put(COLUMN_SUBTOTAL, history.subTotal)
        values.put(COLUMN_TAX_PRICE, history.taxPrice)
        values.put(COLUMN_DELIVERY_PRICE, history.deliveryPriceval)
        values.put(COLUMN_TOTAL, history.total)
        values.put(COLUMN_PHONE, history.phone)
        values.put(COLUMN_LOCATION, history.location)

        val success = db.update(TABLE_HISTORY, values, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }

    // Delete a history record by ID
    fun deleteHistory(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_HISTORY, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }

    // Delete all history records
    fun deleteAllHistory(): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_HISTORY, null, null)
        db.close()
        return success
    }
}
