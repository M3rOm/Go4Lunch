package com.example.go4lunch.ui.restaurants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.go4lunch.R
import com.example.go4lunch.messages.MessageEvent
import com.example.go4lunch.model.Restaurant
import com.example.go4lunch.services.RestaurantRecyclerAdapter
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_restaurants.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

class RestaurantListFragment : Fragment() {

    private lateinit var restaurantRecyclerAdapter: RestaurantRecyclerAdapter
    private lateinit var myDocumentsArray: ArrayList<QueryDocumentSnapshot>
    private lateinit var mQuery: Query

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firestore and RecyclerView
        initRecyclerView()
        initFirestore()
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun initFirestore() {
        val mFirestore = FirebaseFirestore.getInstance()
        mQuery = mFirestore.collection("restaurants")
            .orderBy("name")
       mQuery.get().addOnSuccessListener { result ->
           myDocumentsArray = ArrayList()
           for (document in result) {
               Log.d("doc", "${document.id} => ${document.data}")
               myDocumentsArray.add(document)
           }
           updateUI(myDocumentsArray)
       }.addOnFailureListener{
           Log.e("doc", it.message)
       }
    }

    private fun updateUI(myDocumentsArray: ArrayList<QueryDocumentSnapshot>) {
restaurantRecyclerAdapter.updateItems(myDocumentsArray)
    }

    private fun initRecyclerView() {
        recycler_view_restaurants.apply {
            layoutManager = LinearLayoutManager(activity)
            restaurantRecyclerAdapter = RestaurantRecyclerAdapter()
            adapter = restaurantRecyclerAdapter
        }
    }
    //Handle eventBus message
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun transmitResult(event: MessageEvent) {
        //Comb together the two lists, delete those list items, which ID's are not on both.
        for (i in myDocumentsArray) {
            if (!event.message.contains(i.id)){
                myDocumentsArray.remove(i)
            }
        }
        //Re-generate list
        updateUI(myDocumentsArray)
    }
}