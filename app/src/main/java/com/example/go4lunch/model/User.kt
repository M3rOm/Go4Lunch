package com.example.go4lunch.model

class User(
    val firstName: String,
    val lastName : String,
    val email : String,
    val photo: String?,
    val decided: Boolean,
    val placeToEat: Restaurant?
) {
    constructor() : this("","","", null, false, null)
}