package uz.falconmobile.taxifood.model

//await firestore.collection('MAIN').doc("REQ").update({
//    'lat': lat,
//    'lon': long,
//    'x1': x1,
//    'x2': x2,
//    'del': del,
//    'tax': tax,
//});

data class requerment_model(

    val lat:String="",
    val lon:String="",
    val x1:String="",
    val x2:String="",
    val del:String="",
    val tax:String=""

)
