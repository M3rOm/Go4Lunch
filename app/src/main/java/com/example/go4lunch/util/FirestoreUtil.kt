package com.example.go4lunch.util

import com.firebase.ui.auth.data.model.User
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
                currentUserDocRef.set(newUser).addOnCanceledListener {
                    onComplete()
                }
            } else
                onComplete()
        }
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