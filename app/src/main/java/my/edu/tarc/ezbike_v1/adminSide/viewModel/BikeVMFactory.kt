package my.edu.tarc.ezbike_v1.adminSide.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.ezbike_v1.adminSide.db.bikeDAO

class BikeVMFactory(private val dao: bikeDAO):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BikeVM::class.java)){
            return BikeVM(dao) as T
        }else{
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}
