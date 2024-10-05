package uz.falconmobile.taxifood.db.models

import java.io.Serializable


data class fruit_model(
    val name: String = "",
    val quanty: String = "",
    val banner: String = "",
    val price: String = "",
    val tag1: String = "",
    val tag2: String = "",
    val restouran: String="",

    ) : Serializable
