package com.adamfoerster.happyplaces.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "places")
data class PlaceEntity(
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Long,
    val longitude: Long
): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}