package my.edu.tarc.ezbike_v1.user_management

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.ezbike_v1.MainActivity
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.adminSide.ui.admin.AdminHomeActivity
import my.edu.tarc.ezbike_v1.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        val forgetTextView: TextView = binding.textViewForget
        forgetTextView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_login_to_nav_forgetPsw)
        }

        val createTextView: TextView = binding.textViewCreate
        createTextView.setOnClickListener {
            val intent = Intent(activity, Registration::class.java)
            startActivity(intent)
        }

        val loginBtn: Button = binding.buttonLogin
        loginBtn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val psw = binding.editTextPsw.text.toString()

            if(email.isNotEmpty() && psw.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email, psw).addOnCompleteListener{
                    if(it.isSuccessful) {
                        if(email == "ezbike111@gmail.com"){
                            val intent = Intent(activity, AdminHomeActivity::class.java)
                            startActivity(intent)
                        } else{
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                        }

                    } else{
                        Toast.makeText(activity, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else{
                Toast.makeText(activity, "All fields have to fill in", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}