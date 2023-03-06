package org.geeksforgeeks.myapplication.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import org.geeksforgeeks.myapplication.R
import org.geeksforgeeks.myapplication.databinding.ActivityClusterBinding
import org.geeksforgeeks.myapplication.ui.viewmodel.MapViewModel
import org.geeksforgeeks.myapplication.utils.Const.Companion.googleMapUtils
import org.geeksforgeeks.myapplication.utils.GoogleMapUtil

class ClusterActivity : AppCompatActivity() {

    private lateinit var activityClusterBinding: ActivityClusterBinding
    private lateinit var mapViewModel: MapViewModel
    private val googleMapUtil: GoogleMapUtil by googleMapUtils(R.id.cluster_map)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityClusterBinding = ActivityClusterBinding.inflate(layoutInflater)
        setContentView(activityClusterBinding.root)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        mapViewModel.setClusterMap(googleMapUtil = googleMapUtil, this)

    }

    private fun setUpCluster() {

    }

}