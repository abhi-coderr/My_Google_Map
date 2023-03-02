package org.geeksforgeeks.myapplication.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import org.geeksforgeeks.myapplication.R
import org.geeksforgeeks.myapplication.databinding.ActivityMainBinding
import org.geeksforgeeks.myapplication.ui.viewmodel.MapViewModel
import org.geeksforgeeks.myapplication.utils.Const.Companion.API_KEY

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, API_KEY)
        }

        // Map Fragment
        mapFragment = supportFragmentManager.findFragmentById(
            R.id.map
        ) as SupportMapFragment

        mapViewModel.setMap(mapFragment = mapFragment)

        mapViewModel.showProgress.observe(this) { isShow ->
            if (isShow) {
                activityMainBinding.progressBar.visibility = View.VISIBLE
            } else {
                activityMainBinding.progressBar.visibility = View.GONE
            }
        }

        mapViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        mapViewModel.responseData.observe(this) { result ->
            val lineOption = PolylineOptions()
            for (i in result.indices) {
                lineOption.addAll(result[i])
                lineOption.width(10f)
                lineOption.color(Color.BLACK)
                lineOption.geodesic(true)
            }
            mapViewModel.googleMapUtil.addPolyline(lineOption)
        }

        activityMainBinding.clusterBtn.setOnClickListener {
            startActivity(Intent(this, ClusterActivity::class.java))
        }

    }
}