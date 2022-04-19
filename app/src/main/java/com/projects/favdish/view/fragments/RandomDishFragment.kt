package com.projects.favdish.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.projects.favdish.R
import com.projects.favdish.application.FavDishApplication
import com.projects.favdish.databinding.FragmentRandomDishBinding
import com.projects.favdish.model.entities.FavDish
import com.projects.favdish.model.entities.RandomDish
import com.projects.favdish.utils.Constants
import com.projects.favdish.viewmodel.FavDishViewModel
import com.projects.favdish.viewmodel.FavDishViewModelFactory
import com.projects.favdish.viewmodel.NotificationsViewModel
import com.projects.favdish.viewmodel.RandomDishViewModel
import kotlinx.coroutines.flow.collect

class RandomDishFragment : Fragment() {

    private var mBinding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel : RandomDishViewModel

    // A global variable for Progress Dialog
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize the ViewModel variable.
        mRandomDishViewModel = ViewModelProvider(this)[RandomDishViewModel::class.java]

        //mRandomDishViewModel.getRandomDishFromAPI()


        randomDishViewModelObserver()

        /**
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mBinding!!.srlRandomDish.setOnRefreshListener {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            randomDishViewModelObserver()
        }
    }//onViewCreated

    private fun randomDishViewModelObserver(){

        /*mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner){
            randomDishResponse-> randomDishResponse?.let {
                Log.i("Ran", "${randomDishResponse.recipes[0]}")
            }

        }*/

        lifecycleScope.launchWhenStarted {
            mRandomDishViewModel.randomDishResponse().collect { randomDishResponse->
                randomDishResponse.let {
                    //Log.i("Ran", "${randomDishResponse!!.recipes[0]}")
                    setRandomDishResponseInUI(randomDishResponse!!.recipes[0])
                    mRandomDishViewModel.endLoading()
                }
            }
        }

        /*mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner){dataError->
            dataError?.let {
                Log.i("Random Dish API Error", "$dataError")

                if (mBinding!!.srlRandomDish.isRefreshing) {
                    mBinding!!.srlRandomDish.isRefreshing = false
                }
            }
        }*/

        lifecycleScope.launchWhenStarted {
            mRandomDishViewModel.randomDishLoadingError.collect { dataError->
                dataError.let {
                    Log.i("Random Dish API Error", "$dataError")

                    if (mBinding!!.srlRandomDish.isRefreshing) {
                        mBinding!!.srlRandomDish.isRefreshing = false
                    }
                }

            }
        }

        /*mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner){ loadRandomDish ->
            loadRandomDish?.let {
                Log.i("Random Dish Loading", "$loadRandomDish")
            }
        }*/

        lifecycleScope.launchWhenStarted {
            mRandomDishViewModel.loadRandomDish.collect { loadRandomDish ->
                loadRandomDish.let {
                    Log.i("Random Dish Loading", "$loadRandomDish")

                    // Show the progress dialog if the SwipeRefreshLayout is not visible and hide when the usage is completed.
                    if (loadRandomDish && !mBinding!!.srlRandomDish.isRefreshing) {
                        showCustomProgressDialog() // Used to show the progress dialog
                    } else {
                        Log.i("vaLo::",loadRandomDish.toString())
                        Log.i("vaMBin..::",mBinding!!.srlRandomDish.isRefreshing.toString())
                        hideProgressDialog()
                    }
                }
            }
        }

    }

    private fun setRandomDishResponseInUI(recipe : RandomDish.Recipe){
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title

        var dishType : String = "other"

        if(recipe.dishTypes.isNotEmpty()){
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }

        // There is not category params present in the response so we will define it as Other.
        mBinding!!.tvCategory.text = "Other"
        var ingredients = ""
        for (value in recipe.extendedIngredients) {

            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }

        mBinding!!.tvIngredients.text = ingredients
        mBinding!!.tvCookingDirection.text = recipe.instructions

        mBinding!!.tvCookingTime.text = resources.getString(R.string.lbl_estimate_cooking_time, recipe.readyInMinutes.toString())

        var addedToFavorite = false

        mBinding!!.ivFavoriteDish.setOnClickListener {
            val randomDishDetails = FavDish(
                recipe.image,
                Constants.DISH_IMAGE_SOURCE_ONLINE,
                recipe.title,
                dishType,
                "Other",
                ingredients,
                recipe.readyInMinutes.toString(),
                recipe.instructions,
                true
            )
            //conectamos el viewmodel con nuestra activity
            val mFavDishViewModel : FavDishViewModel by viewModels{
                FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
            }

            mFavDishViewModel.insert(randomDishDetails)

            //addedToFavorite = true

            mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_selected))

            Toast.makeText(requireActivity(), resources.getString(R.string.msg_added_to_favorites), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    /**
     * A function is used to show the Custom Progress Dialog.
     */
    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(requireActivity())

        mProgressDialog?.let {
            /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
            it.setContentView(R.layout.dialog_custom_progress)

            //Start the dialog and display it on screen.
            it.show()
        }

        //mBinding!!.srlRandomDish.isRefreshing = false
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    private fun hideProgressDialog() {
        mProgressDialog?.let {
            it.dismiss()
        }
    }
}