package com.example.go4lunch.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.go4lunch.R
import com.example.go4lunch.model.Restaurant
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.restaurant_list_item.view.*
import java.util.ArrayList

/**
 * RecyclerView adapter for a list of Restaurants.
 */
class RestaurantRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mSnapshots = ArrayList<DocumentSnapshot>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(
                R.layout.restaurant_list_item,
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
        val restName = itemView.restaurant_name_textView
        val restAddress = itemView.restaurant_address_textView
        val restOpen = itemView.restaurant_opening_textView
        val restDistance = itemView.restaurant_distance_textView
        val restGoing = itemView.restaurant_numberOfGoing_textView
        val restImage = itemView.restaurant_imageView

        fun bind(snapshot: DocumentSnapshot) {
            val restaurant = snapshot.toObject(Restaurant::class.java)

            //Load image
            if (restaurant != null) {
                Glide.with(restImage.context)
                    .load(restaurant.photoUrl)
                    .into(restImage)
            }
            restName.text = restaurant?.name ?: "no name"
            restAddress.text = restaurant?.address ?: "no address"
            restOpen.text = restaurant?.opening ?: "no opening"
            //TODO : set distance logic
            restDistance.text = "100m"
            restGoing.text = restaurant?.going?.size.toString()
        }
    }
    fun updateItems(newList: List<DocumentSnapshot>) {
        mSnapshots.clear()
        mSnapshots.addAll(newList)
        notifyDataSetChanged()
    }

}

