package com.example.go4lunch.util

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().uid
                ?: throw NullPointerException("UID is NULL")}"
        )
    private val restCollRef = firestoreInstance.collection("restaurants")
    private fun getCurrentRestaurantDocRef(pos: LatLng) {
val query = restCollRef.whereEqualTo("position", pos).limit(1)
        query.get().addOnSuccessListener {
            /** Here I want to create a list of the documents,
             * but it will be maximum one item because of the limit.
             * The reason I am doing this, is to get the document (if available)
             * which has the same position I initiated.
             * Later I can use this document to update its properties in NewRestaurantActivity.
             *
             */
        }
    }

    //Create entry in database
    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = com.example.go4lunch.model.User(
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "",
                    "",
                    "",
                    false,
                    null
                )
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }

    fun initRestaurant(pos: LatLng) {
        getCurrentRestaurantDocRef(pos)
        val restaurant =
            com.example.go4lunch.model.Restaurant(
                "",
                "",
                "",
                pos,
                "",
                null,
                "",
                "",
                0.0,
                "",
                ""
            )
        firestoreInstance.collection("restaurants")
            .add(restaurant)
            .addOnSuccessListener { documentReference ->
                Log.d("db", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("db", "Error adding document", e)

            }
    }



    fun updateRestaurant(
        name: String = "",
        type: String = "",
        address: String = "",
        phone: String = "",
        website: String =""
    ) {
        val restaurantFieldMap = mutableMapOf<String, String>()
        if (name.isNotBlank()) restaurantFieldMap["name"] = name
        if (type.isNotBlank()) restaurantFieldMap["type"] = type
        if (address.isNotBlank()) restaurantFieldMap["address"] = address
        if (phone.isNotBlank()) restaurantFieldMap["phone"] = phone
        if (website.isNotBlank()) restaurantFieldMap["website"] = website

    }

    fun updateCurrentUser(
        firstName: String = "",
        lastName: String = "",
        email: String = "",
        photo: String? = null
    ) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (firstName.isNotBlank()) userFieldMap["firstName"] = firstName
        if (lastName.isNotBlank()) userFieldMap["lastName"] = lastName
        if (email.isNotBlank()) userFieldMap["email"] = email
        if (photo != null) userFieldMap["photo"] = photo
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (com.example.go4lunch.model.User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(com.example.go4lunch.model.User::class.java)!!)
            }
    }
}