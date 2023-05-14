package my.edu.tarc.ezbike_v1.user_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentCredibilityBinding

class CredibilityFragment : Fragment() {

    private lateinit var binding: FragmentCredibilityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCredibilityBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser?.email

        val userRef = db.collection("user").document(email!!)
        userRef.get()
            .addOnSuccessListener {
                if(it != null){
                    binding.textViewCreditNo.text = it.getLong("credibility")?.toInt().toString()
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Data retrieval fail", Toast.LENGTH_SHORT).show()
            }

        return binding.root
    }
}