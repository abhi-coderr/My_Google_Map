package org.geeksforgeeks.myapplication.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.SupportMapFragment
import org.geeksforgeeks.myapplication.R
import org.geeksforgeeks.myapplication.databinding.ActivityClusterBinding
import org.geeksforgeeks.myapplication.ui.viewmodel.MapViewModel

class ClusterActivity : AppCompatActivity() {

    private lateinit var activityClusterBinding: ActivityClusterBinding
    private lateinit var mapClusterFragment: SupportMapFragment
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityClusterBinding = ActivityClusterBinding.inflate(layoutInflater)
        setContentView(activityClusterBinding.root)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        mapClusterFragment = supportFragmentManager.findFragmentById(
            R.id.cluster_map
        ) as SupportMapFragment

        mapViewModel.setMap(mapFragment = mapClusterFragment)

    }
}