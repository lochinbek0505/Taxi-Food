package uz.falconmobile.taxifood.db.models

import java.io.Serializable

data class transfer_array(

    val resId: String,
    val ids: ArrayList<restouran_id_model>

):Serializable
