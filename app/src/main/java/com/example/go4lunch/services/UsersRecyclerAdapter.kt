package com.example.go4lunch.services

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.go4lunch.MainActivity
import com.example.go4lunch.R
import com.example.go4lunch.model.Restaurant
import com.example.go4lunch.model.User
import com.example.go4lunch.util.FirestoreUtil
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.user_list_item.view.*


/**
 * RecyclerView adapter for a list of Users.
 */
class UsersRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mSnapshots = ArrayList<DocumentSnapshot>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(
                R.layout.user_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(getSnapshot(position))
        }
    }

    override fun getItemCount(): Int {
        return mSnapshots.size
    }

    private fun getSnapshot(index: Int): DocumentSnapshot {
        return mSnapshots[index]
    }

    //Creating a custom view holder, to reflect how my entries are going to look like
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView.imageView
        val userState = itemView.has_decided_TextView

        fun bind(snapshot: DocumentSnapshot) {
            val user = snapshot.toObject(User::class.java)

            if (user != null) {
                //Load image
                Glide.with(userImage.context)
                    .load(user.photo)
                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                    .into(userImage)
                val context = userImage.context
                //Load state
                if (user.decided) {
                    val text = context.getString(
                        R.string.user_decided,
                        user.firstName,
                        user.placeToEat?.type,
                        user.placeToEat?.name
                    )
                    userState.text = text
                } else {
                    val text = context.getString(R.string.user_not_decided, user.firstName)
                    userState.text = text
                }
            }
        }
    }

    fun updateItems(newList: List<DocumentSnapshot>) {
        mSnapshots.clear()
        mSnapshots.addAll(newList)
        notifyDataSetChanged()
    }
}


