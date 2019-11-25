package com.example.go4lunch.model


class Restaurant(
    val name: String,
    val uid: String,
    val address: String,
    val position: CustomLatLng?,
    val opening: String,
    val going: List<User>?,
    val phone: String,
    val photoUrl: String,
    val rating: Double,
    val type: String,
    val website: String){
    constructor():this ("","","",null,"",null,"","",0.0,"","")
}
