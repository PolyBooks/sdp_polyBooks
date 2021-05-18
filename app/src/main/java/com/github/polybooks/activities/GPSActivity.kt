package com.github.polybooks.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.polybooks.R
import com.github.polybooks.utils.CameraManip
import com.github.polybooks.utils.setupNavbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class GPSActivity: AppCompatActivity() {
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION, "com.google.android.things.permission.MANAGE_GNSS_DRIVERS")
    val REQUEST_CODE_PERMISSIONS = 11

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContentView(R.layout.activity_gps)

        val mapFragment = supportFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                if (permissions.all { it.value }) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.d("GPS", "HERE 1 =======================================")
                    setupMap(mapFragment)
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.

                    Toast.makeText(
                        this,
                        "This feature is unavailable without the required permissions",
                        Toast.LENGTH_SHORT
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }

        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                -> {
                Log.d("GPS", "HERE 2 +++++++++++++++++++++++++++++")
                setupMap(mapFragment)
                }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    REQUIRED_PERMISSIONS)
            }

        }


            setupNavbar(findViewById(R.id.bottom_navigation), this)
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun setupMap(mapFragment : SupportMapFragment?) {
        mapFragment?.getMapAsync { googleMap ->
             {
                //shouldShowRequestPermissionRationale(GPSActivity::class.java, String)
                //ActivityCompat.requestPermissions(
                //    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                //)
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            //Log.d("GPSActivity", "Have enabled the thing=============++++++++++++++++===========")
            googleMap.isMyLocationEnabled = true

            googleMap.setOnMapLoadedCallback {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        location?.let {
                            val current = LatLng(location.latitude, location.longitude)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16f))
                        }
                    }
                //val epfl = LatLng(46.5165921, 6.5576564)
            }
            /*
            // Put this to do updates when the location changes
            val locationRequest = LocationRequest.create()?.apply {
                interval = 10000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations){
                        //updateMap(googleMap, location)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
            */
        }
    }

    private fun updateMap(googleMap: GoogleMap, location: Location){
        val current =  LatLng(location.latitude, location.longitude)
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(current))
    }
}