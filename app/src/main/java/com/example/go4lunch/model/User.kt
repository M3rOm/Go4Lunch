package com.example.go4lunch.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    val firstName: String,
    val lastName : String,
    val email : String,
    val photo: String?,
    val decided: Boolean,
    val placeToEat: Restaurant?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Restaurant::class.java.classLoader)
    ) {
    }

    constructor() : this("","","", null, false, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(photo)
        parcel.writeByte(if (decided) 1 else 0)
        parcel.writeParcelable(placeToEat, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}