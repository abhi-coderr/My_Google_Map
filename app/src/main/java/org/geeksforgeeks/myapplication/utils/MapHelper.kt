package org.geeksforgeeks.myapplication.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapHelper(
    var map: GoogleMap,
    val onClick: (LatLng) -> Unit
) : OnMapReadyCallback {

    override fun onMapReady(p0: GoogleMap?) {

        if (p0 != null) {
            map = p0
        }

        map.setOnMapClickListener {latLong->
            onClick(latLong)
        }

    }

    fun addOnMarker(latLong: LatLng) {

        map.addMarker(
            MarkerOptions().icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN
                )
            ).position(latLong)
        )

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latLong.latitude, latLong.longitude),
                10f
            )
        )

    }

}