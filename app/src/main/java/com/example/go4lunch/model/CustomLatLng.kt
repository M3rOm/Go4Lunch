package com.example.go4lunch.model

import android.os.Parcel
import android.os.Parcelable

class CustomLatLng(
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    constructor() : this(0.0, 0.0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomLatLng> {
        override fun createFromParcel(parcel: Parcel): CustomLatLng {
            return CustomLatLng(parcel)
        }

        override fun newArray(size: Int): Array<CustomLatLng?> {
            return arrayOfNulls(size)
        }
    }

}
