package com.t3h.demo1

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern
import kotlin.math.log

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Log.d("Connect","onConnnectionFailed"+connectionResult)
    }
    private val RC_SIGN_IN = 1
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        btn_signin.setOnClickListener(View.OnClickListener {
            emailValidate()
        })
        btn_gg_signin.setOnClickListener(View.OnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val intent = Intent(this, PetsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun emailValidate() {
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
            val intent = Intent(this, PetsActivity::class.java)
            startActivity(intent)
        }
    }
}
