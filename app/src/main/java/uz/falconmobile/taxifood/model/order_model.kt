package uz.falconmobile.taxifood.model

data class order_model (

    val customerName:String="",

    val isConfirmed:Boolean=false,
    val location:String="",
    val latitute:Double=0.0,
    val longtute:Double=0.0,
    val orderId:String="",
    val orderTime:String="",
    val phone:String="",
    val subTotal:String="",
    val taxPrice:String="",
    val deliveryPrice:String="",
    val total:String="",
    val orderedFood:ArrayList<order_food_model> = arrayListOf(),


)