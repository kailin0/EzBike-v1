package my.edu.tarc.ezbike_v1.adminSide.ui.bike

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.adminSide.db.BikeData
import my.edu.tarc.ezbike_v1.adminSide.db.locationDB
import my.edu.tarc.ezbike_v1.adminSide.viewModel.BikeVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.BikeVMFactory
import my.edu.tarc.ezbike_v1.databinding.FragmentAddBikeBinding
import java.io.ByteArrayOutputStream
import java.util.*

class AddBikeFragment : Fragment() {

    private var _binding: FragmentAddBikeBinding?= null
    private val binding get() = _binding!!

    private lateinit var bikeVM: BikeVM

    private var bikeSerialNo: String? = null
    private var bikeAvailability: String? = null
//    private var bikebility: String? = null

    private val outputStream = ByteArrayOutputStream()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBikeBinding.inflate(inflater, container, false)

        if (isBikeEdit){
            binding.btnDeleteBike.isVisible=true
            binding.btnSaveBike.isVisible=true
            binding.btnAddNewBike.isVisible=false
            bikeSerialNo = arguments?.getString("bike_serial_no")
            bikeAvailability = arguments?.getString("bike_availability")
//            bikeQRCode = arguments?.getString("")
            binding.etptBikeSerialNo.setText(bikeSerialNo)
//            binding.etptBikeSerialNo.isClickable = false
//            binding.etptBikeSerialNo.isEnabled = false
//            binding.btnQR.isClickable = fasle
//            binding.btnQR.isEnabled = false
//            binding.ivQR.setImageDrawable()
//            binding.bikeAvailability.checkedRadioButtonId = binding.bikeAvailability
        }
        binding.ivQR.setImageDrawable(null)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }

        })

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = locationDB.getInstance(requireActivity().application).bikeDao()
        val factory = BikeVMFactory(dao)
        bikeVM = ViewModelProvider(requireActivity()).get(BikeVM::class.java)

        binding.btnQR.setOnClickListener {
            val text = binding.etptBikeSerialNo.text.toString()

            //c8 instance of encoder
            if (text.isNotBlank()) {
                val qrCodeBitmap = generateQRCode(text)
                binding.ivQR.isVisible=true
                binding.ivQR.setImageBitmap(qrCodeBitmap)
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }

        binding.btnAddNewBike.setOnClickListener {
            if(binding.etptBikeSerialNo.text.toString().isNotBlank() && binding.bikeAvailability.checkedRadioButtonId !=-1 && binding.ivQR.drawable != null){
                saveBikeData()
                clearInput()
            }else{
                Toast.makeText(requireContext(),"Please Fill up all the field",Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnSaveBike.setOnClickListener {
            if(binding.etptBikeSerialNo.text.toString().isNotBlank() && binding.bikeAvailability.checkedRadioButtonId !=-1 && binding.ivQR.drawable != null){
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are you sure to update the Bike?")
                builder.setNegativeButton("CANCEL") { dialog, _ ->
                    // handle negative button click
                    dialog.cancel()
                }
                builder.setPositiveButton("CONFIRM") { _, _ ->
                    // handle positive button click
                    updateBikeData()
                    findNavController().navigate(R.id.action_nav_addBikeFragment_to_nav_bikeMsgFragment)
                }
                builder.show()
            }else{
                Toast.makeText(requireContext(),"Please Fill up all the field",Toast.LENGTH_SHORT).show()
            }


        }
        binding.btnDeleteBike.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure to delete the Bike?")
            builder.setNegativeButton("CANCEL") { dialog, _ ->
                // handle negative button click
                dialog.cancel()
            }
            builder.setPositiveButton("CONFIRM") { _, _ ->
                // handle positive button click
                deleteBikeData()
                findNavController().navigate(R.id.action_nav_addBikeFragment_to_nav_bikeMsgFragment)

            }
            builder.show()
        }

    }

    private fun getAvailability(): String{
        val radioGroup = binding.bikeAvailability.checkedRadioButtonId
        val selectedRadioButton = binding.bikeAvailability.findViewById<RadioButton>(radioGroup)
        val selectedRadioButtonText = selectedRadioButton.text.toString()
        return selectedRadioButtonText
    }
    //function to add location to database
    private fun saveBikeData(){
//        val radioGroup = binding.bikeAvailability.checkedRadioButtonId
//        val selectedRadioButton = binding.bikeAvailability.findViewById<RadioButton>(radioGroup)
//        val selectedRadioButtonText = selectedRadioButton.text.toString()
        bikeVM.insertBike(
            BikeData(
                binding.etptBikeSerialNo.text.toString(),
                getAvailability(),
                outputStream.toByteArray()

            )
        )
        bikeVM.refreshBikeList()
        bikeVM.saveBikesToFirebase()

        findNavController().navigate(R.id.action_nav_addBikeFragment_to_nav_bikeMsgFragment)

    }

    private fun updateBikeData(){
        bikeVM.updateBike(
            BikeData(
                binding.etptBikeSerialNo.text.toString(),
                getAvailability(),
                outputStream.toByteArray()
            )
        )
//        Toast.makeText(requireContext(),"Successfully updated",Toast.LENGTH_SHORT).show()
    }

    private fun deleteBikeData(){
        bikeVM.deleteBike(
            BikeData(
                binding.etptBikeSerialNo.text.toString(),
                getAvailability(),
                outputStream.toByteArray()
            )
        )
//        locationVM.deleteAll()
//        Toast.makeText(requireContext(),"Successfully delete",Toast.LENGTH_SHORT).show()

    }

    //clear input after press "Add" button
    private fun clearInput(){
        binding.etptBikeSerialNo.text?.clear()
        binding.ivQR.isVisible=false
    }

    private fun generateQRCode(data: String): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix: BitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        return bitmap
    }


}
