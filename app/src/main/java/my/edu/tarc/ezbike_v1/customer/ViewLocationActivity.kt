package my.edu.tarc.ezbike_v1.customer


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*
import my.edu.tarc.ezbike_v1.R
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


class ViewLocationActivity() : AppCompatActivity(), OnMapReadyCallback {
    var isPermissionGranter: Boolean = false
    lateinit var gMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mMarker: Marker? = null
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_location)
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.setLoggingEnabled(true) // optional but recommended for debugging


        val imageButtonSearch: ImageButton = findViewById(R.id.imageButtonSearch)
        val editTextSearch: EditText = findViewById(R.id.editTextSearch)
        val supportMapFragment = SupportMapFragment.newInstance()

        //
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.lightBrown)))
        supportActionBar!!.elevation = 0F
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.apply {
            title = "START YOUR JOURNEY"
        }

        supportFragmentManager.beginTransaction().add(R.id.fragmentMap, supportMapFragment)
            .commit()
        supportMapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkPermission()     //will be trigger by “VIEW LOCATION” btn
        openGps()   //show dialog to open GPS   //will be trigger by “VIEW LOCATION” btn


        //For location searching
        imageButtonSearch.setOnClickListener {
            if(editTextSearch.text.isNotEmpty()) {
                val location = editTextSearch.text.toString()
                val geocoder = Geocoder(this, Locale.getDefault())


                @Suppress("DEPRECATION")    //added since getFromLocationName is deprecated
                val listAddress: List<Address>? = geocoder.getFromLocationName(location, 1)
                if (listAddress?.isNotEmpty() == true) {
                    val latLng = LatLng(listAddress[0].latitude, listAddress[0].longitude)

                    removeMarker()
                    addMarker(latLng)

                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.0F)
                    gMap.animateCamera(cameraUpdate)
                    //gMap.uiSettings.isZoomControlsEnabled = true

                } else{
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }

            } else{
                Toast.makeText(this, "Type any location name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeMarker() {
        mMarker?.remove()
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        markerOptions.title("YOU")
        mMarker = gMap.addMarker(markerOptions)
    }



    private fun checkPermission() {
        Dexter.withContext(this).withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION).withListener(object:PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                isPermissionGranter = true

                Toast.makeText(applicationContext, "Permission Granter", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, "")
                intent.data = uri
                startActivity(intent)
            }

            override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                isPermissionGranter = true
                p1?.continuePermissionRequest()
            }

        }).check()
    }


    private fun displayOutlets(googleMap: GoogleMap) {
        gMap = googleMap
        val db = FirebaseFirestore.getInstance()
        val stationNameCollection = db.collection("stationName")

        stationNameCollection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val position = document.getGeoPoint("position")
                if(position != null){
                    val markerOptions2 = MarkerOptions()
                    markerOptions2.position(LatLng(position.latitude, position.longitude))
                    markerOptions2.title(document.id)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    gMap.addMarker(markerOptions2)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("TAG", "Error getting documents: ", exception)
        }

    }


    //override the function in the interface
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true

        displayOutlets(googleMap)

        //Check whether the location permission is on
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        gMap.isMyLocationEnabled = true
        getCurrentLocationUser()
    }

    private fun openGps() {

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val task = LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Toast.makeText(this, "GPS on", Toast.LENGTH_SHORT).show()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(this@ViewLocationActivity,
                        0x1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    private fun getCurrentLocationUser() {

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if(location != null){
//                        Toast.makeText(this, "Testing", Toast.LENGTH_SHORT).show()
                        val latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng).title("YOU")

                        gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f))
                        gMap.addMarker(markerOptions)
                    }
                }
            }
        }

    }

}
