package org.geeksforgeeks.myapplication.network.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyItem(
    lat: Double,
    lng: Double,
    title: String,
    snippet: String
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    fun getZIndex(): Float {
        return 0f
    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.snippet = snippet
    }
}