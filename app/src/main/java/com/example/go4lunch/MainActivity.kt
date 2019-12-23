package com.example.go4lunch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.go4lunch.messages.MessageEvent
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_host.*
import org.greenrobot.eventbus.EventBus


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        val sideNavView: NavigationView = findViewById(R.id.side_nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //Setup drawer
        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        //Add Sidebar Header
        val headerView =
            layoutInflater.inflate(R.layout.nav_header, sideNavView, false)
        sideNavView.addHeaderView(headerView)
        //Make profile info clickable, and open profilefragment
        headerView.setOnClickListener {
            nav_host_fragment.findNavController().navigate(R.id.profileFragment)
            drawer.closeDrawer(GravityCompat.START)
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.navigation_map_view, R.id.navigation_list_view, R.id.navigation_workmates
            )
        )
        appBarConfiguration.drawerLayout
        //Initiate suggestions for search textView
        val places = arrayListOf("Cameo", "KFC", "Tesco Restaurant", "Ali Baba", "Belaggio")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val textView = input_search as AutoCompleteTextView
        textView.setAdapter(adapter)
        //PLACES
        // Initialize the SDK
        Places.initialize(
            applicationContext,
            R.string.google_maps_key.toString()
        )
        // Create a new Places client instance
        val placesClient =
            Places.createClient(this)

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()
        // Create a RectangularBounds object.
        // Create a RectangularBounds object.
        val bounds = RectangularBounds.newInstance(
            LatLng(50.715279, -1.907633),
            LatLng(50.754868, -1.857648)
        )
        //Create a request for places autocomplete
        val request =
            FindAutocompletePredictionsRequest.builder()
                //.setLocationBias(bounds)
                .setLocationRestriction(bounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery("bel")
                .build()
        //Listen to the search button to start searching
        textView.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    places_autocomplete_search_bar.visibility = View.GONE
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    placesClient.findAutocompletePredictions(
                        FindAutocompletePredictionsRequest.builder()
                            //.setLocationBias(bounds)
                            .setLocationRestriction(bounds)
                            .setTypeFilter(TypeFilter.ESTABLISHMENT)
                            .setSessionToken(token)
                            .setQuery(v.text.toString())
                            .build()
                    )
                        .addOnSuccessListener { response ->
                            val foundEstablishments = ArrayList<String>()
                            for (prediction in response.autocompletePredictions) {
                                Log.i("PLACES", prediction.placeId)
                                foundEstablishments.add(prediction.placeId)
                            }
                            val newMessageToSend = MessageEvent(foundEstablishments)
                            EventBus.getDefault().post(newMessageToSend)
                        }
                        .addOnFailureListener { exception ->
                            if (exception is ApiException) {
                                Log.i("PLACES", "Place not found" + exception.statusCode)
                            }
                        }
                    true
                }
                else -> false
            }
        }

        //By calling this method, the title in the action bar will automatically
        // be updated when the destination changes
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
        sideNavView.setNavigationItemSelectedListener(this)
        //Places autocomplete
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_icon -> {
                if (places_autocomplete_search_bar.visibility != View.VISIBLE) {
                    places_autocomplete_search_bar.visibility = View.VISIBLE
                } else {
                    places_autocomplete_search_bar.visibility = View.GONE
                }
            }
        }
        return false
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_logout -> AuthUI.getInstance().signOut(this).addOnCompleteListener {
                startActivity(
                    Intent(this, SplashActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            R.id.nav_settings -> nav_host_fragment.findNavController().navigate(R.id.settingsFragment)
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
