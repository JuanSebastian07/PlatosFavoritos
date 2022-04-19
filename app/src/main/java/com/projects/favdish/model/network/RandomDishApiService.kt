package com.projects.favdish.model.network

import com.projects.favdish.model.entities.RandomDish
import com.projects.favdish.utils.Constants
//import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//RF
class RandomDishApiService {

    //Esto para tener como valor determinado en la variable corutineScope en el hilo principal o interfaz de usuario
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private val api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
         //Esta es una de las librerias que implementamos al principio y es lo que no va hacer la conversion de json a nuestro response RandomDish
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RandomDishAPI::class.java)


    suspend fun getRandomDish(): Response<RandomDish.Recipes> {
        return api.getRandomDish(
            Constants.API_KEY_VALUE,
            Constants.LIMIT_LICENSE_VALUE,
            Constants.TAGS_VALUE,
            Constants.NUMBER_VALUE
        )
    }

}
