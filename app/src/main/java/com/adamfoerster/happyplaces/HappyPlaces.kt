package com.adamfoerster.happyplaces
import android.app.Application
import com.adamfoerster.happyplaces.database.HappyPlacesDatabase

class HappyPlaces: Application() {
    val db by lazy{
        HappyPlacesDatabase.getInstance(this)
    }
}