package com.android.nfc.system.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationData")
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo
    var positionName: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    ) {
    constructor(positionName: String, longitude: Double, latitude: Double) : this() {
        this.positionName = positionName
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun toString(): String {
        return "id=$id, positionName=$positionName, longitude=$longitude, latitude=$latitude"
    }
}