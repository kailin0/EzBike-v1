package my.edu.tarc.ezbike_v1.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.databinding.ActivityLendOkBinding

class LendOkActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLendOkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLendOkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val desc = binding.yahooDesc
        val img = binding.imageView
        val ttl = binding.yahoTitle
        val btn = binding.btnbacklend
        val lendStatus = intent.getStringExtra("lend_status")
        val late = intent.getStringExtra("return_status")


        if(lendStatus == "fail"){
            desc.text = resources.getString(R.string.oops_desc)
            img.setImageResource(R.drawable.oops_logo)
            ttl.text = resources.getString(R.string.oops)
        }else if(lendStatus == "qrcode"){
            desc.text = resources.getString(R.string.invalidqr)
            img.setImageResource(R.drawable.oops_logo)
            ttl.text = resources.getString(R.string.oops)
        }else if(lendStatus == "mainten"){
            desc.text = resources.getString(R.string.mainten)
            img.setImageResource(R.drawable.oops_logo)
            ttl.text = resources.getString(R.string.success)
        }else if(lendStatus == "return" && late.toString() == "return_late"){
            desc.text = resources.getString(R.string.tq_overtime)
            img.setImageResource(R.drawable.yahoo_logo)
            ttl.text = resources.getString(R.string.success)
        }else if(lendStatus == "return" && late.toString() != "return_late"){
            desc.text = resources.getString(R.string.tq)
            img.setImageResource(R.drawable.yahoo_logo)
            ttl.text = resources.getString(R.string.success)
        }else if(lendStatus == "not_ur"){
            desc.text = resources.getString(R.string.not_ur)
            img.setImageResource(R.drawable.oops_logo)
            ttl.text = resources.getString(R.string.oops)
        }
        else{
            desc.text = resources.getString(R.string.yahoo_desc)
            img.setImageResource(R.drawable.yahoo_logo)
            ttl.text = resources.getString(R.string.yahoo)
        }

        btn.setOnClickListener{
            finish()
        }
    }

}