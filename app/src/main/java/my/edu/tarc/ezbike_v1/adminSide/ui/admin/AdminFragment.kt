package my.edu.tarc.ezbike_v1.adminSide.ui.admin

import FirebaseListener
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.adminSide.viewModel.BikeVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.BikeVMProvider
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVMProvider
import my.edu.tarc.ezbike_v1.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

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

//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//            //do nth = disable back button function
//        }
        setMenuVisibility(true)
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

        binding.FABtnLogout.setOnClickListener {
//            findNavController().navigate(R.id.action_nav_adminFragment_to_nav_loginFragment)
            findNavController().popBackStack()
        }



    }


    //    override fun onStop() {
//        super.onStop()
//        // Unregister the FirebaseListener and stop listening to Firebase data changes
//        locationRef.removeEventListener(firebaseListener)
//        bikeRef.removeEventListener(firebaseListener)
//    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        locationRef.removeEventListener(firebaseListener)
//        bikeRef.removeEventListener(firebaseListener)
    }
}
