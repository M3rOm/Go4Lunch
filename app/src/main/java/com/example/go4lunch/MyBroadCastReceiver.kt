package com.example.go4lunch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("android.intent.action.BOOT_COMPLETED")){
            val serviceIntent = Intent(context, MyService::class.java)
            context?.startService(serviceIntent)
        } else {
            Toast.makeText(context?.applicationContext, "Alarm Manager just ran", Toast.LENGTH_SHORT).show()
        }
    }
}
