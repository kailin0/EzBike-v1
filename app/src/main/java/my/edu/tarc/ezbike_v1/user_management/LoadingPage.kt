package my.edu.tarc.ezbike_v1.user_management

import android.content.Intent
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import my.edu.tarc.ezbike_v1.R

class LoadingPage : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_page)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val imageViewLoading: ImageView = findViewById(R.id.imageViewLoading)
        (imageViewLoading.drawable as? AnimatedImageDrawable)?.start()

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            val intent = Intent(this, Entry::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}