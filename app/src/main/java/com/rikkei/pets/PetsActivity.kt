package com.rikkei.pets


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.rikkei.pets.adapter.GalleryAdapter
import com.rikkei.pets.adapter.PetsAdapter
import com.rikkei.pets.api.ImageDogApi
import com.squareup.picasso.Picasso
import com.rikkei.pets.api.DogApi
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_pets.*
import kotlinx.android.synthetic.main.gv_item.*
import kotlinx.android.synthetic.main.pets.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import android.widget.Adapter as Adapter


class PetsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var firebaseAuth: FirebaseAuth? = null
    lateinit var ivProfilePicture: CircleImageView
    lateinit var tvName: TextView
    lateinit var tvEmail: TextView
    val PICK_IMAGE_MULTIPLE = 1
    val PERMISSION = 2
    lateinit var imageEncoded: String
    lateinit var imagesEncodedList: MutableList<String>
    lateinit var galleryAdapter: GalleryAdapter

    var retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    var api = retrofit.create(DogApi::class.java)
    var apiImage = retrofit.create(ImageDogApi::class.java)
    var dogList : ArrayList<String> = ArrayList()
    lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)

        firebaseAuth = FirebaseAuth.getInstance()
        initViews()
        DogBreedList()
        DrawerLayout()
        EmailInfo()
        RandomImageDog()
        setSupportActionBar(toolbar)
        navigation.setNavigationItemSelectedListener(this)
        checkPermission()
    }

    private fun EmailInfo() {
        ivProfilePicture = findViewById<View>(R.id.im_avatar) as CircleImageView
        tvName = findViewById<View>(R.id.tv_name) as TextView
        tvEmail = findViewById<View>(R.id.tv_emailpets) as TextView
        val user = firebaseAuth?.currentUser
        tvName.text = user?.displayName
        tvEmail.text = user?.email
        Picasso.get()
            .load(user?.photoUrl)
            .into(ivProfilePicture)
    }

    private fun DrawerLayout() {
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        toggle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {}
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)

    }
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    private fun RandomImageDog() {
        Log.i("Random Image","Display")
        val callImage = apiImage.ImageDog("$dogList")
        callImage.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    if (response.body()!=null){
                        val bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        im_dog.setImageBitmap(bmp)
                    }else{

                    }
                }else{

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })

    }

    private fun initViews() {
        pager.adapter = PetsAdapter(supportFragmentManager)
        tabs.setupWithViewPager(pager)
    }
    private fun DogBreedList() {
        val call = api.DogBreed()
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        Log.i("Success",response.body().toString())
                        val json =response.body().toString()
                        convertToDogBreed(json)
                    }else{
                        Log.i("No Response","Empty Response")
                    }
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
            }

        })
    }

    private fun convertToDogBreed(json: String) {
        try {
            val obj = JSONObject(json)
            if (obj.optString("status").equals("success")){
                val dataDogBreed= obj.getJSONObject("message")
                val keys : Iterator<String> = dataDogBreed.keys()
                while (keys.hasNext()){
                    val key = keys.next()
                    dogList.add(key)
                }
                val adapter = PetsAdapter(supportFragmentManager)
                for (dog in dogList.indexOf(dogList[0])  until dogList.indexOf(dogList[40])){
                    menuItem(dogList[dog],dog)
                    val breed = Pets.newInstance(dogList[dog])
                    adapter.addFragment(breed,dogList[dog])

                }
                pager.adapter = adapter
            }else{
                Log.d("Tag","Failed")
            }
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.drawer_view,menu)
        return true
    }


    fun menuItem(dog: String,id:Int) {
        navigation.menu.add(1,id,id,dog.toUpperCase())
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var t = item.itemId
        when(t){
            t->{
                pager.currentItem = t
                drawer.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }
    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION)
            }
            else{
                addImage()
            }
        }
        else{
            addImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    addImage()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun addImage(){
        fbtn_add.setOnClickListener{
            var intent = Intent()
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_MULTIPLE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode ==RESULT_OK
                && null != data) {

                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                imagesEncodedList = java.util.ArrayList()
                if (data.data != null) {

                    val mImageUri = data.data

                    val cursor = contentResolver.query(mImageUri!!,
                        filePathColumn, null, null, null)
                    cursor!!.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    imageEncoded = cursor.getString(columnIndex)
                    cursor.close()

                    val mArrayUri = java.util.ArrayList<Uri>()
                    mArrayUri.add(mImageUri)
                    galleryAdapter = GalleryAdapter(applicationContext, mArrayUri)
                    gv!!.adapter = galleryAdapter
                    gv!!.verticalSpacing = gv!!.horizontalSpacing
                    val mlp = gv!!
                        .layoutParams as ViewGroup.MarginLayoutParams
                    mlp.setMargins(0, gv!!.horizontalSpacing, 0, 0)

                } else {
                    if (data.clipData != null) {
                        val mClipData = data.clipData
                        val mArrayUri = java.util.ArrayList<Uri>()
                        for (i in 0 until mClipData!!.itemCount) {

                            val item = mClipData!!.getItemAt(i)
                            val uri = item.uri
                            mArrayUri.add(uri)
                            val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                            cursor!!.moveToFirst()

                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            imageEncoded = cursor.getString(columnIndex)
                            imagesEncodedList.add(imageEncoded)
                            cursor.close()

                            galleryAdapter = GalleryAdapter(applicationContext, mArrayUri)
                            gv!!.adapter = galleryAdapter
                            gv!!.verticalSpacing = gv!!.horizontalSpacing
                            val mlp = gv!!
                                .layoutParams as ViewGroup.MarginLayoutParams
                            mlp.setMargins(0, gv!!.horizontalSpacing, 0, 0)

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size)
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                    Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG)
                .show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}

