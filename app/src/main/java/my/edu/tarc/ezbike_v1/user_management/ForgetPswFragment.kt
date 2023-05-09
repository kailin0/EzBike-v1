package my.edu.tarc.ezbike_v1.user_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentForgetPswBinding

class ForgetPswFragment : Fragment() {

    private lateinit var binding: FragmentForgetPswBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentForgetPswBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        val btnSubmit = binding.buttonSubmit
        btnSubmit.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Please check your email", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_nav_forgetPsw_to_nav_login)
                }
                .addOnFailureListener{
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }

}