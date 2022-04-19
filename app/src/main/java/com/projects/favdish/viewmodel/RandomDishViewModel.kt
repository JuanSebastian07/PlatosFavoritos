package com.projects.favdish.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.projects.favdish.model.entities.RandomDish
import com.projects.favdish.model.network.RandomDishApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RandomDishViewModel : ViewModel() {

    private val randomRecipeApiService = RandomDishApiService()

    /**
     * Creates a MutableLiveData with no value assigned to it.
     */
    val loadRandomDish = MutableStateFlow(true)
    suspend fun randomDishResponse() = MutableStateFlow(randomRecipeApiService.getRandomDish().body())
    val randomDishLoadingError = MutableStateFlow(true)


    fun endLoading(){
        loadRandomDish.value = false
    }

    fun restartLoading(){
        loadRandomDish.value = true
    }

    /*fun getRandomDishFromAPI() {
        // Define the value of the load random dish.
        loadRandomDish.value = true

        coroutineScope.launch {
            randomRecipeApiService.getRandomDish().body()
            Log.i("cor ->",randomRecipeApiService.getRandomDish().toString())//Asi solo vemos el link, el codigo de red y mas
            Log.i("cor ->",randomRecipeApiService.getRandomDish().body().toString())//Con el .body podemos ver la respuestas
        }

        loadRandomDish.value = true
        //Log.i("cor ->",randomRecipeApiService.getRandomDish().toString())
    }*/
}

