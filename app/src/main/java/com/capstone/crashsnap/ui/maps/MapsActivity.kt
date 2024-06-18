package com.capstone.crashsnap.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.capstone.crashsnap.data.remote.response.PlaceResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.capstone.crashsnap.databinding.ActivityMapsBinding
import com.capstone.crashsnap.ui.auth.LoginActivity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }



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
        val location = "${latlng.latitude},${latlng.longitude}"
        val radius = 5000

        val carRepairLiveData = viewModel.nearbyPlaces("car repair", location, radius, apiKey)
        val ketokMagicLiveData = viewModel.nearbyPlaces("ketok magic", location, radius, apiKey)

        carRepairLiveData.observe(this@MapsActivity) { carRepairResult ->
            ketokMagicLiveData.observe(this@MapsActivity) { ketokMagicResult ->
                val combinedResults = mutableListOf<PlaceResult>()

                if (carRepairResult != null) {
                    when (carRepairResult) {
                        is NetResult.Success -> {
                            carRepairResult.data.results.let { combinedResults.addAll(it) }
                        }
                        is NetResult.Error -> {
                            showToast(carRepairResult.error)
                        }
                        else -> {}
                    }
                }

                if (ketokMagicResult != null) {
                    when (ketokMagicResult) {
                        is NetResult.Success -> {
                            ketokMagicResult.data.results.let { combinedResults.addAll(it) }
                        }
                        is NetResult.Error -> {
                            showToast(ketokMagicResult.error)
                        }
                        else -> {}
                    }
                }

                if (combinedResults.isNotEmpty()) {
                    combinedResults.forEach {
                        val latLng = LatLng(it.geometry.location.lat, it.geometry.location.lng)
                        mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
                    }
                    val cameraZoom = 15f
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, cameraZoom))
                    showToast("Found ${combinedResults.size} places")
                }
            }
        }
    }


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