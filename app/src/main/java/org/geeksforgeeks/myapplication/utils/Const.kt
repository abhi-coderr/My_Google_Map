package org.geeksforgeeks.myapplication.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.SupportMapFragment

class Const {

    companion object {
        const val API_KEY = "AIzaSyCaJqa2rUm9ZAKqGzl7LZXVIvfEbCYGa4I"
        const val BASE_URL = "https://maps.googleapis.com/"
        fun FragmentActivity.googleMapUtils(id: Int): Lazy<GoogleMapUtil> = lazy {
            val supportFragment =
                this.supportFragmentManager.findFragmentById(id) as SupportMapFragment
            GoogleMapUtil(supportFragment)
        }
    }

}