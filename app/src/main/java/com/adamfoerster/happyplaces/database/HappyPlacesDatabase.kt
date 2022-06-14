package com.adamfoerster.happyplaces.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlaceEntity::class], version = 2)
abstract class HappyPlacesDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDAO

    companion object {
        @Volatile
        private var INSTANCE: HappyPlacesDatabase? = null
        fun getInstance(context: Context): HappyPlacesDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HappyPlacesDatabase::class.java,
                        "happyplaces"
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}