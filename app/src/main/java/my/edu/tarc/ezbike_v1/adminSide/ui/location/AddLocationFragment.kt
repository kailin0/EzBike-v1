package my.edu.tarc.ezbike_v1.adminSide.ui.location

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.adminSide.db.LocationData
import my.edu.tarc.ezbike_v1.adminSide.db.locationDB
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVMFactory
import my.edu.tarc.ezbike_v1.databinding.FragmentAddLocationBinding
import kotlin.system.exitProcess

class AddLocationFragment : Fragment() {

    private var _binding: FragmentAddLocationBinding?= null
    private val binding get() = _binding!!

//    private lateinit var viewmodel : LocationVM
    private lateinit var locationVM: LocationVM

//    private lateinit var selectedLocation:LocationData

    private var locationName: String? = null
    private var locationAddress: String? = null
    private var locationid: String ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)

        if (isEdit){
            binding.btnDelete.isVisible=true
            binding.btnSave.isVisible=true
            binding.btnAddNewLocation.isVisible=false
            locationid = arguments?.getString("location_id")
            locationName = arguments?.getString("location_name")
            locationAddress = arguments?.getString("location_address")
            binding.etptLocaationName.setText(locationName)
            binding.etpaLocationAddress.setText(locationAddress)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }

        })

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = locationDB.getInstance(requireActivity().application).locationDao()
        val factory = LocationVMFactory(dao)
        locationVM = ViewModelProvider(requireActivity()).get(LocationVM::class.java)

        binding.btnAddNewLocation.setOnClickListener {
            if(binding.etptLocaationName.text.toString().isNotBlank() && binding.etpaLocationAddress.text.toString().isNotBlank()){
                saveLocationData()
                clearInput()
            }else{
                Toast.makeText(requireContext(),"Please fill up the Location Name and Address",Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnSave.setOnClickListener {
            if(binding.etptLocaationName.text.toString().isNotBlank() && binding.etpaLocationAddress.text.toString().isNotBlank()){
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are you sure to update the location?")
                builder.setNegativeButton("CANCEL") { dialog, _ ->
                    // handle negative button click
                    dialog.cancel()
                }
                builder.setPositiveButton("CONFIRM") { _, _ ->
                    // handle positive button click
                    updateLocationData()
                    findNavController().navigate(R.id.action_nav_addLocationFragment_to_locationSuccessFragment)
                }
                builder.show()
            }else{
                Toast.makeText(requireContext(),"Please fill up the Location Name and Address",Toast.LENGTH_SHORT).show()
            }


        }
        binding.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure to delete the location?")
            builder.setNegativeButton("CANCEL") { dialog, _ ->
                // handle negative button click
                dialog.cancel()
            }
            builder.setPositiveButton("CONFIRM") { _, _ ->
                // handle positive button click
                deleteLocationData()
                findNavController().navigate(R.id.action_nav_addLocationFragment_to_locationSuccessFragment)
            }
            builder.show()
        }


    }

    //clear all data in db
//    private fun clearAll(){
//        locationVM.deleteAll()
//        locationVM.refreshLocationList()
//        locationVM.deleteAllfromFirebase()
//    }
    //function to add location to database
    private fun saveLocationData(){
        locationVM.insertLocation(
            LocationData(
                0,
                binding.etptLocaationName.text.toString(),
                binding.etpaLocationAddress.text.toString()
            )
        )
        locationVM.refreshLocationList()
        locationVM.saveLocationsToFirebase()
//        Toast.makeText(requireContext(),"Location added successfully",Toast.LENGTH_SHORT).show()
        locationVM.toastMessage.observe(viewLifecycleOwner, Observer {
//            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        findNavController().navigate(R.id.action_nav_addLocationFragment_to_locationSuccessFragment)

    }

    private fun updateLocationData(){
        locationVM.updateLocation(
            LocationData(
//                selectedLocation.locationID,
                locationid!!.toInt(),
                binding.etptLocaationName.text.toString(),
                binding.etpaLocationAddress.text.toString()
            )
        )
//        Toast.makeText(requireContext(),"Successfully updated",Toast.LENGTH_SHORT).show()
    }

    private fun deleteLocationData(){
        locationVM.deleteLocation(
            LocationData(
//                selectedLocation.locationID,
                locationid!!.toInt(),
                binding.etptLocaationName.text.toString(),
                binding.etpaLocationAddress.text.toString()
            )
        )
//        locationVM.deleteAll()
//        Toast.makeText(requireContext(),"Successfully delete",Toast.LENGTH_SHORT).show()

    }

    //clear input after press "Add" button
    private fun clearInput(){
        binding.etptLocaationName.text.clear()
        binding.etpaLocationAddress.text.clear()
    }


}
