package my.edu.tarc.ezbike_v1.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentHomeBinding
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private val INTERVAL = 1000L // interval between each tick of the timer, in milliseconds
    private var creditDecreased = false
    private val viewLocationActivity = ViewLocationActivity()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var near = false
    var isPermissionGranter: Boolean = false

    //delete
    private var firebaseAuth = FirebaseAuth.getInstance()
    val email = firebaseAuth.currentUser?.email


    val db = Firebase.firestore
    val lendRef = db.collection("user_lend").document(email!!)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        activity?.actionBar?.setDisplayHomeAsUpEnabled(false);


//        val buttonLocation: ImageView = binding.buttonLocation
//        buttonLocation.setOnClickListener{
//            val intent = Intent(activity, ViewLocationActivity::class.java)
//            startActivity(intent)
//        }
//
//        val buttonBike: ImageView = binding.buttonBike
//        buttonBike.setOnClickListener{
//            val intent = Intent(activity, ReturnActivity::class.java)
//            startActivity(intent)
//        }

        val userRef = db.collection("user").document(email!!)

        val btnView = binding.buttonLocation
        val btnLend = binding.buttonBike
        var status = ""
        var bikeId = ""
        var oppo = ""
        var cred = 0
        var pendingDur = 0
        var lastLocation = 0


        // get the duration from the application object
        val durationInMillis = (requireActivity().applicationContext as MyApp).duration ?: 0
        timeLeftInMillis = durationInMillis

        lendRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val oppo = documentSnapshot.getString("opportunity").toString()

            } else {
                Toast.makeText(requireActivity().applicationContext, "User not found1", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                requireActivity().applicationContext,
                "Failed to get user credibility: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                cred = documentSnapshot.getLong("credibility")?.toInt() ?: 0
            } else {
                Toast.makeText(requireActivity().applicationContext, "User not found2", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                requireActivity().applicationContext,
                "Failed to get user credibility: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

        lendRef.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Failed to get user information: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                return@addSnapshotListener
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                status = documentSnapshot.getString("status").toString()
                bikeId = documentSnapshot.getString("bikeid").toString()
                oppo = documentSnapshot.getString("opportunity").toString()
                pendingDur = documentSnapshot.getLong("pending_dur")?.toInt() ?: 0
                lastLocation = documentSnapshot.getLong("lastLocation")?.toInt() ?: 0

                updateUI(status, cred, lastLocation, oppo)
            } else {
                Toast.makeText(requireActivity().applicationContext, "User not found3", Toast.LENGTH_SHORT).show()
            }
        }

        btnView.setOnClickListener{
            val intent = Intent(requireActivity().applicationContext, ViewLocationActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateUI(status: String, cred: Int, lastLocation:Int, oppo: String) {
        val myApp = requireActivity().applicationContext as MyApp
        val duration = myApp.duration
        var cDRes = myApp.countdownResult


        var tes = 0
        //delete
        val userId = "testing"
        val db = Firebase.firestore
        val userRef = db.collection("user").document(userId)

        if (status == "lend") {
            binding.cdown.isVisible = true
            binding.cdownLbl.isVisible = true
            binding.lblPend.isVisible = false
            binding.textViewLend.text = getString(R.string.returnText)

            if (duration != null) {
                if (countDownTimer != null) {
                    countDownTimer?.cancel()
                    countDownTimer = null
                }
                val durationInMillis = duration * 60 * 1000
                val startTime = SystemClock.elapsedRealtime()
                val endTime = startTime + durationInMillis
                countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val elapsedTime = endTime - SystemClock.elapsedRealtime()
                        val hours = elapsedTime / (1000 * 60 * 60)
                        val minutes = (elapsedTime / (1000 * 60)) % 60
                        val seconds = (elapsedTime / 1000) % 60
                        val remainingTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        binding.cdown.text = remainingTime
                    }

                    override fun onFinish() {
                        val myApp = requireActivity().applicationContext as MyApp

                        binding.cdown.text = "Time Over"
                        myApp.countdownResult = "return_late"
//                        Toast.makeText(applicationContext,cDRes.toString() , Toast.LENGTH_SHORT).show()
                        if (!creditDecreased) {
                            creditDecreased = true
                            userRef.update("credibility", FieldValue.increment(-10))
                        }
                        myApp.duration = 0

                        val lendRef = db.collection("user_lend").document(userId)
                        lendRef.update("lendDur", 0)
                        val currentDate = Date()
                        // Add one day to the current date
                        val calendar = Calendar.getInstance()
                        calendar.time = currentDate

                        if(cred>79){
                            lendRef.update("pending_dur", 0)
                            lendRef.update("opportunity", "")
                        }else if(cred < 80 && cred > 59 && oppo == ""){
                            calendar.add(Calendar.DAY_OF_YEAR, 2)
                            val penaltyDate = calendar.time
                            lendRef.update("penalty_date", penaltyDate)
                            lendRef.update("pending_dur", 2)
                            lendRef.update("opportunity", "pend")
                        }else if(cred < 60 && cred > 39 && oppo == ""){
                            calendar.add(Calendar.DAY_OF_YEAR, 8)
                            val penaltyDate = calendar.time
                            lendRef.update("penalty_date", penaltyDate)
                            lendRef.update("pending_dur", 8)
                            lendRef.update("opportunity", "pend")
                        }else if(cred < 40 && cred > 19 && oppo == ""){
                            calendar.add(Calendar.DAY_OF_YEAR, 31)
                            val penaltyDate = calendar.time
                            lendRef.update("penalty_date", penaltyDate)
                            lendRef.update("pending_dur", 31)
                            lendRef.update("opportunity", "pend")
                        }else if(cred < 20 && cred > 0 && oppo == ""){
                            calendar.add(Calendar.DAY_OF_YEAR, 61)
                            val penaltyDate = calendar.time
                            lendRef.update("penalty_date", penaltyDate)
                            lendRef.update("pending_dur", 61)
                            lendRef.update("opportunity", "pend")
                        }else if(cred <= 0) {
//                            Toast.makeText(applicationContext,cred.toString() , Toast.LENGTH_SHORT).show()
                            lendRef.update("opportunity", "never")
                            lendRef.update("pending_dur", 0)
                        }
                    }
                }.start()

//                Toast.makeText(this, countDownTimer.toString(), Toast.LENGTH_SHORT).show()
            }
            binding.buttonBike.setOnClickListener {
                Toast.makeText(activity, "hello", Toast.LENGTH_SHORT)
//                checkPer(userId)
                val lendRef = db.collection("user_lend").document(userId)
                lendRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val lastLocation = documentSnapshot.getLong("lastLocation")
                        if (lastLocation != null && lastLocation <= 1000) {
                            val intent = Intent(requireActivity().applicationContext, ReturnActivity::class.java)
                            intent.putExtra("return_status", myApp.countdownResult.toString())
//                            intent.putExtra("duration", myApp.duration.toString())
                            startActivity(intent)
                        } else {
                            Toast.makeText(activity, "You are not nearby EZBike Station", Toast.LENGTH_SHORT).show()                        }
                    } else {
                        Toast.makeText(activity, "User lend data not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(activity, "Error fetching user lend data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        } else {

            myApp.duration = 0

//            lendRef.update("pending_dur", 0)
            //today date
            val tdy = Date()
            val calendar = Calendar.getInstance()
            calendar.time = tdy
            val today = calendar.time


            val lblPend = binding.lblPend
            lblPend.isVisible = false
            //penalty date
            var penaltyDate: Date? = null
            lendRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    penaltyDate = documentSnapshot.getTimestamp("penalty_date")?.toDate()
                    if(cred < 80 && oppo=="pend"){
                        var days: Long? = null
                        penaltyDate?.let {
                            val diff = it.time - calendar.timeInMillis
                            days = TimeUnit.MILLISECONDS.toDays(diff)

                        }
                        // Update the countdown TextView with the remaining days
                        lblPend.text = "Pending on: $days day(s)"
                        lblPend.isVisible = true
                    }else if(oppo == "never") {
                        lblPend.text = getString(R.string.pendingdesc)
                        lblPend.isVisible = true
                    }
                } else {
                    Log.d("TAG", "fail to get document")
                }
            }.addOnFailureListener { exception ->
                Log.d("TAG", "Fail to get collection")
            }

            binding.cdown.isVisible = false
            binding.cdownLbl.isVisible = false
            binding.textViewLend.text = getString(R.string.lend)
            (requireActivity().applicationContext as MyApp).countdownResult = ""
            binding.buttonBike.setOnClickListener {

                if (status == "lend") {
                    val intent = Intent(activity, ReturnActivity::class.java)
                    startActivity(intent)

                } else {
                    if (cred < 80) {
                        if (oppo == "pend" && today.after(penaltyDate)) {
                            lendRef.update("opportunity", "")
                            lblPend.isVisible = false
                            val intent = Intent(activity, LendActivity::class.java)
                            startActivity(intent)
                        }else if(oppo == "never") {
                            Toast.makeText(
                                requireActivity().applicationContext,
                                "You can't lend bike anymore in the future!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_nav_home_to_nav_credibility)
//                            val intent = Intent(activity, CreditActivity::class.java)
//                            startActivity(intent)
                        }else if (oppo == ""){
                            val intent = Intent(activity, LendActivity::class.java)
                            startActivity(intent)
                        }else{
                            findNavController().navigate(R.id.action_nav_home_to_nav_credibility)
                        }

                    } else {
                        val intent = Intent(activity, LendActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }


    private fun checkPer(userid: String): Boolean {
        var userId = userid
        var isNearby = false
        val db = Firebase.firestore
        val lendRef = db.collection("user_lend").document(userId)
        checkPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {
                    // Use the user's location
                    val userLocation = GeoPoint(location.latitude, location.longitude)

                    // Query the stationName collection and loop through the documents
                    db.collection("stationName")
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val stationLocation = document.getGeoPoint("position")
                                val distance = location.distanceTo(Location("").apply {
                                    if (stationLocation != null) {
                                        latitude = stationLocation.latitude
                                    }
                                    if (stationLocation != null) {
                                        longitude = stationLocation.longitude
                                    }
                                })
                                if (distance <= 1000) {
                                    isNearby = true
                                    lendRef.update("lastLocation", distance.toInt())
                                    break
                                }else{
                                    lendRef.update("lastLocation", distance.toInt())
                                }

                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(activity, "Fail to get station locations", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Fail to get location", Toast.LENGTH_SHORT).show()
            }

        return isNearby
    }
    private fun checkPermission() {
        Dexter.withContext(activity).withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION).withListener(object:
            PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                isPermissionGranter = true

//                Toast.makeText(applicationContext, "Permission Granter", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireContext().packageName, "")
                intent.data = uri
                startActivity(intent)
            }

            override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                isPermissionGranter = true
                p1?.continuePermissionRequest()
            }

        }).check()
    }

    override fun onResume() {
        super.onResume()
        checkPer(email!!)
    }
}