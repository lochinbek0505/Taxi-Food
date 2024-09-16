package uz.falconmobile.taxifood.db.models

import java.io.Serializable

data class restouran_id_model(

    val categoryId: String = "",
    val foodId: ArrayList<String> = arrayListOf()

) : Serializable