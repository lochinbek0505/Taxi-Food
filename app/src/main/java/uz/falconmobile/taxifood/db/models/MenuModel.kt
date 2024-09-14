package uz.falconmobile.taxifood.db.models

data class MenuModel(

    val type:String="",
    val food:List<FoodModel> = listOf()

)
