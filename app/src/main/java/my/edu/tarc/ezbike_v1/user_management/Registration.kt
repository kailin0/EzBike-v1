package my.edu.tarc.ezbike_v1.user_management

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
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
            val dateDialogFragment = DateDialogFragment { year, month, day ->
                val dobStr = String.format("%02d/%02d/%d", day, month + 1, year)
                val dob = getInstance()
                with(dob){
                    set(YEAR, year)
                    set(MONTH, month)
                    set(DAY_OF_MONTH, day)
                }
                val today = getInstance()
                val age = daysBetween(dob, today).div(365)
                if(age in 13..80){
                    binding.buttonDob.text = dobStr
                } else if(age > 80){
                    Toast.makeText(this, "Please make sure you are 80 years old and below", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please make sure you are 13 years old and above", Toast.LENGTH_SHORT).show()
                }
            }
            dateDialogFragment.show(supportFragmentManager, "DateDialog")
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
                        storeUserPref(name, email, contactNo, dob)
                        storeUserFirestore(name, email, contactNo, dob)

                    } else{
                        binding.editTextConfirmPsw.error = "Confirm password does not match"
                    }
                } else{
                    binding.editTextPhone.error = "Invalid contact no. format. Eg: 012-4567788"
                }
            } else {
                if(name.isEmpty())
                    binding.editTextName.error = "Name is required"
                if(email.isEmpty())
                    binding.editTextEmail.error ="Email is required"
                if(psw.isEmpty())
                    binding.editTextPsw.error = "Password is required"
                if(cPsw.isEmpty())
                    binding.editTextConfirmPsw.error = "Confirm password is required"
                if(contactNo.isEmpty())
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
            val c = getInstance()
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

    private fun daysBetween(startDate: Calendar, endDate: Calendar?): Long {
        val date = startDate.clone() as Calendar
        var daysBetween: Long = 0
        while (date.before(endDate)) {
            date.add(DAY_OF_MONTH, 1)
            daysBetween++
        }
        return daysBetween
    }


    private fun createUser(email: String, psw: String){
        firebaseAuth.createUserWithEmailAndPassword(email, psw).addOnCompleteListener{
            if(it.isSuccessful){

            } else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun storeUserPref(name: String, email: String, contactNo: String, dob: String) {
        val sharedPreferences = getSharedPreferences("preference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply{
            putString("name", name)
            putString("email", email)
            putString("contactNo", contactNo)
            putString("dob", dob)
        }.apply()
    }

    private fun storeUserFirestore(name: String, email: String, contactNo: String, dob: String){

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