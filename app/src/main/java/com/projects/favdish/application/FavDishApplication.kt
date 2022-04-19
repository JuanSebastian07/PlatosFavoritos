package com.projects.favdish.application

import android.app.Application
import com.projects.favdish.model.database.FavDishRepository
import com.projects.favdish.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {

    private val database by lazy {
        FavDishRoomDatabase.getDatabase(this)
    }

    val repository by lazy {
        FavDishRepository(database.favDishDao())
    }
}