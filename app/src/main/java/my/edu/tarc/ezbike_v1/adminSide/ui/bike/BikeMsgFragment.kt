package my.edu.tarc.ezbike_v1.adminSide.ui.bike

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import my.edu.tarc.ezbike_v1.adminSide.viewModel.isBikeDelete
import my.edu.tarc.ezbike_v1.adminSide.viewModel.isBikeSuccess
import my.edu.tarc.ezbike_v1.adminSide.viewModel.isBikeUpdate
import my.edu.tarc.ezbike_v1.databinding.FragmentBikeMsgBinding

class BikeMsgFragment : Fragment() {

    private var _binding: FragmentBikeMsgBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBikeMsgBinding.inflate(inflater,container,false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
                if(isBikeEdit){
                    findNavController().popBackStack()
                }
            }

        })

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isBikeSuccess && !isBikeUpdate && !isBikeDelete){
            binding.tvSuccess.isVisible=true
            binding.tvSuccessMsg.isVisible=true
            binding.ivBikeSuccess.isVisible=true
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.ivBikeFail.isVisible=false
            binding.tvUpdateMsg.isVisible=false
            binding.tvDeleteMsg.isVisible=false
        }else if (!isBikeUpdate && !isBikeSuccess && !isBikeDelete){
            binding.tvFail.isVisible=true
            binding.tvFailMsg.isVisible=true
            binding.ivBikeFail.isVisible=true
            binding.tvSuccess.isVisible=false
            binding.tvSuccessMsg.isVisible=false
            binding.ivBikeSuccess.isVisible=false
            binding.tvUpdateMsg.isVisible=false
            binding.tvDeleteMsg.isVisible=false
        }else if (isBikeUpdate && !isBikeSuccess && !isBikeDelete){
            binding.tvSuccess.isVisible=true
            binding.tvUpdateMsg.isVisible=true
            binding.ivBikeSuccess.isVisible=true
            binding.tvSuccessMsg.isVisible=false
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.ivBikeFail.isVisible=false
            binding.tvDeleteMsg.isVisible=false
        }

        if (isBikeDelete && !isBikeUpdate && !isBikeSuccess){
            binding.tvSuccess.isVisible=true
            binding.tvDeleteMsg.isVisible=true
            binding.ivBikeSuccess.isVisible=true
            binding.tvSuccessMsg.isVisible=false
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.ivBikeFail.isVisible=false
            binding.tvUpdateMsg.isVisible=false
        }

        binding.btnBackBike.setOnClickListener {
//            findNavController().navigate(R.id.action_locationSuccessFragment_to_nav_locationFragment)
            binding.tvSuccess.isVisible=false
            binding.tvSuccessMsg.isVisible=false
            binding.ivBikeSuccess.isVisible=false
            binding.tvFail.isVisible=false
            binding.tvFailMsg.isVisible=false
            binding.ivBikeFail.isVisible=false
            binding.tvUpdateMsg.isVisible=false
            binding.tvDeleteMsg.isVisible=false
            isBikeDelete=false
            isBikeSuccess=false
            isBikeUpdate=false
            findNavController().popBackStack()
            if(isBikeEdit){
                findNavController().popBackStack()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}