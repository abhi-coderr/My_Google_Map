package org.geeksforgeeks.myapplication.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.geeksforgeeks.myapplication.network.ApiServices.MapServices
import org.geeksforgeeks.myapplication.utils.Const.Companion.API_KEY
import org.geeksforgeeks.myapplication.utils.Const.Companion.BASE_URL
import org.geeksforgeeks.myapplication.utils.GoogleMapUtil
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MapViewModel : ViewModel() {

    //retrofit instance
    private val retrofit = retrofitInstance()

    //data observed by main activity
    val responseData = MutableLiveData<ArrayList<List<LatLng>>>()

    //error data observed by main activity
    val errorMessage = MutableLiveData<String>()

    //notify to main activity about progressbar
    val showProgress = MutableLiveData<Boolean>()

    //nullable job for coroutine light weight threads
    private var job: Job? = null

    //declare util instance
    lateinit var googleMapUtil: GoogleMapUtil

    private var lastFirst = Any()
    private var lastSecond = Any()
    private var markerPoints: ArrayList<Any> = ArrayList()

    private fun getDirection(origin: String, dest: String) {
        showProgress.value = true

        job = viewModelScope.launch {
            val result = retrofit.getRouteFromAPI(
                origin = origin,
                destination = dest,
                key = API_KEY
            )
            if (result.isSuccessful) {
                showProgress.value = false

                val response = ArrayList<List<LatLng>>()
                try {
                    val respObj = result.body()
                    if (respObj?.routes.isNullOrEmpty()) {
                        onError("Route not possible")
                    } else {
                        val path = ArrayList<LatLng>()
                        for (i in 0 until respObj?.routes?.get(0)?.legs?.get(0)?.steps?.size!!) {
                            path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                        }
                        response.add(path)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                responseData.postValue(response)
            } else {
                onError("Error : ${result.message()}")
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        showProgress.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    private fun retrofitInstance(): MapServices {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor) // same for .addInterceptor(...)
            .connectTimeout(30, TimeUnit.SECONDS) //Backend is really slow
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val api: MapServices by lazy {
            Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MapServices::class.java)
        }
        return api
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
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
            val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dLat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dLng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    fun setMap(mapFragment: SupportMapFragment) {
        googleMapUtil = GoogleMapUtil(
            mapFragment = mapFragment,
            onMapReady = {
            },
            onClick = { latLng ->
                markerPoints.add(latLng)

                when {
                    markerPoints.size > 3 -> {

                        markerPoints.clear()

                        googleMapUtil.clearMap()

                        markerPoints.add(latLng)

                        val origin = markerPoints[0]

                        googleMapUtil.addMarker(
                            lastSecond as LatLng,
                            BitmapDescriptorFactory.HUE_GREEN,
                            isFocus = false
                        )

                        googleMapUtil.addMarker(
                            origin as LatLng,
                            BitmapDescriptorFactory.HUE_RED,
                            isFocus = false
                        )

                        val originLatString = (lastSecond as LatLng).latitude.toString()
                        val originLongString = (lastSecond as LatLng).longitude.toString()

                        val originString = "$originLatString,$originLongString"

                        val destinationLatString = origin.latitude.toString()
                        val destinationLongString = origin.longitude.toString()

                        val destinationString = "$destinationLatString,$destinationLongString"
                        getDirection(originString, destinationString)

                    }

                    markerPoints.size == 1 -> {

                        googleMapUtil.addMarker(
                            latLng,
                            BitmapDescriptorFactory.HUE_GREEN,
                            isFocus = true,
                            zoomLevel = 18f
                        )
                    }

                    markerPoints.size == 2 -> {

                        googleMapUtil.clearMap()

                        googleMapUtil.addMarker(
                            markerPoints[0] as LatLng,
                            BitmapDescriptorFactory.HUE_GREEN,
                            isFocus = false
                        )
                        googleMapUtil.addMarker(
                            latLng,
                            BitmapDescriptorFactory.HUE_RED,
                            isFocus = false
                        )

                        val origin = markerPoints[0]
                        val dest = markerPoints[1]

                        val originLatString = (origin as LatLng).latitude.toString()
                        val originLongString = (origin).longitude.toString()

                        val originString = "$originLatString,$originLongString"

                        val destinationLatString = (dest as LatLng).latitude.toString()
                        val destinationLongString = (dest).longitude.toString()

                        val destinationString = "$destinationLatString,$destinationLongString"

                        getDirection(originString, destinationString)
                    }

                    markerPoints.size == 3 -> {
                        googleMapUtil.clearMap()

                        val origin = markerPoints[1]

                        val dest = markerPoints[2]

                        lastFirst = origin
                        lastSecond = dest


                        googleMapUtil.addMarker(
                            origin as LatLng,
                            BitmapDescriptorFactory.HUE_GREEN,
                            isFocus = false
                        )
                        googleMapUtil.addMarker(
                            latLng,
                            BitmapDescriptorFactory.HUE_RED,
                            isFocus = false
                        )

                        val originLatString = origin.latitude.toString()
                        val originLongString = origin.longitude.toString()

                        val originString = "$originLatString,$originLongString"

                        val destinationLatString = (dest as LatLng).latitude.toString()
                        val destinationLongString = (dest).longitude.toString()

                        val destinationString = "$destinationLatString,$destinationLongString"

                        getDirection(originString, destinationString)

                    }
                }
            }
        )
    }

    fun setClusterMap(mapFragment: SupportMapFragment) {
        googleMapUtil = GoogleMapUtil(mapFragment = mapFragment, onClick = {
            googleMapUtil.addMarker(it, BitmapDescriptorFactory.HUE_AZURE)
        })
    }

}