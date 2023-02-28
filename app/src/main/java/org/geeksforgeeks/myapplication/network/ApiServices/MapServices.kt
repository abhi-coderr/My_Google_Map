package org.geeksforgeeks.myapplication.network.ApiServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.geeksforgeeks.myapplication.network.model.MapDataClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MapServices {

    @POST("maps/api/directions/json")
//    @POST("maps/api/directions/json?origin={originLat},{originLong}&destination={destLat},{destLong}&sensor=false&mode=driving&key={apiKey}")
    suspend fun getRouteFromAPI(
//        @Query("origin")
//        @Path("originLat") originLat: Double,
//        @Path("originLong") originLong: Double,
//        @Path("destLat") destLat: Double,
//        @Path("destLong") destLong: Double,
//        @Path("apiKey") apiKey: String
    @Query("origin") origin : String,
    @Query("destination") destination : String,
    @Query("key") key : String
    ): Response<MapDataClass>

}