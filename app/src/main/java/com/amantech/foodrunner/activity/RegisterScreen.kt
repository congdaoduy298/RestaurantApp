package com.amantech.foodrunner.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.amantech.foodrunner.R
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import org.json.JSONObject
import com.amantech.foodrunner.util.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterScreen : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )
        setContentView(R.layout.activity_register_screen)
        initializeView()

    }

    fun startHomeScreen() {
        val intent = Intent(this@RegisterScreen, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateRegisterFields() = (etName.text.toString().length >= 3
            && etEmail.text.toString().isNotEmpty()
            && etMobileNumber.text.toString().length == 10
            && etAddress.text.toString().isNotEmpty() &&
            etPassword.text.toString().length >= 6
            && etConfirmPassword.text.toString().length >= 6
            && (etPassword.text.toString() == etConfirmPassword.text.toString()))

    private fun saveUserInformation(){
        val user = Firebase.auth.currentUser
        user?.let {
            val uid = user.uid

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val mob_no = etMobileNumber.text.toString()
            val address = etAddress.text.toString()
            val password = etPassword.text.toString()

            val  profile = Profile (name, mob_no, password, address, email)

            var dataReference = FirebaseDatabase.getInstance().getReference("profiles")
            dataReference.child(uid).setValue(profile)

            println("Registration Successful!!")

            //saving preferences..

            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
            sharedPreferences.edit().putString("Name", name).apply()
            sharedPreferences.edit().putString("MobNo", mob_no).apply()
            sharedPreferences.edit().putString("Email", email).apply()
            sharedPreferences.edit().putString("Address", address).apply()

            //starting homescreen
            startHomeScreen()
        }
    }

    private fun initializeView() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Register Yourself"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        var firebaseDatabase = FirebaseDatabase.getInstance()
        var dataReference = firebaseDatabase.getReference("restaurant")

        dataReference.child("0").get().addOnSuccessListener {
            if (it.exists()){
                var email = it.child("address").value
                Log.d("Email", email.toString())
            }
        }



        btnRegister.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.show()

            if (validateRegisterFields()) {
                //function called for validating registration
                auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            progressDialog.dismiss()
                            // Sign in success, update UI with the signed-in user's information
                            saveUserInformation()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Create email and password failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }


            } else {
                Toast.makeText(
                    this@RegisterScreen,
                    "Incomplete credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }


}
