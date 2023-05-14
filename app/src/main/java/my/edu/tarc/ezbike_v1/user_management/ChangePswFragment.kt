package my.edu.tarc.ezbike_v1.user_management

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentChangePswBinding


class ChangePswFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentChangePswBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePswBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
                                Toast.makeText(activity, "Re-Authentication updated", Toast.LENGTH_SHORT).show()

                                user?.updatePassword(newPsw)?.addOnCompleteListener{ task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(activity, "Password changed", Toast.LENGTH_SHORT).show()
                                    } else{
                                        Toast.makeText(activity, "Password fail to change", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else{
                                Toast.makeText(activity, "Re-Authentication fail", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else{
                        val intent = Intent(activity, Entry::class.java)
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

        return binding.root
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

