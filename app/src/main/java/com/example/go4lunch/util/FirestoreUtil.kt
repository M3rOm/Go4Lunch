package com.example.go4lunch.util

import android.util.Log
import com.example.go4lunch.model.CustomLatLng
import com.example.go4lunch.model.Restaurant
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreUtil {
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference = firestoreInstance.document(
        "users/${FirebaseAuth.getInstance().uid
            ?: throw NullPointerException("UID is NULL")}"
    )

    private fun getCurrentRestaurantDocRef(id: String): DocumentReference {
        val currentRestaurantDocRef: DocumentReference = firestoreInstance.document(
            "restaurants/$id"
        )
        return currentRestaurantDocRef

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

    fun getRestaurant(placeId: String, position: LatLng): Restaurant {
        /** This function gets called first when a user opens it from map.
         * First it tries to get the restaurant's document from the database,
         * and if not found, creates a new entry with empty values, and returns it.
         * However if found, it creates a Restaurant object from the fetched document,
         * and returns it.
         */
        var restaurant = Restaurant()
        getCurrentRestaurantDocRef(placeId).get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                restaurant =
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
                firestoreInstance.collection("restaurants")
                    .document(placeId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            restaurant =
                                document.toObject(Restaurant::class.java)!!
                            Log.d("db", "DocumentSnapshot data: ${document.data}")
                        } else {
                            Log.d("db", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("db", "get failed with ", exception)
                    }

            }

        }
        /**
         * For some reason, it never returns the details from a document saved earlier.
         * It always returns empty values, like if it was just created
         * and also creates a new entry with the same details.
         */
        return restaurant
    }


    fun updateRestaurant(
        //This function updates the restaurant's document in the database with the new values typed in by he user.
        id: String,
        name: String = "",
        type: String = "",
        address: String = "",
        phone: String = "",
        website: String = ""
    ) {
        val restaurantFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) restaurantFieldMap["name"] = name
        if (type.isNotBlank()) restaurantFieldMap["type"] = type
        if (address.isNotBlank()) restaurantFieldMap["address"] = address
        if (phone.isNotBlank()) restaurantFieldMap["phone"] = phone
        if (website.isNotBlank()) restaurantFieldMap["website"] = website
        getCurrentRestaurantDocRef(id).update(restaurantFieldMap)
    }

    fun updateCurrentUser(
        //This function updates the user's document in the database with the new values typed in by he user.
        firstName: String = "",
        lastName: String = "",
        email: String = "",
        photo: String? = null,
        decided: Boolean = false,
        placeToEat: Restaurant? = null
    ) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (firstName.isNotBlank()) userFieldMap["firstName"] = firstName
        if (lastName.isNotBlank()) userFieldMap["lastName"] = lastName
        if (email.isNotBlank()) userFieldMap["email"] = email
        if (photo != null) userFieldMap["photo"] = photo
        if (decided) userFieldMap["decided"] = decided
        if (placeToEat != null) userFieldMap ["placeToEat"] = placeToEat
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (com.example.go4lunch.model.User) -> Unit) {
        //Fetches the document for the currently logged on user.
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(com.example.go4lunch.model.User::class.java)!!)
            }
    }

    fun addUserToRestaurant(uid: String) {
        getCurrentRestaurantDocRef(uid).update("going", currentUserDocRef)
            .addOnSuccessListener { Log.d("FIRESTORE", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("FIRESTORE", "Error updating document", e) }
    }

}