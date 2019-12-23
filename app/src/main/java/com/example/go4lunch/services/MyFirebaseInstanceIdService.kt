package com.example.go4lunch.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIdService : FirebaseMessagingService() {

    val TAG = "PushNotifService"
    lateinit var name: String

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "Token on this device: $p0")
    }

}