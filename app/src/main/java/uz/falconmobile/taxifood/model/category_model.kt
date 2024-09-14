package uz.falconmobile.taxifood.model

import java.io.Serializable


data class category_model (

//    val id:Int=0,
    val type:String="",
    val foods:List<food_model> = listOf()

):Serializable