package org.geeksforgeeks.myapplication.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import org.geeksforgeeks.myapplication.R
import org.geeksforgeeks.myapplication.network.model.MyItem


class MyClusterRenderer(
    private var context: Context, map: GoogleMap?, private var customMarker: Bitmap? = null,
    clusterManager: ClusterManager<MyItem?>
) : DefaultClusterRenderer<MyItem?>(context, map, clusterManager) {
    private val mClusterIconGenerator = IconGenerator(context)
    override fun onBeforeClusterItemRendered(
        item: MyItem?,
        markerOptions: MarkerOptions
    ) {
        customMarker?.let { bitmap ->
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        }
//        val markerDescriptor =
//            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
//        markerOptions.icon(customMarker)
    }

    override fun onClusterItemRendered(clusterItem: MyItem?, marker: Marker) {
        super.onClusterItemRendered(clusterItem, marker)
    }

    override fun onBeforeClusterRendered(cluster: Cluster<MyItem?>, markerOptions: MarkerOptions) {
        val clusterIcon: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_marker_institute)

//        clusterIcon?.setColorFilter(
//            ContextCompat.getColor(context, android.R.color.holo_orange_light),
//            PorterDuff.Mode.SRC_ATOP
//        )



        mClusterIconGenerator.setBackground(clusterIcon)

        //modify padding for one or two digit numbers
        if (cluster.size < 10) {
            mClusterIconGenerator.setContentPadding(40, 20, 0, 0)
        } else {
            mClusterIconGenerator.setContentPadding(30, 20, 0, 0)
        }
        val icon = mClusterIconGenerator.makeIcon(cluster.size.toString())
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
        customMarker?.let { bitmap ->
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        }
    }
}