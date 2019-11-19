package com.example.go4lunch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.go4lunch.util.FirestoreUtil
import com.facebook.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_screen.*
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 1
    private val signInProviders = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder()
            .setAllowNewAccounts(true)
            .build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        //Open AuthUI on button click
        google_login_btn.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(signInProviders)
                .build()
            startActivityForResult(intent, RC_SIGN_IN)
        }
        // Initialize Facebook Login button
        val btn = findViewById<LoginButton>(R.id.fb_login_btn)
        callbackManager = CallbackManager.Factory.create()

        btn.setReadPermissions("email", "public_profile")
        btn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("fb", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("fb", "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d("fb", "facebook:onError", error)
                // ...
            }
        })
        // ...

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
                //TODO: Indeterminate progress dialog?
                //TODO: Initialize current user in Firestore
                //val user = FirebaseAuth.getInstance().currentUser
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return
                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK ->
                        Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show()
                    ErrorCodes.UNKNOWN_ERROR ->
                        Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("fb", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("fb", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("fb", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

                // ...
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if (currentUser != null) updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Toast.makeText(this, "${currentUser.toString()} is logged in", Toast.LENGTH_SHORT).show()
    }

}