package uz.falconmobile.taxifood.model

data class food_model(
    val isFavorite: Boolean,
    val foodName: String,
    val description: String,
    val image: String,
    val price: String,
    val star: String,
    val star_count: String,
    val isVeg: Boolean
)
