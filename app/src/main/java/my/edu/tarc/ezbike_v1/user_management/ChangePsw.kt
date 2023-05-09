package my.edu.tarc.ezbike_v1.user_management

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.ActivityChangePswBinding
import my.edu.tarc.ezbike_v1.databinding.ActivityRegistrationBinding

class ChangePsw : AppCompatActivity() {

    private lateinit var binding: ActivityChangePswBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePswBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val buttonSave: Button = binding.buttonSave

        buttonSave.setOnClickListener{
            val currentPsw = binding.editTextPsw.text.toString()
            val newPsw = binding.editTextNewPsw.text.toString()
            val confirmPsw = binding.editTextConfirmPsw.text.toString()

            if(currentPsw.isNotEmpty() && newPsw.isNotEmpty() && confirmPsw.isNotEmpty()){

                if(newPsw == confirmPsw){
                    val user = firebaseAuth.currentUser
                    if(user != null){
                        val credential = EmailAuthProvider.getCredential(user.email!!, currentPsw)
                        user?.reauthenticate(credential)?.addOnCompleteListener{
                                if(it.isSuccessful){
                                    Toast.makeText(this, "Re-Authentication updated", Toast.LENGTH_SHORT).show()

                                    user?.updatePassword(newPsw)?.addOnCompleteListener{ task ->
                                        if(task.isSuccessful){
                                            Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show()
                                        } else{
                                            Toast.makeText(this, "Password fail to change", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else{
                                    Toast.makeText(this, "Re-Authentication fail", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else{
                        val intent = Intent(this, Entry::class.java)
                        startActivity(intent)
                    }
                } else{
                    binding.editTextConfirmPsw.error ="Confirm password does not match"
                }
            } else{
                if(currentPsw.isNullOrEmpty())
                    binding.editTextPsw.error = "Password is required"
                if(newPsw.isNullOrEmpty())
                    binding.editTextNewPsw.error ="New Password is required"
                if(confirmPsw.isNullOrEmpty())
                    binding.editTextConfirmPsw.error = "Current Password is required"
            }
        }
    }
}