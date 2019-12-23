package com.example.go4lunch.ui.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.go4lunch.R
import com.example.go4lunch.services.RestaurantRecyclerAdapter
import com.example.go4lunch.services.UsersRecyclerAdapter
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_restaurants.*
import java.util.ArrayList

class UserListFragment : Fragment() {

    private lateinit var userRecyclerAdapter: UsersRecyclerAdapter
    private lateinit var mQuery: Query
    private lateinit var userListenerRegistration: ListenerRegistration

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

    private fun initFirestore() {
        val mFirestore = FirebaseFirestore.getInstance()
        mQuery = mFirestore.collection("users")
            .orderBy("firstName")
       mQuery.get().addOnSuccessListener { result ->
           var myDocumentsArray : ArrayList<QueryDocumentSnapshot> = ArrayList()
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
userRecyclerAdapter.updateItems(myDocumentsArray)
    }

    private fun initRecyclerView() {
        recycler_view_restaurants.apply {
            layoutManager = LinearLayoutManager(activity)
            userRecyclerAdapter = UsersRecyclerAdapter()
            adapter = userRecyclerAdapter
        }
    }
}