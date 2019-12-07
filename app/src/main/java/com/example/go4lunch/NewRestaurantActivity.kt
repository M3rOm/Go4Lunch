package com.example.go4lunch

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.go4lunch.model.CustomLatLng
import com.example.go4lunch.model.Restaurant
import com.example.go4lunch.util.FirestoreUtil
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_restaurant.*

/**
 * This activity displays when user clicks on a pin's text bubble inside map view.
 * In this activity the user can add a restaurant to the database,
 * or in case it already exists, he/she can edit its details.
 */
class NewRestaurantActivity : AppCompatActivity() {
    //lazy property means the value gets computed only upon first access
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
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
            FirestoreUtil.updateRestaurant(
                placeId,
                new_restaurant_name_textView.text.toString(),
                new_restaurant_cuisine_textView.text.toString(),
                new_restaurant_address_textView.text.toString(),
                new_restaurant_phone_textView.text.toString(),
                new_restaurant_website_textView.text.toString()
            )
            finish()
        }
        // TODO: Implement cancel button
    }

    override fun onStart() {
        super.onStart()
        val currentRestaurantDocRef: DocumentReference = firestoreInstance.document(
            "restaurants/$placeId"
        )
        currentRestaurantDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val restaurant =
                    Restaurant(
                        "",
                        placeId,
                        "",
                        CustomLatLng(position.latitude, position.longitude),
                        "",
                        null,
                        "",
                        "",
                        0.0,
                        "",
                        ""
                    )
                firestoreInstance.collection("restaurants").document(placeId)
                    .set(restaurant)
                    .addOnSuccessListener {
                        Log.d("db", "DocumentSnapshot successfully written")
                    }
                    .addOnFailureListener { e ->
                        Log.w("db", "Error adding document", e)
                    }

            } else {
                //convert the document snapshot into Restaurant object
                val restaurant = documentSnapshot.toObject(Restaurant::class.java)!!
                restaurant_uid_textView.text = restaurant.uid
                new_restaurant_name_textView.setText(restaurant.name)
                new_restaurant_cuisine_textView.setText(restaurant.type)
                new_restaurant_address_textView.setText(restaurant.address)
                new_restaurant_phone_textView.setText(restaurant.phone)
                new_restaurant_website_textView.setText(restaurant.website)
            }

        }

    }
}