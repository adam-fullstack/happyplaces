package com.adamfoerster.happyplaces.database

import androidx.room.*

@Dao
interface PlaceDAO {
    @Query("SELECT * FROM places")
    fun getAll(): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE id=:id LIMIT 1")
    fun findById(id: Int): PlaceEntity

    @Query("SELECT * FROM places WHERE title LIKE :first LIMIT 1")
    fun findByTitle(first: String): PlaceEntity

    @Insert
    fun insertAll(vararg users: PlaceEntity)

    @Update
    fun updateAll(vararg place: PlaceEntity)

    @Delete
    fun delete(user: PlaceEntity)
}