package com.projects.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.projects.favdish.R
import com.projects.favdish.application.FavDishApplication
import com.projects.favdish.databinding.FragmentDishDetailsBinding
import com.projects.favdish.model.entities.FavDish
import com.projects.favdish.utils.Constants
import com.projects.favdish.viewmodel.FavDishViewModel
import com.projects.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {

    private var mFavDishDetails : FavDish? = null
    private var mBinding : FragmentDishDetailsBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels{
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Agregamos el menu (shared)
        setHasOptionsMenu(true)
    }
    //**para que el menu pueda funcionar debemos agregar estas dos funciones
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflamos el menu
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_share_dish ->{
                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                mFavDishDetails?.let {
                    var image = ""
                    if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                        image = it.image
                    }

                    var cookingInstructions = it.directionToCook

                    extraText ="$image \n" + "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" + "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" + "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //**

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return mBinding!!.root //inflater.inflate(R.layout.fragment_dish_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Rescatamos los datos que vienen del otro fragment
        val args : DishDetailsFragmentArgs by navArgs()
        Log.i("Dish Title", args.dishDetails.title)
        Log.i("Dish Category", args.dishDetails.category)
        Log.i("Dish Ingredient", args.dishDetails.ingredients)

        mFavDishDetails = args.dishDetails

        args.let {
            try{
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .into(mBinding!!.ivDishImage)
            }catch (e : IOException){
                e.printStackTrace()
            }
            mBinding!!.tvTitle.text = it.dishDetails.title
            mBinding!!.tvType.text = it.dishDetails.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } // Used to make first letter capital
            mBinding!!.tvCategory.text = it.dishDetails.category
            mBinding!!.tvIngredients.text = it.dishDetails.ingredients
            mBinding!!.tvCookingDirection.text = it.dishDetails.directionToCook
            mBinding!!.tvCookingTime.text = it.dishDetails.cookingTime



            if(args.dishDetails.favoriteDish){
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(),R.drawable.ic_favorite_selected
                ))
            }else{
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(), R.drawable.ic_favorite_unselected
                ))
            }
        }

        mBinding!!.ivFavoriteDish.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
            Log.i("args.dishDetails", args.dishDetails.toString())
            mFavDishViewModel.update(args.dishDetails)

            if(args.dishDetails.favoriteDish){
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(),R.drawable.ic_favorite_selected
                ))
                Toast.makeText(requireActivity(),resources.getString(R.string.msg_added_to_favorites),Toast.LENGTH_SHORT).show()
            }else{
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(), R.drawable.ic_favorite_unselected
                ))
                Toast.makeText(requireActivity(),resources.getString(R.string.msg_remove_from_favorites),Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}