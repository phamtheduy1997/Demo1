package com.rikkei.pets


import android.R.*
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
//import android.support.v4.app.Fragment
//import android.support.v4.view.ViewPager
//import android.support.v4.app.FragmentManager
//import android.support.v4.app.FragmentPagerAdapter
//import android.support.v4.view.GravityCompat
//import android.support.v4.widget.DrawerLayout
//import android.support.v7.app.ActionBarDrawerToggle
//import android.support.v7.app.AppCompatActivity
//import android.support.v7.widget.DialogTitle
//import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Constants
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.rikkei.pets.api.ImageDogApi
import com.squareup.picasso.Picasso
import com.rikkei.pets.api.DogApi
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_pets.*
import kotlinx.android.synthetic.main.header_navigation.*
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
//        RandomImageDog()
        setSupportActionBar(toolbar)
        //navigation.setNavigationItemSelectedListener(this)
    }

    private fun EmailInfo() {
        ivProfilePicture = findViewById<View>(R.id.im_avatar) as CircleImageView
        tvName = findViewById<View>(R.id.tv_name) as TextView
        tvEmail = findViewById<View>(R.id.tv_emailpets) as TextView
        val user = firebaseAuth?.currentUser
        tvName.text = user?.displayName
        tvEmail.text = user?.email
        Picasso.with(this)
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
        if (item!!.itemId == id.home) {}
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
        var callImage = apiImage.ImageDog("$dogList")
        callImage.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    if (response.body()!=null){
                        var bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        dog_image.setImageBitmap(bmp)
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
        var call = api.DogBreed()
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.i("Response",response.body().toString())
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
                for (dog in dogList){
                    Log.d("Dog",dog+"\n")
                    menuItem(dog)
                    val breed = Pets.newInstance("")
                    adapter.addFragment(breed,"$dog")

                }
                pager.adapter = adapter
            }else{
                Log.d("Tag","Get Json Failed")
            }
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pets,menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        if (item!!.getItemId()== android.R.id.home){
//
//        }
//        if (toggle.onOptionsItemSelected(item)){
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
    fun menuItem(dog: String) {
        navigation.menu.add(dog)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
}