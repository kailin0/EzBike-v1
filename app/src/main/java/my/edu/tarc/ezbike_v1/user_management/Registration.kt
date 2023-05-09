package my.edu.tarc.ezbike_v1.user_management

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.ezbike_v1.databinding.ActivityRegistrationBinding
import java.util.*
import java.util.Calendar.*

class Registration : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth

    //Change to loading page later
//    public override fun onStart() {
//        super.onStart()
//
//        firebaseAuth = Firebase.auth
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth

        val loginTextView: TextView = binding.textViewLogin
        loginTextView.setOnClickListener {
            val intent = Intent(this, Entry::class.java)
            startActivity(intent)
        }

        val dobBtn = binding.buttonDob
        dobBtn.setOnClickListener {
            val dateDialogFragment = DateDialogFragment{
                    year, month, day ->
                binding.buttonDob.text = String.format("%02d/%02d/%d", day, month+1, year)
            }
            dateDialogFragment.show(supportFragmentManager,
                "DateDialog")
        }

        val createBtn: Button = binding.buttonCreate
        createBtn.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val psw = binding.editTextPsw.text.toString()
            val cPsw = binding.editTextConfirmPsw.text.toString()
            val contactNo = binding.editTextPhone.text.toString()
            val regexContact = Regex("^(01)[0-46-9]-[0-9]{7,8}\$")
            val dob = binding.buttonDob.text.toString()
            val regexDob = """^\d{2}/\d{2}/\d{4}$""".toRegex()

            if(name.isNotEmpty() && email.isNotEmpty() && psw.isNotEmpty() && cPsw.isNotEmpty()
                && contactNo.isNotEmpty() && regexDob.matches(dob)){
                if(regexContact.matches(contactNo)){
                    if(psw == cPsw){
                        createUser(email, psw)
                        storeUser(name, email, contactNo, dob)

                    } else{
                        binding.editTextConfirmPsw.error = "Confirm password does not match"
                    }
                } else{
                    binding.editTextPhone.error = "Invalid contact no. format. Eg: 012-4567788"
                }
            } else {
                if(name.isNullOrEmpty())
                    binding.editTextName.error = "Name is required"
                if(email.isNullOrEmpty())
                    binding.editTextEmail.error ="Email is required"
                if(psw.isNullOrEmpty())
                    binding.editTextPsw.error = "Password is required"
                if(cPsw.isNullOrEmpty())
                    binding.editTextConfirmPsw.error = "Confirm password is required"
                if(contactNo.isNullOrEmpty())
                    binding.editTextPhone.error = "Contact No. is required"
                if(!(regexDob.matches(dob)))
                    binding.buttonDob.error = "Date of birth is required"
            }
        }
    }

    class DateDialogFragment(
        val dateSetListener:(year: Int, month: Int, day: Int) -> Unit): DialogFragment(),
        DatePickerDialog.OnDateSetListener{
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(YEAR)
            val month = c.get(MONTH)
            val day = c.get(DAY_OF_MONTH)
            return DatePickerDialog(requireContext(), this,
                year, month, day)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            dateSetListener(year, month, dayOfMonth)
        }
    }

    private fun createUser(email: String, psw: String){
        firebaseAuth.createUserWithEmailAndPassword(email, psw).addOnCompleteListener{
            if(it.isSuccessful){

            } else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun storeUser(name: String, email: String, contactNo: String, dob: String){

        val db = FirebaseFirestore.getInstance()

        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "contactNo" to contactNo,
            "dob" to dob,
            "credibility" to 100
        )

        db.collection("user").document(email).set(userMap)
            .addOnSuccessListener {
            Toast.makeText(this, "Thanks for register", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Entry::class.java)
            startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Register Fail", Toast.LENGTH_SHORT).show()
            }

    }
}