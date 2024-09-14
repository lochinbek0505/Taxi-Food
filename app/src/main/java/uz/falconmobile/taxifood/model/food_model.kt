package uz.falconmobile.taxifood.model

import java.io.Serializable

data class food_model(
    val name: String = "",
    val description: String = "",
    val banner: String = "",
    val price: String = "",
    val rate: String = "",
    val rate_count: Long = 0,
    val is_veg: Boolean = false
) : Serializable
