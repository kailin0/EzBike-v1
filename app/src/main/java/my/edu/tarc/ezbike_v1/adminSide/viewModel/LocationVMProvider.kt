package my.edu.tarc.ezbike_v1.adminSide.viewModel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import my.edu.tarc.ezbike_v1.adminSide.db.locationDB

class LocationVMProvider {
//    companion object {
//        fun getViewModel(context: Context): LocationVM {
//            val dao = locationDB.getInstance(context.applicationContext).locationDao()
//            val factory = LocationVMFactory(dao)
//            return ViewModelProvider(context as ViewModelStoreOwner, factory).get(LocationVM::class.java)
//        }
//    }

    companion object {

        private var viewModel: LocationVM? = null

        fun getViewModel(activity: FragmentActivity): LocationVM {
            viewModel?.let {
                return it
            } ?: run {
                val dao = locationDB.getInstance(activity.application).locationDao()
                val factory = LocationVMFactory(dao)
                viewModel = ViewModelProvider(activity, factory).get(LocationVM::class.java)
                return viewModel!!
            }
        }
    }
}