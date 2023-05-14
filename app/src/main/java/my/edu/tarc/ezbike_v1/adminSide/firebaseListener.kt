import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.ezbike_v1.adminSide.db.BikeData
import my.edu.tarc.ezbike_v1.adminSide.db.LocationData
import my.edu.tarc.ezbike_v1.adminSide.viewModel.BikeVM
import my.edu.tarc.ezbike_v1.adminSide.viewModel.LocationVM

class FirebaseListener(
    private val locationDatabase: LocationVM,
    private val bikeDatabase: BikeVM
) : ValueEventListener {

    override fun onDataChange(snapshot: DataSnapshot) {
        // Get the new data from Firebase
        val locationData = snapshot.child("locationID").getValue(LocationData::class.java)
        val bikeData = snapshot.child("bikeSerialNO").getValue(BikeData::class.java)

        // Update your app accordingly
        if (locationData != null) {
            locationDatabase.updateLocation(locationData)
        }
        bikeDatabase.updateBike(bikeData)
    }

    override fun onCancelled(error: DatabaseError) {
        // Handle any errors that occur
        Log.e("FirebaseListener", "Error listening to Firebase data: ${error.message}")
    }
}