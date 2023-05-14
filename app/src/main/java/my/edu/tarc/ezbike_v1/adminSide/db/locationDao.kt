package my.edu.tarc.ezbike_v1.adminSide.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface locationDao {

    //function to insert location data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocation(locationData: LocationData)

    //function to update location data
    @Update
    suspend fun updateLocation(locationData: LocationData)

    //function to delete location data
    @Delete
    suspend fun deleteLocation(locationData: LocationData)

    //query function to get all location data
    @Query("SELECT * FROM Location_data_table")
    fun getallLocation():List<LocationData>
//    fun getallLocation():LiveData<List<LocationData>>

    @Query("SELECT COUNT(*) FROM Location_data_table WHERE location_name = :name AND location_address = :address")
    fun getLocationCount(name: String, address: String): Int

    @Query("DELETE FROM Location_data_table")
    suspend fun deleteAll()

    @Query("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Location_data_table'")
    fun resetId()

//    @Query("SELECT * FROM Location_data_table WHERE location_name LIKE :query OR location_address LIKE :query")
//    fun searchLocation(query: String): List<LocationData>

    @Query("SELECT * FROM Location_data_table WHERE location_ID = :id")
    fun getLocationById(id: Int): LocationData?
}