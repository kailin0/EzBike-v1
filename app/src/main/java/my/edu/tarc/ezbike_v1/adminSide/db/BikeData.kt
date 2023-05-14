package my.edu.tarc.ezbike_v1.adminSide.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.ByteArrayOutputStream

@Entity(tableName = "Bike_data_table")
data class BikeData(
    @PrimaryKey()
    @ColumnInfo(name = "bike_serial_no")
    var bikeSerialNO:String,
    @ColumnInfo(name = "bike_availability")
    var bikeAvailability:String,
    @ColumnInfo(name = "bike_QR")
    var bikeQR:ByteArray
)
{
    // Secondary constructor for creating LocationData instances without an auto-generated ID
    constructor() : this("","","".toByteArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BikeData

        if (bikeSerialNO != other.bikeSerialNO) return false
        if (bikeAvailability != other.bikeAvailability) return false
        if (!bikeQR.contentEquals(other.bikeQR)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bikeSerialNO.hashCode()
        result = 31 * result + bikeAvailability.hashCode()
        result = 31 * result + bikeQR.contentHashCode()
        return result
    }
}
