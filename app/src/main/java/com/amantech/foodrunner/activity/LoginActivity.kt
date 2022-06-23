package com.amantech.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.amantech.foodrunner.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUp: TextView
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startHomeScreen()
        }

        setContentView(R.layout.activity_login)

        initializeView()

    }

    private fun startHomeScreen() {
        val intent = Intent(this@LoginActivity, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun accessCurrentUser(){
        val user = Firebase.auth.currentUser
        user?.let {
            val uid = user.uid
            var dataReference = FirebaseDatabase.getInstance().getReference("profiles").child(uid)
            dataReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val email = snapshot.child("email").getValue(String::class.java)
                    val address = snapshot.child("address").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)
                    val mob_no = snapshot.child("mobileNumber").getValue(String::class.java)

                    //saving preferences..
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    sharedPreferences.edit().putString("Name", name).apply()
                    sharedPreferences.edit().putString("MobNo", mob_no).apply()
                    sharedPreferences.edit().putString("Email", email).apply()
                    sharedPreferences.edit().putString("Address", address).apply()
                    Log.d("Address", address.toString())
                } else {
                    Log.d("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }
        }
    }

    fun validateAndLogin() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    if (user!!.isEmailVerified()){
                        // Sign in success, update UI with the signed-in user's information
                        accessCurrentUser()
                        //starting homescreen
                        startHomeScreen()
                    }
                    else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please verify your email first!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@LoginActivity,
                        "Incorrect Phone or Password",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    fun validateLoginFields() =
        (etEmail.text.toString().length != 0)

    private fun initializeView() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUp = findViewById(R.id.txtSignUp)

        var data: String? = txtForgotPassword.text.toString()
        var content = SpannableString(data)
        content.setSpan(UnderlineSpan(), 0, data?.length ?: 0, 0)
        txtForgotPassword.text = content

        data = txtSignUp.text.toString()
        content = SpannableString(data)
        content.setSpan(UnderlineSpan(), 0, data.length ?: 0, 0)
        txtSignUp.text = content

        txtForgotPassword.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    ForgotPassword::class.java
                )
            )
        }

        btnLogin.setOnClickListener {
            val dialog = setProgressDialog(this, "Loading..")
            dialog.show()
            if (validateLoginFields()) {
                validateAndLogin()
                dialog.dismiss()
            }
            else {
                Toast.makeText(
                    this@LoginActivity,
                    "Please fill the email element.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        txtSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterScreen::class.java))
        }
    }

    fun setProgressDialog(context:Context, message:String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }
}
