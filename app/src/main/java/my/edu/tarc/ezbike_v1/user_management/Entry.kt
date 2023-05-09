package my.edu.tarc.ezbike_v1.user_management

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.ezbike_v1.MainActivity
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.ActivityRegistrationBinding

class Entry : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    public override fun onStart() {
        super.onStart()

        firebaseAuth = Firebase.auth

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
    }
}