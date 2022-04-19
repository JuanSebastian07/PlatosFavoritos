package com.projects.favdish.view.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projects.favdish.R
import com.projects.favdish.databinding.ItemDishLayoutBinding
import com.projects.favdish.model.entities.FavDish
import com.projects.favdish.utils.Constants
import com.projects.favdish.view.activities.AddUpdateDishActivity
import com.projects.favdish.view.fragments.AllDishesFragment
import com.projects.favdish.view.fragments.FavoriteDishesFragment

class FavDishAdapter(private val fragment : Fragment) : RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes : List<FavDish> = listOf()

    class ViewHolder(view: ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        //Holds the TextView that will add each item to
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        holder.tvTitle.text = dish.title
        holder.itemView.setOnClickListener {
            //si venimos de AlldishesFragment
            if(fragment is AllDishesFragment){
                fragment.dishDetails(dish)
            }
            if(fragment is FavoriteDishesFragment){
            fragment.dishDetails(dish)
            }
        }
        //listener
        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter,popup.menu)

            popup.setOnMenuItemClickListener {
                //editar
                if(it.itemId == R.id.action_edit_dish){
                    val intent = Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)
                    //Log.i("You have clicked on","Edit option of ${dish.title}")
                //Eliminar
                }else if(it.itemId == R.id.action_delete_dish){
                    if (fragment is AllDishesFragment){
                        fragment.deleteDish(dish)
                    }
                    //Log.i("You have clicked on","Delete option of ${dish.title}")
                }
                true
            }

            popup.show()//Mostramos el popup
        }

        if(fragment is AllDishesFragment){
            holder.ibMore.visibility = View.VISIBLE
        }else if(fragment is FavoriteDishesFragment){
            holder.ibMore.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>){
        dishes = list
        //notificamos que los datos han cambiado
        notifyDataSetChanged()
    }


}