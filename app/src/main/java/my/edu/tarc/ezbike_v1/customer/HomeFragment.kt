package my.edu.tarc.ezbike_v1.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentHomeBinding
import my.edu.tarc.ezbike_v1.databinding.FragmentLoginBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val button: Button = binding.button2

        button.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_profileFragment)
        }

        return binding.root
    }

}