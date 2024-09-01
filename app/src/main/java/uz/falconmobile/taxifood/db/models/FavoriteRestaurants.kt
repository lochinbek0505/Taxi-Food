package uz.falconmobile.taxifood.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_restaurants_table")
data class FavoriteRestaurants(
    @PrimaryKey(autoGenerate = true) val restaurantId: Int = 0,
    val name: String = "",
    val image: String = "",
    val star: String = "",
    val lenght: String = "",
    val locate: String = "",
    val isFavorite: Boolean

)
