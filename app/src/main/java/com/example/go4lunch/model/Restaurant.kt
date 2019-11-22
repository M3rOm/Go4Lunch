package com.example.go4lunch.model

import com.google.android.gms.maps.model.LatLng

class Restaurant(
    val name: String,
    val uid: String,
    val address: String,
    val position: LatLng?,
    val opening: String,
    val going: List<User>?,
    val phone: String,
    val photoUrl: String,
    val rating: Double,
    val type: String,
    val website: String){
    constructor():this ("","","",null,"",null,"","",0.0,"","")
}
