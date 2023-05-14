package my.edu.tarc.ezbike_v1.customer

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        activity?.actionBar?.setDisplayHomeAsUpEnabled(false);

        val buttonLocation: ImageView = binding.buttonLocation
        buttonLocation.setOnClickListener{
            val intent = Intent(activity, ViewLocationActivity::class.java)
            startActivity(intent)
        }

        val buttonBike: ImageView = binding.buttonBike
        buttonBike.setOnClickListener{
            val intent = Intent(activity, ReturnActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

}