package org.geeksforgeeks.myapplication.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.geeksforgeeks.myapplication.network.ApiServices.MapServices
import org.geeksforgeeks.myapplication.network.model.MapDataClass
import org.geeksforgeeks.myapplication.utils.Const.Companion.API_KEY
import org.geeksforgeeks.myapplication.utils.Const.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapViewModel : ViewModel() {

    val retrofit = retrofitInstance()

    val responseData = MutableLiveData<ArrayList<List<LatLng>>>()

    val errorMessage = MutableLiveData<String>()

    val showProgress = MutableLiveData<Boolean>()

    var job: Job? = null

    fun getDirection(origin: String, dest: String) {
        showProgress.value = true

        job = viewModelScope.launch {
            val result = retrofit.getRouteFromAPI(
                origin = origin,
                destination = dest,
                key = API_KEY
            )
//            ) val result = retrofit.getRouteFromAPI(
//                origin.latitude,
//                origin.longitude,
//                dest.latitude,
//                dest.longitude,
//                API_KEY
//            )

            withContext(Dispatchers.Main) {
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

    }

    private fun onError(message: String) {
        errorMessage.value = message
        showProgress.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun retrofitInstance(): MapServices {
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

}