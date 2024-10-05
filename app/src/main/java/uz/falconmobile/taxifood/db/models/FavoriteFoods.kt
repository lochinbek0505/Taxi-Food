package uz.falconmobile.taxifood.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_foods_table")
data class FavoriteFoods(
    @PrimaryKey(autoGenerate = true) val foodId: Int = 0,
    val isFavorite: Boolean,
    val foodName: String,
    val description: String,
    val image: String,
    val price: String,
    val star: String,
    val star_count: String,
    val isVeg: Boolean,
    val restouran: String,
)
