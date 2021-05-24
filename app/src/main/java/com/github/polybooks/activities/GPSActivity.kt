package com.github.polybooks.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.polybooks.R
import com.github.polybooks.activities.RegisterActivity.Companion.TAG
import com.github.polybooks.utils.setupNavbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.util.*


class GPSActivity: AppCompatActivity() {
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION, "com.google.android.things.permission.MANAGE_GNSS_DRIVERS")
    val REQUEST_CODE_PERMISSIONS = 11

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var uid : String

    private lateinit var locationCallback: LocationCallback

    private var mapFrag : SupportMapFragment? = null

    private lateinit var otherUid : String

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContentView(R.layout.activity_gps)

        uid = intent.getStringExtra(EXTRA_MESSAGE2).toString()

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
                    mapFrag = mapFragment
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
                mapFrag = mapFragment
                setupMap(mapFragment)
                }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    REQUIRED_PERMISSIONS)
            }

        }

        val searchButton= findViewById<Button>(R.id.button_search)
        searchButton.setOnClickListener{
            val uidField = findViewById<TextInputEditText>(R.id.uid_field)
            val uid = uidField.text.toString()

            otherUid = uid;
            addPostEventListener(Firebase.database.getReference("localisation_$uid"))
            searchUser(uid, mapFragment)
        }

        setupNavbar(findViewById(R.id.bottom_navigation), this)

    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun searchUser(uid: String, mapFragment : SupportMapFragment?){
        mapFragment?.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true

            googleMap.setOnMapLoadedCallback {

                val database = Firebase.database.reference
                var lat = 0.0
                var long = 0.0

                database.child("localisation_$uid").child("latitude").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    lat = it.value as Double
                    database.child("localisation_$uid").child("longitude").get().addOnSuccessListener {
                        Log.i("firebase", "Got value ${it.value}")
                        long = it.value as Double
                        val loca = LatLng(lat, long)
                        googleMap.addMarker(
                            MarkerOptions().position(loca)
                                .title("He is here !"))
                        // create an object that will specify how the camera will be updated
                        val update = CameraUpdateFactory.newLatLngZoom(loca, 16.0f)
                        googleMap.moveCamera(update)
                    }.addOnFailureListener{
                        Log.e("firebase", "Error getting data", it)
                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }
        }
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun setupMap(mapFragment : SupportMapFragment?) {
        mapFragment?.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true

            googleMap.setOnMapLoadedCallback {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.

                        location?.let {
                            val current = LatLng(location.latitude, location.longitude)

                            val database = Firebase.database
                            val ref = database.getReference("localisation_$uid")
                            ref.setValue(location)
                        }
                    }
                //val epfl = LatLng(46.5165921, 6.5576564)
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun updateMyLoca(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                location?.let {
                    val current = LatLng(location.latitude, location.longitude)

                    val database = Firebase.database
                    val ref = database.getReference("localisation_$uid")
                    ref.setValue(location)
                }
            }
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                // val post = dataSnapshot.getValue()
                searchUser(otherUid, mapFrag)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)
        // [END post_value_event_listener]
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            Log.w(TAG, "update my loca")
            updateMyLoca()
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }
}