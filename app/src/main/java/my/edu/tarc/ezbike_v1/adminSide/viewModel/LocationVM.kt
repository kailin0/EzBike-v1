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
import my.edu.tarc.ezbike_v1.adminSide.db.LocationData
import my.edu.tarc.ezbike_v1.adminSide.db.locationDao
import java.util.*
import kotlin.collections.HashMap

var isSuccess = false
var isUpdate = false
var isDelete = false
class LocationVM(private val dao: locationDao) : ViewModel() {

    private var _locationList = MutableLiveData<List<LocationData>>()
    val locationList: LiveData<List<LocationData>> get() = _locationList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage get() = _toastMessage

    // Get a DatabaseReference object for the root node
    private val rootNodeRef = FirebaseDatabase.getInstance().reference
    // Create a new child node under the root node with a randomly generated ID
    private val firebaseRef = rootNodeRef.child("locationID")

//    private var initialDataLoaded = false



    init {
        refreshLocationList()
    }

    fun refreshLocationList() {
        viewModelScope.launch {
            val locations = withContext(Dispatchers.IO) {
                dao.getallLocation()
            }
            _locationList.postValue(locations)

        }
    }

//    fun synchronizeData() {
//        // Load data from Room database
//        val localData = dao.getallLocation()
//
//        // Listen for changes in Firebase database
//        firebaseRef.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                // Get the location data from Firebase
//                val firebaseData = dataSnapshot.getValue(LocationData::class.java)
//
//                // Check if the location already exists in the local database
//                val existingData = localData.find { it.locationID == firebaseData?.locationID }
//
//                if (existingData != null) {
//                    // Update the existing data in the local database
//                    viewModelScope.launch(Dispatchers.IO) {
//                        dao.updateLocation(firebaseData!!)
//                    }
//                } else {
//                    // Insert the new data into the local database
//                    viewModelScope.launch(Dispatchers.IO) {
//                        dao.addLocation(firebaseData!!)
//                    }
//                }
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                // Get the location data from Firebase
//                val firebaseData = dataSnapshot.getValue(LocationData::class.java)
//
//                // Update the corresponding data in the local database
//                viewModelScope.launch(Dispatchers.IO) {
//                    dao.updateLocation(firebaseData!!)
//                }
//            }
//
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                // Get the location data from Firebase
//                val firebaseData = dataSnapshot.getValue(LocationData::class.java)
//
//                // Delete the corresponding data from the local database
//                viewModelScope.launch(Dispatchers.IO) {
//                    dao.deleteLocation(firebaseData!!)
//                }
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                // Not implemented
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(TAG, "Listener was cancelled", databaseError.toException())
//            }
//        })
//    }



    fun insertLocation(locationData: LocationData?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var count = dao.getLocationCount(locationData!!.locationName,locationData!!.locationAddress)
                if(count==0){
                    if (locationData != null) {
                        dao.addLocation(locationData)
                        saveLocationsToFirebase()
//                        _toastMessage.postValue("Location Added Successfully")
                    }
                    isSuccess=true
                }
                else{
//                    _toastMessage.postValue("Location Already Exist")
                    isSuccess=false
                }
            }
            refreshLocationList()
        }
    }

    fun updateLocation(locationData: LocationData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var count = dao.getLocationCount(locationData!!.locationName,locationData!!.locationAddress)
                if(count==0){
                    if (locationData != null) {
                        dao.updateLocation(locationData)
//                        _toastMessage.postValue("Location Added Successfully")
                        saveLocationsToFirebase()
                        isUpdate=true
                    }
                }
                else{
//                    _toastMessage.postValue("Location Already Exist")
                    isUpdate=false
                }
            }
            refreshLocationList()
        }
    }

    fun deleteLocation(locationData: LocationData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteLocation(locationData)
                val locationRef = firebaseRef.child(locationData.locationID.toString())
                locationRef.removeValue()
                isDelete=true
                dao.resetId()
            }
            refreshLocationList()
        }
    }

//    fun deleteAll() {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                dao.deleteAll()
//                dao.resetId()
//            }
//            refreshLocationList()
//        }
//    }


    //save data to firebase
    @SuppressLint("RestrictedApi")
    fun saveLocationsToFirebase() {
        val updates = HashMap<String, Any>()

        viewModelScope.launch(Dispatchers.IO) {
            val locations = withContext(Dispatchers.IO) {
                dao.getallLocation()
            }
            for (location in locations) {
                val locationRef = firebaseRef.child(location.locationID.toString())

                val locationUpdates = HashMap<String, Any>()
                locationUpdates["name"] = location.locationName
                locationUpdates["address"] = location.locationAddress

                updates[locationRef.path.toString()] = locationUpdates
            }
            rootNodeRef.updateChildren(updates)
        }
    }


}
