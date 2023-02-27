package org.geeksforgeeks.myapplication

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.geeksforgeeks.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var activityMainBinding: ActivityMainBinding
//    private lateinit var customInfowindowsLayoutBinding: CustomInfowindowsLayoutBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
//    private var originLatitude: Double = 23.1667
//    private var originLongitude: Double = 72.5810
//    private var destinationLatitude: Double = 23.1929
//    private var destinationLongitude: Double = 72.6156
//    private var firstLocation: Double? = null
//    private var secondLocation: Double? = null

    private var isClickable = false

    var lastFirst = Any()
    var lastSecond = Any()


    var markerPoints: ArrayList<Any> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

//        mMap = GoogleMap

        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)

        val apiKey = "AIzaSyCaJqa2rUm9ZAKqGzl7LZXVIvfEbCYGa4I"

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        // Map Fragment
        mapFragment = supportFragmentManager.findFragmentById(
            R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val gd = activityMainBinding.directions
        gd.setOnClickListener {

            Toast.makeText(this, "🤣", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            mMap = p0
        }
        mMap.setInfoWindowAdapter(MyInfoWindowAdapter(this))

        mMap.setOnMapClickListener { latlong ->

            if (markerPoints.size > 2) {
                markerPoints.clear()
                mMap.clear()

                markerPoints.add(latlong)

                var origin = markerPoints.get(0)

                mMap.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    ).position(lastSecond as LatLng)
                )

                mMap.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        )
                    ).position(origin as LatLng)
                )

                val urll = getDirectionURL(lastSecond as LatLng, origin)
                GetDirection(urll).execute()


            }

            markerPoints.add(latlong)

            if (markerPoints.size == 1) {
                mMap.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    ).position(latlong)
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 18F))

            } else if (markerPoints.size == 2) {

                mMap.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    ).position(markerPoints[0] as LatLng)
                )

                mMap.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        )
                    ).position(latlong)
                )

                var origin = markerPoints.get(0)
                var dest = markerPoints.get(1)
                val urll = getDirectionURL(origin as LatLng, dest as LatLng)
                GetDirection(urll).execute()

            } else if (markerPoints.size == 3) {


                mMap.clear()

                var origin = markerPoints.get(1)

                var dest = markerPoints.get(2)

                lastFirst = origin
                lastSecond = dest

                mMap.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    ).position(origin as LatLng)
                )

                mMap.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        )
                    ).position(latlong)
                )

                val urll = getDirectionURL(origin, dest as LatLng)
                GetDirection(urll).execute()

            }

        }

    }


    private fun getDirectionURL(
        origin: LatLng,
        dest: LatLng,
        secret: String = "AIzaSyCaJqa2rUm9ZAKqGzl7LZXVIvfEbCYGa4I"
    ): String {

        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"

//        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()

            val data = response.body!!.string()


            isClickable = true

            val result = ArrayList<List<LatLng>>()

            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {

                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLACK)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

}