package uz.falconmobile.taxifood.db.models

import java.io.Serializable


data class fruit_model(
    val name: String = "",
    val weight: String = "",
    val banner: String = "",
    val price: String = "",
    val rate: String = "",
    val rate_count: Int = 0,
) : Serializable
