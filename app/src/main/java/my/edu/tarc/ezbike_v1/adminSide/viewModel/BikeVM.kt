package my.edu.tarc.ezbike_v1.adminSide.viewModel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import androidx.room.util.query
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.edu.tarc.ezbike_v1.adminSide.db.BikeData
import my.edu.tarc.ezbike_v1.adminSide.db.bikeDAO
import java.util.*
import kotlin.collections.HashMap

var isBikeSuccess = false
var isBikeUpdate = false
var isBikeDelete = false
class BikeVM(private val dao: bikeDAO) : ViewModel() {

    private var _bikeList = MutableLiveData<List<BikeData>>()
    val bikeList: LiveData<List<BikeData>> get() = _bikeList

    // Get a DatabaseReference object for the root node
    private val rootNodeRef = FirebaseDatabase.getInstance().reference
    // Create a new child node under the root node with a randomly generated ID
    private val firebaseRef = rootNodeRef.child("bikeSerialNO")

//    private var initialDataLoaded = false

    init {
        refreshBikeList()
    }

    fun refreshBikeList() {
        viewModelScope.launch {
            val bikes = withContext(Dispatchers.IO) {
                dao.getallBike()
            }
            _bikeList.postValue(bikes)

        }
    }

    fun insertBike(bikeData: BikeData?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var count = dao.getBikeCount(bikeData!!.bikeSerialNO)
                if(count==0){
                    if (bikeData != null) {
                        dao.addBike(bikeData)
                        saveBikesToFirebase()
                    }
                    isBikeSuccess=true
                }
                else{
                    isBikeSuccess=false
                }
            }
            refreshBikeList()
        }
    }

    fun updateBike(bikeData: BikeData?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var count = dao.getBikeCount(bikeData!!.bikeSerialNO)
                if(count==0){
                    if (bikeData != null) {
                        dao.updateBike(bikeData)
//                        _toastMessage.postValue("Location Added Successfully")
                        saveBikesToFirebase()
                        isBikeUpdate=true
                    }
                }
                else{
//                    _toastMessage.postValue("Location Already Exist")
                    isBikeUpdate=false
                }
            }
            refreshBikeList()
        }
    }

    fun deleteBike(bikeData: BikeData?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteBike(bikeData!!)
                val bikeRef = firebaseRef.child(bikeData.bikeSerialNO)
                bikeRef.removeValue()
                isBikeDelete=true
            }
            refreshBikeList()
        }
    }

    //save data to firebase
    @SuppressLint("RestrictedApi")
    fun saveBikesToFirebase() {
        val updates = HashMap<String, Any>()

        viewModelScope.launch(Dispatchers.IO) {
            val bikes = withContext(Dispatchers.IO) {
                dao.getallBike()
            }
            for (bike in bikes) {
                val bikeRef = firebaseRef.child(bike.bikeSerialNO.toString())

                val bikeUpdates = HashMap<String, Any>()
//                bikeUpdates["serial"] = bike.bikeSerialNO
                bikeUpdates["availability"] = bike.bikeAvailability
                bikeUpdates["QR"] = bike.bikeQR.toString()

                updates[bikeRef.path.toString()] = bikeUpdates
            }
            rootNodeRef.updateChildren(updates)
        }
    }


}
