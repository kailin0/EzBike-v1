package my.edu.tarc.ezbike_v1.adminSide.ui.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import my.edu.tarc.ezbike_v1.adminSide.viewModel.isDelete
import my.edu.tarc.ezbike_v1.adminSide.viewModel.isSuccess
import my.edu.tarc.ezbike_v1.adminSide.viewModel.isUpdate
import my.edu.tarc.ezbike_v1.databinding.FragmentLocationSuccessBinding

class LocationSuccessFragment : Fragment() {

    private var _binding: FragmentLocationSuccessBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLocationSuccessBinding.inflate(inflater,container,false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
                if(isEdit){
                    findNavController().popBackStack()
                }

            }

        })

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isSuccess && !isUpdate && !isDelete){
            binding.tvSuccess.isVisible=true
            binding.tvSuccessMsg.isVisible=true
            binding.imageView4.isVisible=true
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.imageView5.isVisible=false
            binding.tvUpdateMsg.isVisible=false
            binding.tvDeleteMsg.isVisible=false
        }else if (!isUpdate && !isSuccess && !isDelete){
            binding.tvFail.isVisible=true
            binding.tvFailMsg.isVisible=true
            binding.imageView5.isVisible=true
            binding.tvSuccess.isVisible=false
            binding.tvSuccessMsg.isVisible=false
            binding.imageView4.isVisible=false
            binding.tvUpdateMsg.isVisible=false
            binding.tvDeleteMsg.isVisible=false
        }else if (isUpdate && !isSuccess && !isDelete){
            binding.tvSuccess.isVisible=true
            binding.tvUpdateMsg.isVisible=true
            binding.imageView4.isVisible=true
            binding.tvSuccessMsg.isVisible=false
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.imageView5.isVisible=false
            binding.tvDeleteMsg.isVisible=false
        }

        if (isDelete && !isUpdate && !isSuccess){
            binding.tvSuccess.isVisible=true
            binding.tvDeleteMsg.isVisible=true
            binding.imageView4.isVisible=true
            binding.tvSuccessMsg.isVisible=false
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.imageView5.isVisible=false
            binding.tvUpdateMsg.isVisible=false
        }

        binding.btnBack.setOnClickListener {
//            findNavController().navigate(R.id.action_locationSuccessFragment_to_nav_locationFragment)
            binding.tvSuccess.isVisible=false
            binding.tvSuccessMsg.isVisible=false
            binding.imageView4.isVisible=false
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.imageView5.isVisible=false
            binding.tvUpdateMsg.isVisible=false
            binding.tvDeleteMsg.isVisible=false
            isDelete=false
            isSuccess=false
            isUpdate=false
            findNavController().popBackStack()
            if(isEdit){
                findNavController().popBackStack()
            }


        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}