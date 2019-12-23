package com.example.go4lunch

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.go4lunch.model.Restaurant
import com.example.go4lunch.util.FirestoreUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_restaurant_page.*

/**
 * This activity displays when user clicks on a custom pin's text bubble inside map view.
 * In this activity the user can set the restaurant as a place to have lunch,
 * open it's website, call the restaurant, and see who else is going
 */
class RestaurantActivity : AppCompatActivity() {
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    //lazy property means the value gets computed only upon first access
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var restaurant: Restaurant
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_page)
        restaurant = intent.getParcelableExtra("restaurant")
    }

    override fun onStart() {
        super.onStart()
        currentRestName_textView.text = restaurant.name
        currentRestType_textView.text =
            getString(R.string.restaurant_type_and_address, restaurant.type, restaurant.address)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, MyBroadCastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        if (restaurant.photoUrl != "") {
            //Load image
            Glide.with(this)
                .load(restaurant.photoUrl)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .into(currentRest_imageview)}

        floatingActionButton.setOnClickListener {
            //Mark user as going
            FirestoreUtil.updateCurrentUser(placeToEat = restaurant,decided = true)
            //Add the user to restaurant data
            FirestoreUtil.addUserToRestaurant(restaurant.uid)
            //TODO: delete alarm when Firebase notification had been implemented
            //startAlarm()
            finish()
        }


        /*val currentRestaurantDocRef: DocumentReference = firestoreInstance.document(
            "restaurants/${restaurant.uid}"
        )
        currentRestaurantDocRef.get().addOnSuccessListener { documentSnapshot ->

                //convert the document snapshot into Restaurant object
                val restaurant = documentSnapshot.toObject(Restaurant::class.java)!!
                restaurant_uid_textView.text = restaurant.uid
                new_restaurant_name_textView.setText(restaurant.name)
                new_restaurant_cuisine_textView.setText(restaurant.type)
                new_restaurant_address_textView.setText(restaurant.address)
                new_restaurant_phone_textView.setText(restaurant.phone)
                new_restaurant_website_textView.setText(restaurant.website)
            }*/

    }

    private fun startAlarm() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,0,pendingIntent)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ->
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,0, pendingIntent)
            else -> alarmManager.set(AlarmManager.RTC_WAKEUP,0,pendingIntent)
        }
    }

}