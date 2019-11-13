package com.example.go4lunch.model

class Restaurant(
    val name: String,
    val address: String,
    val opening: String,
    val going: List<User>?,
    val phone: String,
    val photoUrl: String,
    val rating: Double,
    val type: String,
    val website: String){
    constructor():this ("","","",null,"","",0.0,"","")
}
