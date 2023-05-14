package my.edu.tarc.ezbike_v1.customer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import my.edu.tarc.ezbike_v1.databinding.ActivityLendBinding
import java.time.Duration
import java.util.*


class LendActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLendBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scanbtn = binding.btnScanQR
        val spiner = binding.spinnerDuration



        scanbtn.setOnClickListener {

            val duration = when (spiner.selectedItemPosition) {
                0 -> Duration.ofMinutes(30).toMinutes()
                1 -> Duration.ofMinutes(60).toMinutes()
                2 -> Duration.ofMinutes(90).toMinutes()
                3 -> Duration.ofHours(2).toMinutes()
                4 -> Duration.ofHours(3).toMinutes()
                5 -> Duration.ofHours(4).toMinutes()
                6 -> Duration.ofMinutes(1).toMinutes()
                else -> 0 // set default value if no valid position is selected
            }

            val myApp = application as MyApp
            val durationApp = myApp.duration

            (application as MyApp).duration = duration
            val intent2 = Intent(this, ScanActivity::class.java)
            startActivity(intent2)



            val durationIntent = Intent()
            durationIntent.putExtra("lendDuration", duration)
            setResult(RESULT_OK, durationIntent)

            finish()



        }
    }

}