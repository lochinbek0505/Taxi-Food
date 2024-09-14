package uz.falconmobile.taxifood.model

import java.io.Serializable


data class restouran_model(

    val name: String = "",
    val banner: String = "",
    val rate: String = "",
    val rate_count: String = "",
    val description: String = "",
    val lenght: String = "",
    val location: String = "",
    val types_of_food: List<category_model> = listOf(),

    ) : Serializable
