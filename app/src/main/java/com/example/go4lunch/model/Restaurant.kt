package com.example.go4lunch.model

import android.os.Parcel
import android.os.Parcelable

data class Restaurant (
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
    val website: String ="") : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readParcelable(CustomLatLng::class.java.classLoader),
        parcel.readString()?:"",
        parcel.createTypedArrayList(User),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readDouble(),
        parcel.readString()?:"",
        parcel.readString()?:""
    ) {
//    constructor():this ("","","",null,"",null,"","",0.0,"","")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(uid)
        parcel.writeString(address)
        parcel.writeParcelable(position, flags)
        parcel.writeString(opening)
        parcel.writeTypedList(going)
        parcel.writeString(phone)
        parcel.writeString(photoUrl)
        parcel.writeDouble(rating)
        parcel.writeString(type)
        parcel.writeString(website)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }
}
