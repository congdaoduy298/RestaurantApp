package com.amantech.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.HomeRecyclerAdapter
import com.amantech.foodrunner.model.Restaurant
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUp: TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

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

    fun validateAndLogin() {

        val queue = Volley.newRequestQueue(this@LoginActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", etMobileNumber.text.toString())
        jsonParams.put("password", etPassword.text.toString())

        if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    val mainData = it.getJSONObject("data")

                    try {
                        val success = mainData.getBoolean("success")

                        if (success) {

                            println("Login Successful!!")
                            val data = mainData.getJSONObject("data")

                            val user_id = data.getString("user_id")
                            val name = data.getString("name")
                            val email = data.getString("email")
                            val mob_no = data.getString("mobile_number")
                            val address = data.getString("address")

                            //saving preferences..

                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                            sharedPreferences.edit().putString("user_id", user_id).apply()
                            sharedPreferences.edit().putString("Name", name).apply()
                            sharedPreferences.edit().putString("MobNo", mob_no).apply()
                            sharedPreferences.edit().putString("Email", email).apply()
                            sharedPreferences.edit().putString("Address", address).apply()

                            //starting homescreen
                            startHomeScreen()

                        }

                        else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Incorrect Phone or Password",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                    catch (e: JSONException) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Some unexpected error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(
                        this@LoginActivity,
                        "Volley error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "SECRET_TOKEN_HERE"
                    return headers
                }
            }

            queue.add(jsonRequest)
        }
        //for internet connectivity part
        else {
            val dialog = AlertDialog.Builder(this@LoginActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@LoginActivity)
            }
            dialog.create()
            dialog.show()
        }


    }

    fun validateLoginFields() =
        (etMobileNumber.text.toString().length == 10 && etPassword.text.toString().length >= 6)

    private fun initializeView() {
        etMobileNumber = findViewById(R.id.etMobileNumber)
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

            if (validateLoginFields()) {
                /*
                if valid information entered in login fields
                validate from server and login
                 */
                validateAndLogin()
            }
            else {
                Toast.makeText(
                    this@LoginActivity,
                    "Incorrect credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        txtSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterScreen::class.java))
        }
    }
}
