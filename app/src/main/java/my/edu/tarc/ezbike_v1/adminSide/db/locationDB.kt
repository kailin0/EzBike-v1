package my.edu.tarc.ezbike_v1.adminSide.db

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [LocationData::class, BikeData::class], version = 1, exportSchema = false)
abstract class locationDB : RoomDatabase() {
    abstract fun locationDao(): locationDao
    abstract fun bikeDao(): bikeDAO

    //create instance of db
    companion object{
        @Volatile
        private var INSTANCE : locationDB?= null
        fun getInstance(context:Context):locationDB{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,locationDB::class.java,"Location_database"
                    ).build()
                }
                return instance
            }
        }
    }

}