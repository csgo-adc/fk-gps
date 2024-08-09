package com.android.bluetooths.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {

    @Query("SELECT * FROM LocationData")
    fun queryAll():MutableList<LocationData>

    @Insert
    fun addLocation(vararg LocationData: LocationData)

    @Delete
    fun deleteLocation(vararg LocationData: LocationData)

    @Query("DELETE FROM LocationData")
    fun deleteAll()


}
