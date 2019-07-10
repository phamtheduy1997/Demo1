package com.t3h.demo1

import android.app.AppComponentFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

class PetsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)
    }
}