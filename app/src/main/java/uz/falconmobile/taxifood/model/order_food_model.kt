package uz.falconmobile.taxifood.model

data class order_food_model(

    var imageUrl: String = "",
    var name: String = "",
    val price: String = "",
    var count: Int = 0,
    var restouran: String = "",
)
