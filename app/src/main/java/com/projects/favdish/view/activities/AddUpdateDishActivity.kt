package com.projects.favdish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.projects.favdish.R
import com.projects.favdish.application.FavDishApplication
import com.projects.favdish.databinding.ActivityAddUpdateDishBinding
import com.projects.favdish.databinding.DialogCustomImageSelectionBinding
import com.projects.favdish.databinding.DialogCustomListBinding
import com.projects.favdish.model.entities.FavDish
import com.projects.favdish.utils.Constants
import com.projects.favdish.view.adapters.CustomListItemAdapter
import com.projects.favdish.viewmodel.FavDishViewModel
import com.projects.favdish.viewmodel.FavDishViewModelFactory
import java.io.*
import java.util.*


class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath : String = ""
    private lateinit var mCustomListDialog: Dialog
    private var mFavDishDetails : FavDish? = null
    //RDB
    private val mFavDishViewModel : FavDishViewModel by viewModels{
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    private val openCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //Aqui obtendremos los resultados
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            //Toast.makeText(this,"Tdo bien", Toast.LENGTH_LONG).show()
            val data = result.data
            //foto tomada con nuestro dispositivo
            val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
            // TODO fijamos la imagen en el ivDishImage
            //mBinding?.ivDishImage?.setImageBitmap(thumbnail)

            //Todo Con Glide
            //https://guides.codepath.com/android/Displaying-Images-with-the-Glide-Library
            Glide.with(this)
                .load(thumbnail).fitCenter().override(2500,1000).into(mBinding.ivDishImage)

            //path de la img
            mImagePath = saveImageToInternalStorage(thumbnail)
            Log.i("ImagePath ->", mImagePath)

            mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))

            //Guardamos la imagen en el dispositivo
            //saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
            //Log.e("save image","path:: $saveImageToInternalStorage")
        }
    }

    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //Aqui obtendremos los resultados
            result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null){
            val selectedPhotUri = result.data?.data
            //Todo Aqui fijamos la imagen que seleccionamos en la galeria
            //mBinding?.ivDishImage?.setImageURI(selectedPhotUri)

            //Todo Con glide
            //https://guides.codepath.com/android/Displaying-Images-with-the-Glide-Library
            Glide.with(this)
                .load(selectedPhotUri).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.e("TAG", "Error loading image", e)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resource?.let {
                            val bitmap : Bitmap = resource.toBitmap()
                            mImagePath = saveImageToInternalStorage(bitmap)
                            Log.i("ImagePath ->", mImagePath)
                        }
                        return false
                    }

                })
                .into(mBinding.ivDishImage)

            mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))


            //*********
            //Bueno, Glide es una biblioteca de carga de imágenes rápida y eficiente para Android, enfocada en el desplazamiento suave
            //*********

            //Guardamos la imagen en el dispositivo
            //val image = findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivImage)
            //path Donde va quedar guardada la img
            //saveImageToInternalStorage = saveImageToInternalStorage(getBitmapFromView(image))
            //Log.e("save image","path:: $saveImageToInternalStorage")

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //setupActionBar()

        //recibimos los datos y los almacenamos el variable mFavDishDetails
        if(intent.hasExtra(Constants.EXTRA_DISH_DETAILS)){
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        setupActionBar()

        mFavDishDetails?.let {
            if (it.id != 0){
                Log.i("it.id", it.id.toString())
                mImagePath = it.image
                //Glide
                Glide.with(this@AddUpdateDishActivity)
                    .load(mImagePath)
                    .centerCrop()
                    .into(mBinding.ivDishImage)

                mBinding.etTitle.setText(it.title)
                mBinding.etType.setText(it.type)
                mBinding.etCategory.setText(it.category)
                mBinding.etIngredients.setText(it.ingredients)
                mBinding.etCookingTime.setText(it.cookingTime)
                mBinding.etDirectionToCook.setText(it.directionToCook)

                mBinding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
            }
        }

        mBinding.ivAddDishImage.setOnClickListener(this)

        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)

        mBinding.btnAddDish.setOnClickListener(this)

    }//onCreate

    private fun setupActionBar(){
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        if(mFavDishDetails != null && mFavDishDetails!!.id != 0){
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        }else {
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_add_dish)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                    return
                }
                R.id.et_type -> {
                    customItemsListDialog(resources.getString(R.string.title_select_dish_type),Constants.dishTypes(),Constants.DISH_TYPE)
                    return
                }R.id.et_category ->{
                    customItemsListDialog(resources.getString(R.string.title_select_dish_category),Constants.dishCategories(),Constants.DISH_CATEGORY)
                    return
                }R.id.et_cooking_time ->{
                    customItemsListDialog(resources.getString(R.string.title_select_dish_cooking_time),Constants.dishCookTime(),Constants.DISH_COOKING_TIME)
                    return
                }R.id.btn_add_dish ->{
                    val title = mBinding.etTitle.text.toString().trim { it <= ' ' }
                    val type = mBinding.etType.text.toString().trim { it <= ' ' }
                    val category = mBinding.etCategory.text.toString().trim { it <= ' ' }
                    val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' ' }
                    val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim { it <= ' ' }
                    val cookingDirection = mBinding.etDirectionToCook.text.toString().trim { it <= ' ' }

                    when{
                        TextUtils.isEmpty(mImagePath) -> {
                            Toast.makeText(this, resources.getString(R.string.err_msg_select_dish_image),Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(title) -> {
                            Toast.makeText(this, resources.getString(R.string.err_msg_enter_dish_title),Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(this, resources.getString(R.string.err_msg_select_dish_type),Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(this, resources.getString(R.string.err_msg_select_dish_category),Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(this, resources.getString(R.string.err_msg_enter_dish_ingredients),Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingTimeInMinutes) -> {
                            Toast.makeText(this, resources.getString(R.string.err_msg_select_dish_cooking_time),Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(this, resources.getString(R.string.err_msg_enter_dish_cooking_instructions),Toast.LENGTH_SHORT).show()
                        }
                        else ->{
                            var dishID = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favoriteDish = false

                            mFavDishDetails?.let {
                                if(it.id != 0){
                                    dishID = it.id
                                    imageSource = it.imageSource
                                    favoriteDish = it.favoriteDish
                                }
                            }

                            val favDishDetails = FavDish(mImagePath, /*Constants.DISH_IMAGE_SOURCE_LOCAL*/imageSource, title, type, category, ingredients, cookingTimeInMinutes, cookingDirection, /*false*/favoriteDish)
                            if(dishID == 0){
                                mFavDishViewModel.insert(favDishDetails)
                                //insertamos el obj favDishDetails en la base de datos
                                Toast.makeText(this, "You succesfully added your favorite dish details." , Toast.LENGTH_SHORT).show()
                            }else{
                                //Actualizamos la RDB
                                mFavDishViewModel.update(favDishDetails)
                                Toast.makeText(this, "You succesfully Update your favorite dish details." , Toast.LENGTH_SHORT).show()
                            }
                            //cerramos la activity actual
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun customImageSelectionDialog(){
        val dialog = Dialog(this)
        val binding : DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        //listener camera
        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        openCameraLauncher.launch(intent)
                    }//else
                }
                override fun onPermissionRationaleShouldBeShown(permissions : MutableList<PermissionRequest>?, token : PermissionToken?) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
            dialog.dismiss()
        }
        //listener galleria
        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(galleryIntent)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@AddUpdateDishActivity,"Has denegado el permiso", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?){
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
            dialog.dismiss()
        }
        dialog.show()
    }//customImageSelectionDialog()

    fun selectedListItem(item : String, selection: String){
        when(selection){
            Constants.DISH_TYPE ->{
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY ->{
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            else ->{
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("Parece que ha desactivado los permisos necesarios para esta función. Se puede habilitar en Configuración de la aplicación")
            .setPositiveButton("Ir a Configuraciones"){_,_->
                try {
                    //Vamos a la configuracion
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    //Entonces obtenemos el enlace de la aplicación, por así decirlo, en su configuración según el nombre del paquete
                    val uri = Uri.fromParts("package", packageName, null)
                    Log.i("Uri->", uri.toString())
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("cancel"){dialog,_->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap : Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("FavDishImages", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e : IOException){
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemsListDialog(title: String, itemsList: List<String>, selection : String){
        mCustomListDialog = Dialog(this)
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(this)
        //adapter
        val adapter = CustomListItemAdapter(this, null, itemsList, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

}