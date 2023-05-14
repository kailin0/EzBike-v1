package my.edu.tarc.ezbike_v1.adminSide.viewModel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.ezbike_v1.adminSide.db.locationDB

class BikeVMProvider {

    companion object {

        private var viewModel: BikeVM? = null

        fun getViewModel(activity: FragmentActivity): BikeVM {
            viewModel?.let {
                return it
            } ?: run {
                val dao = locationDB.getInstance(activity.application).bikeDao()
                val factory = BikeVMFactory(dao)
                viewModel = ViewModelProvider(activity, factory).get(BikeVM::class.java)
                return viewModel!!
            }
        }
    }
}