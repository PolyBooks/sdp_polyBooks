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
import com.github.polybooks.utils.GlobalVariables.EXTRA_SELLER_UID
import com.github.polybooks.utils.setupNavbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class GPSActivity: AppCompatActivity() {
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION, "com.google.android.things.permission.MANAGE_GNSS_DRIVERS")
    val REQUEST_CODE_PERMISSIONS = 11

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var uid : String

    private var mapFrag : SupportMapFragment? = null

    private lateinit var otherUid : String

    private var handler: Handler = Handler()
    private var runnable: Runnable? = null
    private var delay = 5000

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_gps)
        uid = intent.getStringExtra(EXTRA_MESSAGE2).toString()

        mapFrag = supportFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment

        requestPermissionsAndSetUpMap()

        /*val searchButton= findViewById<Button>(R.id.button_search)
        searchButton.setOnClickListener{
            val uidField = findViewById<TextInputEditText>(R.id.uid_field)
            otherUid = uidField.text.toString()

            Firebase.database.reference
                .child("enabled_localisation_$otherUid").get()
                .addOnSuccessListener { it ->
                    if (it as Boolean) {
                        addPostEventListener(Firebase.database.getReference("localisation_$uid"))
                        searchUser(uid, mapFrag)
                    }
                }
        }*/

        /*otherUid = intent.getStringExtra(EXTRA_SELLER_UID).toString()
        Firebase.database.reference
            .child("enabled_localisation_$otherUid").get()
            .addOnSuccessListener { it ->
                if (it as Boolean) {
                    addPostEventListener(Firebase.database.getReference("localisation_$uid"))
                    searchUser(uid, mapFrag)
                }else{
                    val toast = Toast.makeText(applicationContext, "This user didn't allow you to locate him", Toast.LENGTH_LONG)
                    toast.show()
                }
            }.addOnFailureListener{
                val toast = Toast.makeText(applicationContext, "This user didn't allow you to locate him", Toast.LENGTH_LONG)
                toast.show() }*/

        setupNavbar(findViewById(R.id.bottom_navigation), this)

    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun requestPermissionsAndSetUpMap(){
        val requestPermissionLauncher =
            registerForActivityResult( ActivityResultContracts.RequestMultiplePermissions() )
            { permissions ->
                if (permissions.all { it.value }) { setupMap(mapFrag) }
                else { startActivity(Intent(this, MainActivity::class.java)) }
            }
        when {
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            -> { setupMap(mapFrag) }
            else -> {
                requestPermissionLauncher.launch(
                    REQUIRED_PERMISSIONS)
            }

        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun searchUser(uid: String, mapFragment : SupportMapFragment?){
        mapFragment?.getMapAsync { googleMap ->
            googleMap.clear() ; googleMap.isMyLocationEnabled = true
            googleMap.setOnMapLoadedCallback {
                val database = Firebase.database.reference ; var lat: Double ; var long: Double
                database.child("localisation_$uid").child("latitude").get()
                    .addOnSuccessListener { it -> lat = it.value as Double
                    database.child("localisation_$uid").child("longitude")
                        .get().addOnSuccessListener {
                            long = it.value as Double
                        val loca = LatLng(lat, long)
                        googleMap.addMarker(MarkerOptions().position(loca).title("He is here !")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                        val update = CameraUpdateFactory.newLatLngZoom(loca, 16.0f)
                        googleMap.moveCamera(update)
                    }.addOnFailureListener{
                        Log.e("firebase", "Error getting data", it) }
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it) }
            }
        }
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun setupMap(mapFragment : SupportMapFragment?) {
        mapFragment?.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true
            googleMap.setOnMapLoadedCallback {
                updateMyLoca()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun updateMyLoca(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val database = Firebase.database
                    val ref = database.getReference("localisation_$uid")
                    ref.setValue(location)
                }
            }
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searchUser(otherUid, mapFrag)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            Log.w("GPS", "Updating my localisation...")
            updateMyLoca()
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }
}