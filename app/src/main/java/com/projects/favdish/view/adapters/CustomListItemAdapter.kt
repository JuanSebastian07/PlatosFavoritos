package com.projects.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.projects.favdish.databinding.ItemCustomListBinding
import com.projects.favdish.view.activities.AddUpdateDishActivity
import com.projects.favdish.view.fragments.AllDishesFragment

class CustomListItemAdapter(private val activity : Activity, private val fragment : Fragment?, private val listIems : List<String>, private val selection : String): RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>(){

    class ViewHolder(binding: ItemCustomListBinding): RecyclerView.ViewHolder(binding.root){
        val tvText = binding.tvText

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemCustomListBinding = ItemCustomListBinding.inflate(LayoutInflater.from(activity),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listIems[position]
        holder.tvText.text = item

        holder.itemView.setOnClickListener {
            if(activity is AddUpdateDishActivity){
                activity.selectedListItem(item,selection)
            }
            if(fragment is AllDishesFragment){
                fragment.filterSelection(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return listIems.size
    }
}