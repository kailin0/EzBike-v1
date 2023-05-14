package my.edu.tarc.ezbike_v1.adminSide.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.ezbike_v1.adminSide.db.locationDao

class LocationVMFactory(private val dao: locationDao):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LocationVM::class.java)){
            return LocationVM(dao) as T
        }else{
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}
