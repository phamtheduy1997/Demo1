package com.t3h.demo1

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DialogTitle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.animation.ScaleAnimation
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.t3h.demo1.api.DogApi
import kotlinx.android.synthetic.main.activity_pets.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import android.widget.Adapter as Adapter

class PetsActivity : AppCompatActivity() {
    var retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    var api = retrofit.create(DogApi::class.java)
    var dogList : ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)
        initViews()
        DogBreedList()
        setSupportActionBar(toolbar)
        tabs.setupWithViewPager(pager)
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
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                    val breed = Pets.newInstance("Dogs")
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


//    private fun setupViewPager(pager: ViewPager?) {
//        val adapter = PetsAdapter(supportFragmentManager)
//        val f1 = Pets.newInstance("Dogs")
//        adapter.addFragment(f1, "Dogs")
//        val f2 = Pets.newInstance("Cats")
//        adapter.addFragment(f2, "Cats")
//        val f3 = Pets.newInstance("Birds")
//        adapter.addFragment(f3, "Birds")
//        pager?.adapter = adapter
//
//    }


}