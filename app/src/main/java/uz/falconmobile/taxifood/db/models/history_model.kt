package uz.falconmobile.taxifood.db.models

data class history_model(

    val date: String="",
    val foods:ArrayList<String> = arrayListOf(),
    val subTotal:String="",
    val taxPrice:String="",
    val deliveryPriceval :String="",
    val total:String="",
    val phone:String="",
    val location:String="",

)
