package my.edu.tarc.ezbike_v1.adminSide.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location_data_table")
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "location_ID")
    var locationID:Int = 0,
    @ColumnInfo(name = "location_name")
    var locationName:String,
    @ColumnInfo(name = "location_address")
    var locationAddress:String
)
{
    // Secondary constructor for creating LocationData instances without an auto-generated ID
    constructor() : this(0, "", "")
}
