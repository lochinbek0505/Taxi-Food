package uz.falconmobile.taxifood.db.models

data class RestouranModel (

//    'latitude': latitude,
//    'longtitude': longtitude,
    val name: String = "",
    val image: String = "",
    val star: String = "",
    val latitude: String = "",
    val longtitude: String = "",
    val distance: String = "",
    val locate: String = "",
    val isFavorite: Boolean

)