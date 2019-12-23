package com.example.go4lunch

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
//import com.example.go4lunch.glide.GlideApp
import com.example.go4lunch.util.FirestoreUtil
import com.example.go4lunch.util.StorageUtil
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    //When opened, it fetches the document of current user from database, and shows details to edit.

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.apply {
            profile_image_view.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select image"),
                    RC_SELECT_IMAGE
                )
            }
            save_btn.setOnClickListener {
                //updates user document in database
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(
                            first_name_textView.text.toString(),
                            last_name_textView.text.toString(),
                            email_textView.text.toString(),
                            imagePath
                        )
                    }
                else
                    FirestoreUtil.updateCurrentUser(
                        first_name_textView.text.toString(),
                        last_name_textView.text.toString(),
                        email_textView.text.toString(),
                        null
                    )
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // When an image is selected this transforms and shows in the ImageView.
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedImageBmp =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()
            Glide.with(this)
                .load(selectedImageBytes)
                .into(profile_image_view)
            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            if (this.isVisible) {
                first_name_textView.setText(user.firstName)
                last_name_textView.setText(user.lastName)
                email_textView.setText(user.email)
                Glide.with(this)
                    .load(user.photo)
                    .into(profile_image_view)

               /* if (!pictureJustChanged && user.photo != null && user.photo != "")
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(user.photo))
                        .into(profile_image_view)*/

            }
        }
    }

}