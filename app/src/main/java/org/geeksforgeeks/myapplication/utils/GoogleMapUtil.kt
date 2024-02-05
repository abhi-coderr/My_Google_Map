package org.geeksforgeeks.myapplication.utils

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.clustering.ClusterManager
import org.geeksforgeeks.myapplication.network.model.MyItem
import org.geeksforgeeks.myapplication.utils.marker.CustomMarkerFactory
import kotlin.math.max

class GoogleMapUtil(
    mapFragment: SupportMapFragment
) {

    private var onMapReady: (googleMap: GoogleMap) -> Unit = {}
    var onClick: (latLng: LatLng) -> Unit = {}
    private var latLongList: ArrayList<MyItem> = ArrayList()

    // Declare a variable for the cluster manager.
    private lateinit var clusterManager: ClusterManager<MyItem?>

    private var map: GoogleMap? = null

    private val mapCallback = OnMapReadyCallback { googleMap: GoogleMap? ->
        googleMap?.let { map = it }
        map?.let { onMapReady.invoke(it) }
        map?.setOnMapClickListener { onClick(it) }
    }

    init {
        mapFragment.getMapAsync(mapCallback)
    }

    fun addMarker(
        latLong: LatLng,
        color: Float,
        isFocus: Boolean = false,
        zoomLevel: Float = 5f
    ) {
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
        map?.let { map?.clear() }
    }

    fun addPolyline(
        polylineOptions: PolylineOptions
    ) {
        map?.addPolyline(polylineOptions)
    }

    fun setUpCluster(latLong: LatLng, context: Context) {

        // Position the map.
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latLong.latitude,
                    latLong.longitude
                ), 10f
            )
        )
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(context, map)
        clusterManager.renderer = MyClusterRenderer(
            context, map,
//            CustomMarkerFactory(context).getMarkerBitmap(
//                max(1, latLongList.size).toString(),
//                CustomMarkerFactory.Type.Artist
//            ),
            CustomMarkerFactory(context).getMarkerBitmap(
                "1",
                CustomMarkerFactory.Type.Artist
            ),
            clusterManager
        )
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map?.setOnCameraIdleListener(clusterManager)
        map?.setOnMarkerClickListener(clusterManager)
        // Add cluster items (markers) to the cluster manager.
        val myItem =
            MyItem(latLong.latitude, latLong.longitude, "Title point", "Snippet point")
        latLongList.add(myItem)
        clusterManager.addItems(latLongList as Collection<MyItem?>?)
        clusterManager.setAnimation(true)
        clearMap()
        clusterManager.cluster()
    }

}

