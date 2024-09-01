package uz.falconmobile.taxifood.db.utilits

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.falconmobile.taxifood.db.models.FavoriteFoods
import uz.falconmobile.taxifood.db.models.FavoriteRestaurants
import uz.falconmobile.taxifood.db.models.UserData

@Database(
    entities = [UserData::class, FavoriteFoods::class, FavoriteRestaurants::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
