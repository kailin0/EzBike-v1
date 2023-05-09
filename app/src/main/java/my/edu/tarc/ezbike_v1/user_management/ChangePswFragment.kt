package my.edu.tarc.ezbike_v1.user_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentChangePswBinding
import my.edu.tarc.ezbike_v1.databinding.FragmentHomeBinding

class ChangePswFragment : Fragment() {

    private lateinit var binding: FragmentChangePswBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePswBinding.inflate(inflater, container, false)


        return binding.root
    }
}