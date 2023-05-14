package my.edu.tarc.ezbike_v1.adminSide.ui.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import my.edu.tarc.ezbike_v1.databinding.FragmentLocationBinding
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.adminSide.adaptor.locationRVAdaptor
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVMProvider

var isEdit = false

class LocationFragment : Fragment(), MenuProvider{

    private var _binding: FragmentLocationBinding?= null
    private val binding get() = _binding!!

    private lateinit var LocationRV : RecyclerView
    private lateinit var adaptor : locationRVAdaptor
    private lateinit var viewmodel : LocationVM
    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        isEdit=false

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()

            }

        })
        setMenuVisibility(false)


        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu2, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adaptor.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                adaptor.filter.filter(newText)
                if (newText.isNullOrEmpty()) {
                    adaptor.resetList()
                } else {
                    adaptor.filter.filter(newText)
                    adaptor.notifyDataSetChanged()
                }
                return true
            }
        })

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_search) {
//            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    adaptor.filter.filter(query)
//                    adaptor.notifyDataSetChanged()
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    adaptor.filter.filter(newText)
//                    adaptor.notifyDataSetChanged()
//                    return true
//                }
//
//            })
        }
        else{
            searchView?.onActionViewCollapsed()
        }
        return true
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.FABtnAddLocation.setOnClickListener {
            findNavController().navigate(R.id.action_nav_locationFragment_to_addLocationFragment)
        }

        LocationRV = binding.rvLocation
        viewmodel = LocationVMProvider.getViewModel(requireActivity())
        initRecyclerView()
//        displayLocationList()

    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        isEdit=false
//        _binding = null
//    }

    private fun initRecyclerView(){
        LocationRV.layoutManager = LinearLayoutManager(requireContext())
        adaptor = locationRVAdaptor{
//           it -> findNavController().navigate(R.id.action_nav_locationFragment_to_addLocationFragment)
            val bundle = Bundle()
            bundle.putString("location_id",it.locationID.toString())
            bundle.putString("location_name", it.locationName)
            bundle.putString("location_address", it.locationAddress)
            val navController = Navigation.findNavController(binding.root)
            navController.navigate(R.id.action_nav_locationFragment_to_addLocationFragment,bundle)
            isEdit=true
        }
        LocationRV.adapter = adaptor

        displayLocationList()
    }

    private fun displayLocationList(){
        viewmodel.locationList.observe(viewLifecycleOwner, Observer {
//            oriList = it
//            Log.d("LocationFragment", "Original List size: ${oriList.size}")
            adaptor.setList(it)
            adaptor.notifyDataSetChanged()
        })
    }

//    private fun resetList() {
//        adaptor.resetList(oriList!!)
//    }

//    override fun onResume() {
//        super.onResume()
//        resetList()
//    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().invalidateOptionsMenu()
    }
}
