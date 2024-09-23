package uz.falconmobile.taxifood.db.models

import java.io.Serializable

data class food_model2(
    val name: String = "",
    val description: String = "",
    val banner: String = "",
    val price: String = "",
    val rate: String = "",
    val rate_count: Int = 0,
    val veg: Boolean = false,
    val cat_id: String = "",
) : Serializable