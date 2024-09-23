package uz.falconmobile.taxifood.db.utilits

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.falconmobile.taxifood.db.utilits.FruitDatabaseHelper.Companion
import uz.falconmobile.taxifood.model.requerment_model


// Create Database Helper class
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "requirements.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "requirements"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LAT = "lat"
        private const val COLUMN_LON = "lon"
        private const val COLUMN_X1 = "x1"
        private const val COLUMN_X2 = "x2"
        private const val COLUMN_DEL = "del"
        private const val COLUMN_TAX = "tax"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LAT + " TEXT,"
                + COLUMN_LON + " TEXT,"
                + COLUMN_X1 + " TEXT,"
                + COLUMN_X2 + " TEXT,"
                + COLUMN_DEL + " TEXT,"
                + COLUMN_TAX + " TEXT" + ")")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Function to add a new record
    fun addRequerment(model: requerment_model): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LAT, model.lat)
            put(COLUMN_LON, model.lon)
            put(COLUMN_X1, model.x1)
            put(COLUMN_X2, model.x2)
            put(COLUMN_DEL, model.del)
            put(COLUMN_TAX, model.tax)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Function to update an existing record by ID
    fun updateRequerment(id: Int, model: requerment_model): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LAT, model.lat)
            put(COLUMN_LON, model.lon)
            put(COLUMN_X1, model.x1)
            put(COLUMN_X2, model.x2)
            put(COLUMN_DEL, model.del)
            put(COLUMN_TAX, model.tax)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
    fun deleteFull() {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Delete all data from each table
            db.delete(
                FruitDatabaseHelper.TABLE_NAME,
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

    // Function to get a requirement by ID
    fun getRequerment(id: Int): requerment_model? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID,
                COLUMN_LAT,
                COLUMN_LON,
                COLUMN_X1,
                COLUMN_X2,
                COLUMN_DEL,
                COLUMN_TAX
            ),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val lat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAT))
            val lon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LON))
            val x1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_X1))
            val x2 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_X2))
            val del = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEL))
            val tax = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAX))
            cursor.close()
            requerment_model(lat, lon, x1, x2, del, tax)
        } else {
            null
        }
    }
}
