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

class Map2ViewModel : ViewModel() {

}