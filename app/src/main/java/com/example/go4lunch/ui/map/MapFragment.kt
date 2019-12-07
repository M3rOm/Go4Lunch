package com.example.go4lunch.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.go4lunch.NewRestaurantActivity
import com.example.go4lunch.R
import com.example.go4lunch.model.Restaurant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore


const val REQUEST_LOCATION_PERMISSION = 1

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var markerID: String
    private lateinit var myMutableMap: MutableMap<Marker,Restaurant>
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity!!)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //Populate the map with restaurant markers
        addRestaurants()
        //Set custom marker click listener
        mMap.setOnMarkerClickListener {
            fun onMarkerClick(it: Marker): Boolean {
                Log.d("marker", "$it Marker clicked")
                return false
            }
            onMarkerClick(it)
        }
        markerID = ""
        try {
            /* Customize the styling of the base map using a JSON object defined
             in a raw resource file. */
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.activity, R.raw.map_style))
        } catch (
            e: Throwable
            //Resources.NotFoundException
        ) {
            Log.e("MAP", "Can't find style. Error: ", e)
        }
        // Move the camera to current position after granting permissions
        enableMyLocation()
        getLocation()
        //setMapLongClick(mMap)
        setPoiClick(mMap)
        //openNewRestaurantDialog(mMap)

    }

    private fun addRestaurants() {
        val restaurants = firestoreInstance.collection("restaurants")
        val orangeMarker = ContextCompat.getDrawable(context!!, R.drawable.ic_restmarkerorange)
        val listOfRestaurants = mutableListOf<Restaurant>()
        val customMarker = orangeMarker?.let { drawableToBitmap(it) }
        restaurants.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    listOfRestaurants.add(document.toObject(Restaurant::class.java))
                }
                for (i in 0 until listOfRestaurants.size) {
                    val pos =
                        listOfRestaurants[i].position?.latitude?.let {
                            listOfRestaurants[i].position?.longitude?.let { it1 ->
                                LatLng(
                                    it,
                                    it1
                                )
                            }
                        }
                        mMap.addMarker(pos?.let {
                        MarkerOptions().position(it).title(listOfRestaurants[i].name)
                            .icon(customMarker)
                    })
                }
            }
            .addOnFailureListener { exception ->
                Log.d("db", "Error getting documents: ", exception)
            }
    }

    private fun getLocation() {
        //Get the best and most recent estimate for device location. In rare occasions this can be null
        if (mMap.isMyLocationEnabled) {
            mFusedLocationClient.lastLocation.addOnSuccessListener { task ->
                var location: Location = task
                val locationLatLong = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLong, 14.0F))
            }
        }
    }

    private fun enableMyLocation() {
// This function checks for permissions, and requests it, if not yet granted.
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                enableMyLocation()
            }
        }
    }


    private fun openNewRestaurantDialog(map: GoogleMap) {
        //TODO : If you tap on a custom marker, which is not a POI, then markerID will be invalid.
        map.setOnInfoWindowClickListener { poi ->
            val idToPut = markerID
            val positionToPut = poi.position
            startActivity(
                Intent(context, NewRestaurantActivity::class.java).putExtra(
                    "restPlaceId",
                    idToPut
                ).putExtra("position", positionToPut)
            )
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        //Behaviour when a POI is clicked on the map.
        map.setOnPoiClickListener { poi ->
            markerID = poi.placeId
            val blueMarker = ContextCompat.getDrawable(context!!, R.drawable.ic_restmarkerblue)
            val customMarker = blueMarker?.let { drawableToBitmap(it) }
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(customMarker)
            )
            poiMarker.showInfoWindow()
            // TODO: Markers for clicked POIs should not show if not in database.
        }
    }
}

private fun drawableToBitmap(drawable: Drawable): BitmapDescriptor? {
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}