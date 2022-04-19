package com.projects.favdish.model.database

import androidx.annotation.WorkerThread
import com.projects.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

/**
 * Un Repositorio gestiona las consultas y le permite utilizar múltiples backend.
 *
 * El DAO se pasa al constructor del repositorio en lugar de a toda la base de datos.
 * Esto se debe a que solo necesita acceso al DAO, ya que el DAO contiene todos los métodos de lectura/escritura para la base de datos.
 * No hay necesidad de exponer toda la base de datos al repositorio.
 *
 * @param favDishDao - Pase el FavDishDao como parámetro.
 */
class FavDishRepository(private val favDishDao : FavDishDao){

    /**
     * De forma predeterminada, Room ejecuta consultas suspendidas fuera del hilo principal, por lo tanto, no necesitamos
     * implementar cualquier otra cosa para asegurarnos de que no estamos haciendo un trabajo de base de datos de larga duración
     * fuera del hilo principal.
     */
    @WorkerThread
    suspend fun insertFavDishDat(favDish : FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishesList()

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
        favDishDao.updateFavDishDetails(favDish)
    }

    val favoriteDishes: Flow<List<FavDish>> = favDishDao.getFavoriteDishesList()

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish){
        favDishDao.deleteFavDishDetails(favDish)
    }

    fun filteredListDishes(value : String) : Flow<List<FavDish>> =
        favDishDao.getFilteredDishesList(value)
}