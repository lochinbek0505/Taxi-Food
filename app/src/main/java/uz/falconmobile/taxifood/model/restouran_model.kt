package uz.falconmobile.taxifood.model

import java.io.Serializable


data class restouran_model(

    val name: String = "",
    val banner1: String = "",
    val banner2: String = "",
    val banner3: String = "",
    val rate: String = "",
    val rate_count: String = "",
    val description: String = "",
    val latitude: String = "",
    val longtitude: String = "",
    var distance: String = "",
    val location: String = "",
    val types_of_food: List<category_model> = listOf(),

    ) : Serializable
