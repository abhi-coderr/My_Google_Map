package org.geeksforgeeks.myapplication.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.geeksforgeeks.myapplication.network.ApiServices.MapServices
import org.geeksforgeeks.myapplication.network.model.MapDataClass
import org.geeksforgeeks.myapplication.utils.Const.Companion.API_KEY
import org.geeksforgeeks.myapplication.utils.Const.Companion.BASE_URL
import org.geeksforgeeks.myapplication.utils.MapHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapViewModel : ViewModel() {

    val retrofit = retrofitInstance()
    val responseData = MutableLiveData<ArrayList<List<LatLng>>>()
    val errorMessage = MutableLiveData<String>()
    val showProgress = MutableLiveData<Boolean>()
    lateinit var mapHelper : MapHelper
    var lastFirst = Any()
    var lastSecond = Any()
    var markerPoints: ArrayList<Any> = ArrayList()
    var job: Job? = null

    fun getDirection(origin: String, dest: String) {
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
                    val path = ArrayList<LatLng>()
                    for (i in 0 until respObj?.routes?.get(0)?.legs?.get(0)?.steps?.size!!) {
                        path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    }
                    response.add(path)
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
        val api: MapServices by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MapServices::class.java)
        }
        return api
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

    fun setMapHelper(mMap: GoogleMap){

        mapHelper = MapHelper(map = mMap, onClick = {latlong ->

            markerPoints.add(latlong)

            when {
                markerPoints.size > 3 -> {
                    markerPoints.clear()
                    mMap.clear()

                    markerPoints.add(latlong)

                    var origin = markerPoints[0]

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

//                    val urll = getDirectionURL(lastSecond as LatLng, origin)

//                    GetDirection(urll).execute()

                    val originLatString = (lastSecond as LatLng).latitude.toString()
                    val originLongString = (lastSecond as LatLng).longitude.toString()

                    val originString = "$originLatString,$originLongString"

                    val destinationLatString = origin.latitude.toString()
                    val destinationLongString = origin.longitude.toString()

                    val destinationString = "$destinationLatString,$destinationLongString"
                    getDirection(originString, destinationString)

                }

                markerPoints.size == 1 -> {
                    mMap.addMarker(
                        MarkerOptions().icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        ).position(latlong)
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 18F))
                }

                markerPoints.size == 2 -> {

                    mMap.clear()
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

                    val origin = markerPoints[0]
                    val dest = markerPoints[1]

//                    val urll = getDirectionURL(origin as LatLng, dest as LatLng)
//                    GetDirection(urll).execute()

                    val originLatString = (origin as LatLng).latitude.toString()
                    val originLongString = (origin as LatLng).longitude.toString()

                    val originString = "$originLatString,$originLongString"

                    val destinationLatString = (dest as LatLng).latitude.toString()
                    val destinationLongString = (dest as LatLng).longitude.toString()

                    val destinationString = "$destinationLatString,$destinationLongString"

                    getDirection(originString, destinationString)
                }

                markerPoints.size == 3 -> {
                    mMap.clear()

                    var origin = markerPoints[1]

                    var dest = markerPoints[2]

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

//                    val urll = getDirectionURL(origin, dest as LatLng)
//
//                    GetDirection(urll).execute()

                    val originLatString = origin.latitude.toString()
                    val originLongString = origin.longitude.toString()

                    val originString = "$originLatString,$originLongString"

                    val destinationLatString = (dest as LatLng).latitude.toString()
                    val destinationLongString = (dest as LatLng).longitude.toString()

                    val destinationString = "$destinationLatString,$destinationLongString"

                    getDirection(originString, destinationString)

                }
            }

        })


    }

}