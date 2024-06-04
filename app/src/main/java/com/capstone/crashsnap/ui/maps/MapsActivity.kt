package com.capstone.crashsnap.ui.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.capstone.crashsnap.BuildConfig
import com.capstone.crashsnap.R
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.data.NetResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.capstone.crashsnap.databinding.ActivityMapsBinding
import com.capstone.crashsnap.ui.auth.SignupActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0
//    private lateinit var placesClient: PlacesClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        val apiKey = getApiKey(this)

//        Places.initialize(applicationContext, apiKey)
//        placesClient = Places.createClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        isMyLocationEnabled()
        getMyLastLocation()

        val cameraZoom = 15f
        val userLocation = LatLng(lat, lon)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, cameraZoom))
        mMap.setOnMyLocationClickListener {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
        }

        setMapStyle()

    }


    private fun isMyLocationEnabled(){
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLocationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLocationLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    showToast(getString(R.string.permission_success))
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    showToast(getString(R.string.permission_success))
                    getMyLastLocation()
                }
                else -> {
                    showToast(getString(R.string.permission_err))
                }
            }
        }

    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(TAG, "getMyLastLocation lokasi : ${location.latitude}, ${location.longitude}")
                    lat = location.latitude
                    lon = location.longitude
                    val userLoc = LatLng(location.latitude, location.longitude)
                    getNearbyRepairShops(userLoc)
                } else {
                    showToast("Location is not found. Try Again")
                }
            }
        } else {
            requestPermissionLocationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getNearbyRepairShops(latlng: LatLng) {
        val apiKey = BuildConfig.API_KEY
        val type = "car_repair"
        val location = "${latlng.latitude},${latlng.longitude}"
        val radius = 5000

        viewModel.nearbyPlaces(type, location, radius, apiKey)
            .observe(this@MapsActivity) { result ->
                if (result != null) {
                    when (result) {
                        is NetResult.Loading -> {

                        }

                        is NetResult.Success -> {
                            val message = result.data.status
                            Log.d(TAG, "getNearbyRepairShops suk: ${result.data.results}")
                            if (result.data.results != null) {
                                Log.d(TAG, "getNearbyRepairShops suk: $message")
                                val places = result.data.results
                                places?.forEach {
                                    val latLng = LatLng(it.geometry.location.lat, it.geometry.location.lng)
                                    mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
                                }
                                val cameraZoom = 15f
                                val userLocation = LatLng(lat, lon)
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, cameraZoom))
                                showToast(message)
                            } else {
                                Log.d(SignupActivity.TAG, "getNearbyRepairShops err: $message ,")
                                showToast(message)
                            }
                        }

                        is NetResult.Error -> {
                            Log.d(SignupActivity.TAG, "onCreate err failed net:  ${result.error}")
                            showToast(result.error)
                        }


                    }
                }
            }

    }

//    private fun getApiKey(context: Context): String {
//        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
//        val bundle = applicationInfo.metaData
//        return bundle.getString("com.google.android.geo.API_KEY") ?: ""
//    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                showToast(getString(R.string.mapStyle_err))
            }
        } catch (exception: Resources.NotFoundException) {
            showToast(exception.toString())
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        const val TAG = "mapsact"
    }
}