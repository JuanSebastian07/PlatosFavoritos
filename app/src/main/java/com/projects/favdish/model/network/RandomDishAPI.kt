package com.projects.favdish.model.network

import com.projects.favdish.model.entities.RandomDish
import com.projects.favdish.utils.Constants
//import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//RF
interface RandomDishAPI {

    //https://api.spoonacular.com/recipes/random?number=1&tags=vegetarian,dessert&apiKey=a909ba145f4f4e6a9be20a289736c26f
    /**
     * To Make a GET request.
     *
     * Pass the endpoint of the URL that is defined in the Constants.
     *
     *
     */
    @GET(Constants.API_ENDPOINT)
    suspend fun getRandomDish(
        // Query parámetro añadido a la URL. Esta es la mejor práctica en lugar de agregarlo como lo hemos hecho en el navegador.
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean,
        @Query(Constants.TAGS) tags: String,
        @Query(Constants.NUMBER) number: Int
    ): Response<RandomDish.Recipes>

}