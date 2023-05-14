package my.edu.tarc.ezbike_v1.adminSide.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy

@Dao
interface bikeDAO {

    //function to insert location data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBike(bikeData: BikeData)

    //function to update location data
    @Update
    suspend fun updateBike(bikeData: BikeData)

    //function to delete location data
    @Delete
    suspend fun deleteBike(bikeData: BikeData)

    //query function to get all location data
    @Query("SELECT * FROM Bike_data_table")
    fun getallBike():List<BikeData>

    @Query("SELECT COUNT(*) FROM Bike_data_table WHERE bike_serial_no = :serialNO")
    fun getBikeCount(serialNO: String): Int

    @Query("DELETE FROM Bike_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM Bike_data_table WHERE bike_serial_no = :serialNO")
    fun getBikeByserialNO(serialNO: Int): BikeData?

}