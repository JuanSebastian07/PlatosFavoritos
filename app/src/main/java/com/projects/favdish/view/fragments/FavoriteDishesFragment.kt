package com.projects.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.projects.favdish.application.FavDishApplication
import com.projects.favdish.databinding.FragmentFavoriteDishesBinding
import com.projects.favdish.model.entities.FavDish
import com.projects.favdish.view.activities.MainActivity
import com.projects.favdish.view.adapters.FavDishAdapter
import com.projects.favdish.viewmodel.DashboardViewModel
import com.projects.favdish.viewmodel.FavDishViewModel
import com.projects.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var mBinding: FragmentFavoriteDishesBinding? = null
    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner){ dishes ->
            dishes.let {
                mBinding!!.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireActivity(),2)
                //Usamos el mismo adapter
                val adapter = FavDishAdapter(this@FavoriteDishesFragment)
                mBinding!!.rvFavoriteDishesList.adapter = adapter
                if(it.isNotEmpty()){
                    mBinding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                    mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.GONE
                    //Le pasamos al metodo dishesList la lista FavDish y que el adapter muestre los datos
                    adapter.dishesList(it)
                    /*for (dish in it){
                        Log.i("Favorite Dish","${dish.id} :: ${dish.title}")
                    }*/
                }else{
                    //vacia
                    mBinding!!.rvFavoriteDishesList.visibility = View.GONE
                    mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(favDish : FavDish){
        //nos movemos de fragment
        findNavController().navigate(FavoriteDishesFragmentDirections.actionNavigationFavoriteDishesToNavigationDishDetailsFragment(favDish))

        //
        if(requireActivity() is MainActivity){
                //Escondemos la barra de abajo
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    override fun onResume() {
        super.onResume()

        if(requireActivity() is MainActivity){
            //Escondemos la barra de abajo
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }
}