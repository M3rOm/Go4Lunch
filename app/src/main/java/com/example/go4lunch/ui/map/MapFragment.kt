package com.example.go4lunch.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.go4lunch.NewRestaurantActivity
import com.example.go4lunch.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


const val REQUEST_LOCATION_PERMISSION = 1

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.go4lunch.R.layout.fragment_map, container, false)
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
        try {
            /* Customize the styling of the base map using a JSON object defined
             in a raw resource file. */
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.activity, R.raw.map_style))
        } catch (e: Throwable
        //Resources.NotFoundException
        ) {
            Log.e("MAP", "Can't find style. Error: ", e)
        }
        // Move the camera to current position after granting permissions
        enableMyLocation()
        getLocation()
        val home = LatLng(-27.47093, 153.0235)
        mMap.addMarker(MarkerOptions().position(home))
        //setMapLongClick(mMap)
        setPoiClick(mMap)
        openNewRestaurantDialog(mMap)

    }

    private fun getLocation() {
        //Get the best and most recent estimate for device location. In rare occasions this can be null
        if (mMap.isMyLocationEnabled) {
            mFusedLocationClient.lastLocation.addOnSuccessListener {task  ->
                var location : Location = task
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


    private fun openNewRestaurantDialog (map: GoogleMap) {
        map.setOnInfoWindowClickListener { poi ->
            val idToPut = poi.title
            val positionToPut = poi.position
            startActivity(Intent(context, NewRestaurantActivity::class.java).putExtra("restPlaceId", idToPut).putExtra("position",positionToPut))
        }
    }
    private fun setPoiClick(map: GoogleMap) {
        //Behaviour when a POI is clicked on the map.
        map.setOnPoiClickListener { poi ->
            map.clear()
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.placeId)
            // TODO: set title for poi.name after tests are done.
            )
            poiMarker.showInfoWindow()
            // TODO: Markers for clicked POIs should not show if not in database.
        }
    }
}