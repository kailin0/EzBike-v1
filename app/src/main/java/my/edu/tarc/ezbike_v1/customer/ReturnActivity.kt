package my.edu.tarc.ezbike_v1.customer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.ActivityReturnBinding
import java.io.ByteArrayOutputStream
import java.util.*

class ReturnActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReturnBinding
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.lightBrown)))
        supportActionBar!!.elevation = 0F
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.apply {
            title = "RETURN YOUR BIKE"
        }

        val btnPic = binding.btnTakePic
        val btnscan = binding.btnscanReturn

        btnPic.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
        btnscan.setOnClickListener{
            Toast.makeText(applicationContext, "Take bike picture before scan QR Code", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val btnscan = binding.btnscanReturn
        val myApp = application as MyApp

        val userId = "testing"
        val db = Firebase.firestore
        val lendRef = db.collection("user_lend").document(userId)
        var status = ""

//        val duration1 = intent.getStringExtra("duration")
        myApp.countdownResult = intent.getStringExtra("return_status")
        lendRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    status = document.getString("status").toString()
                }
            }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            uploadImageToFirebaseStorage(imageBitmap) { isSuccess ->
                if(isSuccess){
                    btnscan.setOnClickListener {
                        val intent = Intent(applicationContext, ScanActivity::class.java)
                        intent.putExtra("return_status",myApp.countdownResult.toString())
//                        intent.putExtra("duration",duration1)
                        startActivity(intent)
                        finish()
                    }
                }

            }
        }


    }

    private fun uploadImageToFirebaseStorage(bitmap: Bitmap, onUploadComplete: (Boolean) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully
            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            onUploadComplete(true)
        }.addOnFailureListener { exception ->
            // Image upload failed
            Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            onUploadComplete(false)
        }
    }







}