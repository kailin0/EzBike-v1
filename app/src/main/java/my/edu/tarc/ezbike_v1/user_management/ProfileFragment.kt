package my.edu.tarc.ezbike_v1.user_management

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.ezbike_v1.MainActivity
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentProfileBinding
import java.util.*
import java.util.Calendar.*
import kotlin.collections.HashMap

class ProfileFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        loadData()


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData(){
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        firebaseAuth = FirebaseAuth.getInstance()

        val textViewName: TextView = binding.textViewName
        val editTextName: EditText = binding.editTextName
        val buttonName: ImageButton = binding.imageButtonName
        val buttonName2: ImageButton = binding.imageButtonName2

        val buttonPsw: ImageButton = binding.imageButtonPsw

        val textViewContact: TextView = binding.textViewPhone
        val editTextContact: EditText = binding.editTextPhone
        val buttonContact: ImageButton = binding.imageButtonPhone
        val buttonContact2: ImageButton = binding.imageButtonPhone2

        val textViewDob: TextView = binding.textViewDob
        val buttonDob: ImageButton = binding.imageButtonDob

        val buttonCredibility: ImageButton = binding.imageButtonCredibility

        val buttonLogout: Button = binding.buttonLogout

        getUserData(textViewName, textViewContact, textViewDob)

        buttonName.setOnClickListener {
            textViewName.visibility = View.GONE
            editTextName.visibility = View.VISIBLE
            editTextName.requestFocus()
            buttonName.visibility = View.GONE
            buttonName2.visibility = View.VISIBLE
        }

        buttonContact.setOnClickListener{
            textViewContact.visibility = View.GONE
            editTextContact.visibility = View.VISIBLE
            editTextContact.requestFocus()
            buttonContact.visibility = View.GONE
            buttonContact2.visibility = View.VISIBLE
        }

        buttonName2.setOnClickListener {
            val name = editTextName.text
            if(name.isNotEmpty()){
                textViewName.text = name
                textViewName.visibility = View.VISIBLE
                editTextName.visibility = View.GONE
                buttonName.visibility = View.VISIBLE
                buttonName2.visibility = View.GONE

                storeUser(name.toString(), "name", "Name")
            } else{
                textViewName.visibility = View.VISIBLE
                editTextName.visibility = View.GONE
                buttonName.visibility = View.VISIBLE
                buttonName2.visibility = View.GONE
            }
        }

        buttonContact2.setOnClickListener {
            val contactNo = editTextContact.text
            val regexContact = Regex("^(01)[0-46-9]-[0-9]{7,8}\$")
            if(contactNo.isNotEmpty()){
                if(regexContact.matches(contactNo)){
                    textViewContact.text = contactNo
                    textViewContact.visibility = View.VISIBLE
                    editTextContact.visibility = View.GONE
                    buttonContact.visibility = View.VISIBLE
                    buttonContact2.visibility = View.GONE

                    storeUser(contactNo.toString(), "contactNo", "Contact No.")
                } else{
                    Toast.makeText(activity, "Invalid contact no. format. Eg: 012-4567788", Toast.LENGTH_SHORT).show()
                }
            } else{
                textViewContact.visibility = View.VISIBLE
                editTextContact.visibility = View.GONE
                buttonContact.visibility = View.VISIBLE
                buttonContact2.visibility = View.GONE
            }
        }

        buttonDob.setOnClickListener {
            val dateDialogFragment = Registration.DateDialogFragment { year, month, day ->
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
                    textViewDob.text = dobStr
                    storeUser(dobStr, "dob", "Date of Birth")
                } else if(age > 80){
                    Toast.makeText(activity, "Please make sure you are 80 years old and below", Toast.LENGTH_SHORT).show()
                    getUserData(textViewName, textViewContact, textViewDob)
                } else {
                    Toast.makeText(activity, "Please make sure you are 13 years old and above", Toast.LENGTH_SHORT).show()
                    getUserData(textViewName, textViewContact, textViewDob)
                }
            }
            dateDialogFragment.show(parentFragmentManager, "DateDialog")
        }

        buttonPsw.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_changePswFragment)
        }

        buttonCredibility.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_credibilityFragment)
        }

        buttonLogout.setOnClickListener{
            firebaseAuth.signOut()
            val intent = Intent(requireActivity(), Entry::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
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

    private fun getUserData(textViewName: TextView, textViewContact: TextView, textViewDob: TextView){

        db = FirebaseFirestore.getInstance()
        val email = firebaseAuth.currentUser?.email

        val sharedPreferences = activity?.getSharedPreferences("preference", Context.MODE_PRIVATE)

        if(sharedPreferences?.getString("email", "") == email){
            val name = sharedPreferences?.getString("name", "")
            val contactNo = sharedPreferences?.getString("contactNo", "")
            val dob = sharedPreferences?.getString("dob", "")

            if(name?.isNotEmpty() == true && contactNo?.isNotEmpty() == true && dob?.isNotEmpty() == true){
                textViewName.text = name
                textViewContact.text = contactNo
                textViewDob.text = dob

            } else {
                val userRef = db.collection("user").document(email!!)
                userRef.get()
                    .addOnSuccessListener {
                        if(it != null){
                            textViewName.text = it.data?.get("name")?.toString()
                            textViewContact.text = it.data?.get("contactNo")?.toString()
                            textViewDob.text = it.data?.get("dob")?.toString()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Data retrieval fail", Toast.LENGTH_SHORT).show()
                    }
            }
        } else{
            val userRef = db.collection("user").document(email!!)
            userRef.get()
                .addOnSuccessListener {
                    if(it != null){
                        textViewName.text = it.data?.get("name")?.toString()
                        textViewContact.text = it.data?.get("contactNo")?.toString()
                        textViewDob.text = it.data?.get("dob")?.toString()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Data retrieval fail", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun storeUser(input: String, fieldName: String, msg: String){

        val sharedPreferences = activity?.getSharedPreferences("preference", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val db = FirebaseFirestore.getInstance()
        val email = firebaseAuth.currentUser?.email

        val userRef = db.collection("user").document(email!!)
        userRef.get()
            .addOnSuccessListener {
                if(it != null){
                    val name = it.data?.get("name")?.toString()
                    val contactNo = it.data?.get("contactNo").toString()
                    val dob = it.data?.get("dob")?.toString()
                    val credibility = it.data?.get("credibility")?.toString()?.toInt()

                    var userMap: HashMap<String, Any?> = HashMap()
                    when (fieldName) {
                        "name" -> {
                            userMap = hashMapOf(
                                fieldName to input,
                                "email" to email,
                                "contactNo" to contactNo,
                                "dob" to dob,
                                "credibility" to credibility)

                            editor?.putString("name", input)
                            editor?.putString("email", email)
                            editor?.putString("contactNo", contactNo)
                            editor?.putString("dob", dob)
                            editor?.apply()

                        }
                        "contactNo" -> {
                            userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                fieldName to input,
                                "dob" to dob,
                                "credibility" to credibility)

                            editor?.putString("name", name)
                            editor?.putString("email", email)
                            editor?.putString("contactNo", input)
                            editor?.putString("dob", dob)
                            editor?.apply()

                        }
                        else -> {
                            userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "contactNo" to contactNo,
                                fieldName to input,
                                "credibility" to credibility)

                            editor?.putString("name", name)
                            editor?.putString("email", email)
                            editor?.putString("contactNo", contactNo)
                            editor?.putString("dob", input)
                            editor?.apply()
                        }
                    }


                    db.collection("user").document(email!!).set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "$msg updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(activity, "Fail to update $msg", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Fail to update $msg", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater){
        menu.findItem(R.id.action_profile).isVisible = false;
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == android.R.id.home){
            findNavController().navigateUp()
        }
        return true
    }

}