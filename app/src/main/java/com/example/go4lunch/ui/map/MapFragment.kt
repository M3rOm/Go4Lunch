package com.example.go4lunch.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.go4lunch.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*

const val REQUEST_LOCATION_PERMISSION = 1

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /*try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        } catch (e: Resources.NotFoundException) {
            Log.e("MAP", "Can't find style. Error: ", e)
        }*/

        // Add a marker to home and move the camera
        val home = LatLng(50.734089, -1.879485)
        val zoom = 16.0F
        mMap.addMarker(MarkerOptions().position(home).title("This is your home"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoom))
        //setMapLongClick(mMap)
        //setPoiClick(mMap)
        //enableMyLocation()

    }
}