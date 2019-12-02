package com.example.go4lunch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.go4lunch.util.FirestoreUtil
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_new_restaurant.*

/**
 * This activity displays when user clicks on a pin's text bubble inside map view.
 * In this activity the user can add a restaurant to the database,
 * or in case it already exists, he/she can edit its details.
 */
class NewRestaurantActivity : AppCompatActivity() {
    private lateinit var position: LatLng
    private lateinit var placeId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_restaurant)
        // INTENT INFO: An intent with the place's name and another one with its position are being received.
        placeId = intent.getStringExtra("restPlaceId")
        val extras = intent.extras
        if (extras?.getParcelable<LatLng>("position") != null) {
            position = extras.getParcelable("position") ?: LatLng(-27.47093, 153.0235)
        }
        save_btn.setOnClickListener {
            //Save modifications on button click, and navigate back to map view
            // TODO: navigate back after closing this acticity.
            FirestoreUtil.updateRestaurant(placeId,
                new_restaurant_name_textView.text.toString(),
                new_restaurant_cuisine_textView.text.toString(),
                new_restaurant_address_textView.text.toString(),
                new_restaurant_phone_textView.text.toString(),
                new_restaurant_website_textView.text.toString()
            )
        }
        // TODO: Implement cancel button
    }

    override fun onStart() {
        super.onStart()
        //Load restaurant details into textViews
        val restaurant = FirestoreUtil.getRestaurant(placeId, position)
        restaurant_uid_textView.text = restaurant.uid
        new_restaurant_name_textView.setText(restaurant.name)
        new_restaurant_cuisine_textView.setText(restaurant.type)
        new_restaurant_address_textView.setText(restaurant.address)
        new_restaurant_phone_textView.setText(restaurant.phone)
        new_restaurant_website_textView.setText(restaurant.website)
    }  
}