package com.projects.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.favdish.R
import com.projects.favdish.application.FavDishApplication
import com.projects.favdish.databinding.DialogCustomListBinding
import com.projects.favdish.databinding.FragmentAllDishesBinding
import com.projects.favdish.model.entities.FavDish
import com.projects.favdish.utils.Constants
import com.projects.favdish.view.activities.AddUpdateDishActivity
import com.projects.favdish.view.activities.MainActivity
import com.projects.favdish.view.adapters.CustomListItemAdapter
import com.projects.favdish.view.adapters.FavDishAdapter
import com.projects.favdish.viewmodel.FavDishViewModel
import com.projects.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private var mBinding: FragmentAllDishesBinding? = null
    private lateinit var mFavDishAdapter : FavDishAdapter
    private lateinit var mCustomListDialog : Dialog

    //RDB
    private val mFavDishViewModel : FavDishViewModel by viewModels {
        //requireActivity: nos permite obtener el contexto del Fragment
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

    }//onCreate

    //+
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_dish -> {
                //requireActivity: nos permite obtener el contexto del Fragment
                startActivity(Intent(requireActivity(),AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.rvDishesList?.layoutManager = GridLayoutManager(requireActivity(),2)

        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)

        mBinding?.rvDishesList?.adapter = mFavDishAdapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){
            dishes ->
                dishes.let {
                    if (it.isNotEmpty()){
                        mBinding?.rvDishesList?.visibility = View.VISIBLE
                        mBinding?.tvNoDishesAddedYet?.visibility = View.GONE
                        //Llamos al metodo con el objeto.dishesesList y
                        //Le pasamos al metodo dishesList la lista FavDish y que el adapter muestre los datos
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding?.rvDishesList?.visibility = View.GONE
                        mBinding?.tvNoDishesAddedYet?.visibility = View.VISIBLE
                    }
                    /*for(item in it){
                        Log.i("Dish Title","${item.id} :: ${item.title}")
                    }*/
                }
        }
    }
    /**
     * A function to navigate to the Dish Details Fragment.
     *
     * @param favDish
     */
    fun dishDetails(favDish : FavDish){
        //Aqui vamos al otro fragment y le pasamos los datos
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDishDetailsFragment(favDish))

        if(requireActivity() is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }

    fun deleteDish(dish : FavDish){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){ dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.lbl_no)){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog(){
        mCustomListDialog = Dialog(requireActivity())
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())

        val adapter = CustomListItemAdapter(requireActivity(), this, dishTypes, Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    override fun onResume() {
        super.onResume()

        if(requireActivity() is MainActivity){
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentAllDishesBinding.inflate(inflater,container,false)
        return mBinding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    fun filterSelection(filterItemSelection : String){
        mCustomListDialog.dismiss()

        Log.i("Filter Selection", filterItemSelection)

        if(filterItemSelection == Constants.ALL_ITEMS){
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){
                    dishes ->
                dishes.let {
                    if (it.isNotEmpty()){
                        mBinding?.rvDishesList?.visibility = View.VISIBLE
                        mBinding?.tvNoDishesAddedYet?.visibility = View.GONE
                        //Llamos al metodo con el objeto.dishesesList y
                        //Le pasamos al metodo dishesList la lista FavDish y que el adapter muestre los datos
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding?.rvDishesList?.visibility = View.GONE
                        mBinding?.tvNoDishesAddedYet?.visibility = View.VISIBLE
                    }
                    /*for(item in it){
                        Log.i("Dish Title","${item.id} :: ${item.title}")
                    }*/
                }
            }
        }else{
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){ dishes ->
                dishes.let {
                    if (it.isNotEmpty()){
                        mBinding?.rvDishesList?.visibility = View.VISIBLE
                        mBinding?.tvNoDishesAddedYet?.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding?.rvDishesList?.visibility = View.GONE
                        mBinding?.tvNoDishesAddedYet?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}