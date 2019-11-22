package com.example.go4lunch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.go4lunch.util.FirestoreUtil
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_new_restaurant.*

class NewRestaurantActivity : AppCompatActivity() {
    private lateinit var position : LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_restaurant)
        val extras = intent.extras
        if (extras?.getParcelable<LatLng>("position") != null) {
            position = extras.getParcelable("position")?: LatLng(-27.47093, 153.0235)
        }
    save_btn.setOnClickListener{
        //saveRestaurantDetails()
    }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.initRestaurant(position)
    }
}