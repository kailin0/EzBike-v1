package my.edu.tarc.ezbike_v1.adminSide.ui.admin

import FirebaseListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.adminSide.viewModel.BikeVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.BikeVMProvider
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVMProvider
import my.edu.tarc.ezbike_v1.databinding.FragmentAdminBinding
import my.edu.tarc.ezbike_v1.user_management.Entry

class AdminFragment : Fragment(), MenuProvider {

    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationVM: LocationVM
    private lateinit var bikeVM: BikeVM

//    private lateinit var firebaseListener: FirebaseListener
//    private lateinit var locationRef: DatabaseReference
//    private lateinit var bikeRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//            //do nth = disable back button function
//        }
//        setMenuVisibility(true)


        var backPressedTime = 0L
        val BACK_PRESSED_INTERVAL = 2000 // 2 seconds
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime < BACK_PRESSED_INTERVAL) {
                    requireActivity().finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }

        })
        locationVM = LocationVMProvider.getViewModel(requireActivity())
        bikeVM = BikeVMProvider.getViewModel(requireActivity())

//        firebaseListener = FirebaseListener(locationVM, bikeVM)
//
//        locationRef = FirebaseDatabase.getInstance().getReference("locationID")
//        bikeRef = FirebaseDatabase.getInstance().getReference("bikeSerialNO")
//        locationRef.addValueEventListener(firebaseListener)
//        bikeRef.addValueEventListener(firebaseListener)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBike.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adminFragment_to_nav_bikeFragment)
        }

        binding.btnLocation.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adminFragment_to_nav_locationFragment)
        }
    }


    //    override fun onStop() {
//        super.onStop()
//        // Unregister the FirebaseListener and stop listening to Firebase data changes
//        locationRef.removeEventListener(firebaseListener)
//        bikeRef.removeEventListener(firebaseListener)
//    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater){
        menuInflater.inflate(R.menu.menu2, menu)
        menu.findItem(R.id.action_logout).isVisible = false;
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == android.R.id.home){
            findNavController().navigateUp()
        }
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        locationRef.removeEventListener(firebaseListener)
//        bikeRef.removeEventListener(firebaseListener)
    }
}
