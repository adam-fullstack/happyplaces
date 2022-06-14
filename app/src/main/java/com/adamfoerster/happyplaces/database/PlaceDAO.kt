package com.adamfoerster.happyplaces.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaceDAO {
    @Query("SELECT * FROM places")
    fun getAll(): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE title LIKE :first LIMIT 1")
    fun findByTitle(first: String): PlaceEntity

    @Insert
    fun insertAll(vararg users: PlaceEntity)

    @Delete
    fun delete(user: PlaceEntity)
}