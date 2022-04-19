package com.projects.favdish.viewmodel

import androidx.lifecycle.*
import com.projects.favdish.model.database.FavDishRepository
import com.projects.favdish.model.entities.FavDish
import kotlinx.coroutines.launch

/**
 * La función de ViewModel es proporcionar datos a la interfaz de usuario y sobrevivir a los cambios de configuración.
 * Un ViewModel actúa como un centro de comunicación entre el repositorio y la interfaz de usuario.
 * También puede usar un ViewModel para compartir datos entre fragmentos.
 * The ViewModel is part of the lifecycle library.
 *
 * @param repository - The repository class is
 */
class FavDishViewModel(private val repository: FavDishRepository) : ViewModel(){

    /**
     * Launching a new coroutine to insert the data in a non-blocking way.
     */
    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDishDat(dish)
    }

    /** Usar LiveData y almacenar en caché lo que devuelve allDishesList tiene varios beneficios:
     * Podemos poner un observador en los datos (en lugar de buscar cambios) y solo
     * actualice la interfaz de usuario cuando los datos realmente cambien.
     * El repositorio está completamente separado de la interfaz de usuario a través de ViewModel.
     */
    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    /**
     * Launching a new coroutine to update the data in a non-blocking way
     */
    fun update(dish : FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    // Obtener la lista de platos favoritos que podemos completar en la interfaz de usuario.
    /** El uso de LiveData y el almacenamiento en caché de lo que devuelve FavouriteDishes tiene varios beneficios:
     * Podemos poner un observador en los datos (en lugar de buscar cambios) y solo
     * actualice la interfaz de usuario cuando los datos realmente cambien.
     * El repositorio está completamente separado de la interfaz de usuario a través de ViewModel.
     */
    val favoriteDishes : LiveData<List<FavDish>> = repository.favoriteDishes.asLiveData()

    fun delete(dish : FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(dish)
    }

    fun getFilteredList(value : String) : LiveData<List<FavDish>> =
        repository.filteredListDishes(value).asLiveData()
}

/**
 * Para crear el ViewModel implementamos un ViewModelProvider.Factory que toma como parámetro las dependencias
 * necesario para crear FavDishViewModel: FavDishRepository.
 * Al usar viewModels y ViewModelProvider.Factory, el marco se encargará del ciclo de vida del ViewModel.
 * Sobrevivirá a los cambios de configuración e incluso si se vuelve a crear la Actividad,
 * siempre obtendrá la instancia correcta de la clase FavDishViewModel.
 */
class FavDishViewModelFactory(private val repository : FavDishRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknow ViewModel Class")
    }
}