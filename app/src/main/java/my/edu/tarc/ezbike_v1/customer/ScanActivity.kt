package my.edu.tarc.ezbike_v1.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import my.edu.tarc.ezbike_v1.R


class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    var scannerView : ZXingScannerView?= null
    private val countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        Firebase.firestore

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.lightBrown)))
        supportActionBar!!.elevation = 0F
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        setPermission()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun handleResult(p0: Result?) {
        val db = Firebase.firestore
        val bikeIdresult = p0?.text.toString()
        val bikeRef = db.collection("bike").document(bikeIdresult)
        val myApp = application as MyApp

        val userId = "testing"
        val lendRef = db.collection("user_lend").document(userId)
        val userRef = db.collection("user").document(userId)

        val myvalue =intent.getStringExtra("return_status").toString()
        lendRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val status = document.getString("status").toString()
                    val bikeId = document.getString("bikeid").toString()

                    if(status == "lend"){
                        if (bikeId == bikeIdresult) {
                            // the user has already borrowed this bike, so we can return it
                            bikeRef.update("status", "available")
                                .addOnSuccessListener {
                                    //Toast.makeText(applicationContext, "You have successfully returned the bike!", Toast.LENGTH_SHORT).show()

                                    val db = FirebaseFirestore.getInstance()
                                    val lendRef = db.collection("user_lend").document(userId)
                                    lendRef.update("status", "")
                                        .addOnSuccessListener {
                                            lendRef.update("bikeid", "")
                                                .addOnSuccessListener {
                                                    lendRef.update("lendDur", 0)

                                                    val intent = Intent(this, LendOkActivity::class.java)
                                                    intent.putExtra("lend_status","return")
                                                    intent.putExtra("return_status",myvalue.toString())
                                                    startActivity(intent)
                                                    lendRef.update("lastLocation", 0)
//                                                    val duration2 = intent.getStringExtra("duration")
                                                    myApp.countdownResult = ""
                                                    myApp.duration = 0
                                                }.addOnFailureListener {
                                                    //Toast.makeText(applicationContext, "Failed to update Bike ID", Toast.LENGTH_SHORT).show()
                                                }
                                        }.addOnFailureListener {
                                            //Toast.makeText(applicationContext, "Failed to update status", Toast.LENGTH_SHORT).show()
                                        }

                                    finish()
                                }.addOnFailureListener {
                                    //Toast.makeText(applicationContext, "Failed to update bike status", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // the user has already borrowed a bike, but not this one
                            Toast.makeText(applicationContext, "You have already borrowed a different bike", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LendOkActivity::class.java)
                            intent.putExtra("lend_status","not_ur")
                            startActivity(intent)
                            finish()
                        }
                    }
                    else{
                        bikeRef.get().addOnSuccessListener { document ->
                            if (document != null) {
                                val status = document.getString("status")
                                if (status != null && status == "under maintenance") {
                                    //Toast.makeText(applicationContext, "Bike is under maintenance", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LendOkActivity::class.java)
                                    intent.putExtra("lend_status","mainten")
                                    startActivity(intent)
                                }else if(status != null && status == "unavailable"){
                                    //Toast.makeText(applicationContext, "Bike is not available", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LendOkActivity::class.java)
                                    intent.putExtra("lend_status","fail")
                                    startActivity(intent)
                                }else if(status != null && status == "available"){

                                    bikeRef.update("status", "unavailable")
                                        .addOnSuccessListener {
                                            //Toast.makeText(applicationContext, "You have successfully lend a bike!", Toast.LENGTH_SHORT).show()
                                            val db = FirebaseFirestore.getInstance()
                                            val lendRef = db.collection("user_lend").document(userId)

                                            lendRef.update("status", "lend")
                                                .addOnSuccessListener {
                                                    lendRef.update("bikeid", bikeIdresult)
                                                        .addOnSuccessListener {
                                                            val myApp = application as MyApp
                                                            val duration = myApp.duration
                                                            lendRef.update("lendDur", duration)
                                                            //Toast.makeText(applicationContext, "Status and Bike ID updated successfully", Toast.LENGTH_SHORT).show()
                                                            val intent2 = Intent(this, LendOkActivity::class.java)
                                                            startActivity(intent2)

                                                        }.addOnFailureListener {
                                                            Toast.makeText(applicationContext, "Failed to update Bike ID", Toast.LENGTH_SHORT).show()
                                                        }
                                                }.addOnFailureListener {
//                                        Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
                                                    Toast.makeText(applicationContext, "Failed to update status", Toast.LENGTH_SHORT).show()
                                                }
                                            finish()


                                        }.addOnFailureListener {
                                            Toast.makeText(applicationContext, "Failed to update bike status", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                else{
//                                    Toast.makeText(applicationContext, "Invalid QR Code", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LendOkActivity::class.java)
                                    intent.putExtra("lend_status","qrcode")
                                    startActivity(intent)
                                }
                            } else {
                                Toast.makeText(applicationContext, "Bike not found", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LendOkActivity::class.java)
                                intent.putExtra("lend_status","fail")
                                startActivity(intent)
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext, "Failed to get bike status: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    //Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "User not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, "Failed to get user information: ${exception.message}", Toast.LENGTH_SHORT).show()
            }


    }



    override fun onResume() {
        super.onResume()


        scannerView?.setResultHandler(this)
        scannerView?.startCamera()

    }

    override fun onStop() {
        super.onStop()

        scannerView?.stopCamera()
        onBackPressed()
    }

    private fun setPermission(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext, "You need camera permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}