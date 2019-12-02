package com.example.go4lunch.model


data class Restaurant(
    val name: String = "",
    val uid: String = "",
    val address: String = "",
    val position: CustomLatLng? = null,
    val opening: String = "",
    val going: List<User>? = null,
    val phone: String = "",
    val photoUrl: String = "",
    val rating: Double = 0.0,
    val type: String = "",
    val website: String =""){
//    constructor():this ("","","",null,"",null,"","",0.0,"","")
}
