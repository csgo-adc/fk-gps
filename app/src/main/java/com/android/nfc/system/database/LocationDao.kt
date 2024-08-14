package com.android.nfc.system.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationDao {

    @Query("SELECT * FROM LocationData")
    fun queryAll():MutableList<LocationData>

    @Insert
    fun addLocation(vararg LocationData: LocationData)

    @Update
    fun updateLocation(vararg LocationData: LocationData)

    @Delete
    fun deleteLocation(vararg LocationData: LocationData)

    @Query("DELETE FROM LocationData")
    fun deleteAll()


}
