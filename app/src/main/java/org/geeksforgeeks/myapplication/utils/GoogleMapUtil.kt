package org.geeksforgeeks.myapplication.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class GoogleMapUtil(
    mapFragment: SupportMapFragment,
    private val onMapReady: (googleMap: GoogleMap) -> Unit = {},
    private val onClick: (latLng: LatLng) -> Unit = {}
) {

    private var map: GoogleMap? = null

    private val mapCallback = OnMapReadyCallback { googleMap: GoogleMap? ->
        googleMap?.let {
            map = it
        }
        map?.let {
            onMapReady.invoke(it)
        }
        map?.setOnMapClickListener {
            onClick(it)
        }
    }

    init {
        mapFragment.getMapAsync(mapCallback)
    }

    fun addMarker(latLong: LatLng, color: Float, isFocus: Boolean = false, zoomLevel: Float = 5f) {
        map?.addMarker(
            MarkerOptions().icon(
                BitmapDescriptorFactory.defaultMarker(
                    color
                )
            ).position(latLong)
        )
        isFocus.let {
            if (isFocus) {
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoomLevel))
            }
        }
    }

    fun clearMap() {
        map?.let {
            map?.clear()
        }
    }

    fun addPolyline(polylineOptions: PolylineOptions) {
        map?.addPolyline(polylineOptions)
    }
}