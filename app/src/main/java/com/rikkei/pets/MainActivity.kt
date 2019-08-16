package com.rikkei.pets

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.internal.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class   MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("Connect","onConnnectionFailed"+connectionResult)
    }
    private val RC_SIGN_IN = 1
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)


        btn_signin.setOnClickListener{
            emailValidate()
        }
        btn_gg_signin.setOnClickListener{
            val signInIntent = mGoogleSignInClient.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)

                }
            } catch (e: ApiException) {
            }

        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val intent = Intent(this, PetsActivity::class.java)
                    startActivity(intent)
                    updateUI(user)
                } else {
                    Toast.makeText(this,"Auth Failed",Toast.LENGTH_LONG).show()
                    updateUI(null)
                }

            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    fun updateUI(user: FirebaseUser?){
        if(user != null){

        }
    }
    private fun emailValidate() {
        val email : String = edt_email.getText().toString().trim()
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
