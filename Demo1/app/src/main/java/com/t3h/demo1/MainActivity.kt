package com.t3h.demo1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_signin.setOnClickListener(View.OnClickListener {
            emailValidate()
        });

    }

    fun emailValidate() {
        var email : String = edt_email.getText().toString().trim()
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            passValidate()
        }else{
            Toast.makeText(this,"Invalid Email",Toast.LENGTH_SHORT).show()

        }
    }

    private fun passValidate() {
        val password :String = edt_password.getText().toString().trim()
        if (password.length<=8){
            Toast.makeText(this,"Invalid Password",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Valid Password",Toast.LENGTH_SHORT).show()

        }
    }
}
