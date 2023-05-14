package my.edu.tarc.ezbike_v1.adminSide.ui.admin

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.ezbike_v1.R
import my.edu.tarc.ezbike_v1.user_management.Entry

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        firebaseAuth = FirebaseAuth.getInstance()

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.lightBrown)))
        supportActionBar!!.elevation = 0F
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //for up button
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_adminFragment, R.id.nav_locationFragment, R.id.nav_bikeFragment,
                R.id.nav_addLocationFragment, R.id.nav_addBikeFragment,
                R.id.nav_locationSuccessFragment, R.id.nav_bikeMsgFragment
            )
        )

        val navController = findNavController(R.id.nav_fragment_admin)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_adminFragment || destination.id == R.id.nav_locationSuccessFragment ||
                destination.id == R.id.nav_bikeMsgFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            }
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu2, menu)
        menu.findItem(R.id.action_search).isVisible = false;

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_logout){
            firebaseAuth.signOut()
            val intent = Intent(this, Entry::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_fragment_admin)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}