package org.geeksforgeeks.myapplication.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import org.geeksforgeeks.myapplication.R
import org.geeksforgeeks.myapplication.databinding.ActivityMainBinding
import org.geeksforgeeks.myapplication.ui.viewmodel.MapViewModel
import org.geeksforgeeks.myapplication.utils.Const.Companion.API_KEY

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var mapFragment: SupportMapFragment
//    var lastFirst = Any()
//    var lastSecond = Any()
//    var markerPoints: ArrayList<Any> = ArrayList()

    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, API_KEY)
        }

        // Map Fragment
        mapFragment = supportFragmentManager.findFragmentById(
            R.id.map
        ) as SupportMapFragment

        mapViewModel.setMap(mapFragment = mapFragment)

        mapViewModel.showProgress.observe(this) { isShow ->
            if (isShow) {
                activityMainBinding.progressBar.visibility = View.VISIBLE
            } else {
                activityMainBinding.progressBar.visibility = View.GONE
            }
        }

        mapViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        mapViewModel.responseData.observe(this) { result ->
            val lineOption = PolylineOptions()
            for (i in result.indices) {
                lineOption.addAll(result[i])
                lineOption.width(10f)
                lineOption.color(Color.BLACK)
                lineOption.geodesic(true)
            }
            mapViewModel.googleMapUtil.map?.addPolyline(lineOption)
        }

    }

//    override fun onMapReady(p0: GoogleMap?) {
//
//        if (p0 != null) {
//            mMap = p0
//        }
//
//        mMap.setOnMapClickListener { latlong ->
//
//            markerPoints.add(latlong)
//
//            when {
//                markerPoints.size > 3 -> {
//                    markerPoints.clear()
//                    mMap.clear()
//
//                    markerPoints.add(latlong)
//
//                    var origin = markerPoints[0]
//
//                    mMap.addMarker(
//                        MarkerOptions().icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_GREEN
//                            )
//                        ).position(lastSecond as LatLng)
//                    )
//
////                mapHelper.addOnMarker(lastSecond as LatLng)
//
//                    mMap.addMarker(
//                        MarkerOptions().icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_RED
//                            )
//                        ).position(origin as LatLng)
//                    )
//
////                mapHelper.addOnMarker(origin as LatLng)
//
////                    val urll = getDirectionURL(lastSecond as LatLng, origin)
//
////                    GetDirection(urll).execute()
//
//                    val originLatString = (lastSecond as LatLng).latitude.toString()
//                    val originLongString = (lastSecond as LatLng).longitude.toString()
//
//                    val originString = "$originLatString,$originLongString"
//
//                    val destinationLatString = origin.latitude.toString()
//                    val destinationLongString = origin.longitude.toString()
//
//                    val destinationString = "$destinationLatString,$destinationLongString"
//                    mapViewModel.getDirection(originString, destinationString)
//
//
//                }
//
//                markerPoints.size == 1 -> {
//                    mMap.addMarker(
//                        MarkerOptions().icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_GREEN
//                            )
//                        ).position(latlong)
//                    )
//
////                mapHelper.addOnMarker(latlong)
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 18F))
//                }
//
//                markerPoints.size == 2 -> {
//
//                    mMap.clear()
//                    mMap.addMarker(
//                        MarkerOptions().icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_GREEN
//                            )
//                        ).position(markerPoints[0] as LatLng)
//                    )
//
////                mapHelper.addOnMarker(markerPoints[0] as LatLng)
//
//                    mMap.addMarker(
//                        MarkerOptions().icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_RED
//                            )
//                        ).position(latlong)
//                    )
//
////                mapHelper.addOnMarker(latlong)
//
//                    val origin = markerPoints[0]
//                    val dest = markerPoints[1]
//
////                    val urll = getDirectionURL(origin as LatLng, dest as LatLng)
////                    GetDirection(urll).execute()
//
//                    val originLatString = (origin as LatLng).latitude.toString()
//                    val originLongString = (origin as LatLng).longitude.toString()
//
//                    val originString = "$originLatString,$originLongString"
//
//                    val destinationLatString = (dest as LatLng).latitude.toString()
//                    val destinationLongString = (dest as LatLng).longitude.toString()
//
//                    val destinationString = "$destinationLatString,$destinationLongString"
//
//                    mapViewModel.getDirection(originString, destinationString)
//                }
//
//                markerPoints.size == 3 -> {
//                    mMap.clear()
//
//                    var origin = markerPoints[1]
//
//                    var dest = markerPoints[2]
//
//                    lastFirst = origin
//                    lastSecond = dest
//
//                    mMap.addMarker(
//                        MarkerOptions().icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_GREEN
//                            )
//                        ).position(origin as LatLng)
//                    )
//
////                mapHelper.addOnMarker(origin as LatLng)
//
//                    mMap.addMarker(
//                        MarkerOptions().icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_RED
//                            )
//                        ).position(latlong)
//                    )
//
////                mapHelper.addOnMarker(latlong)
//
////                    val urll = getDirectionURL(origin, dest as LatLng)
////
////                    GetDirection(urll).execute()
//
//                    val originLatString = origin.latitude.toString()
//                    val originLongString = origin.longitude.toString()
//
//                    val originString = "$originLatString,$originLongString"
//
//                    val destinationLatString = (dest as LatLng).latitude.toString()
//                    val destinationLongString = (dest as LatLng).longitude.toString()
//
//                    val destinationString = "$destinationLatString,$destinationLongString"
//
//                    mapViewModel.getDirection(originString, destinationString)
//
//                }
//
//            }
//
//        }
//
//    }
}