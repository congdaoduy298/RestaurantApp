package com.amantech.foodrunner.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.amantech.foodrunner.R
import com.amantech.foodrunner.activity.ForgotPassword
import com.amantech.foodrunner.activity.LoginActivity
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordDetailsFragment : Fragment() {

    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password_details, container, false)

        etMobileNumber = view.findViewById(R.id.etMobileNumber)
        etEmail = view.findViewById(R.id.etEmail)
        btnNext = view.findViewById(R.id.btnNext)




        btnNext.setOnClickListener {

            if (validateFields()) {
                //internet validation part
                validateAndNext()

            } else {
                Toast.makeText(activity as Context, "Incorrect credentials", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        return view
    }

    fun validateAndNext() {
        Firebase.auth.sendPasswordResetEmail(etEmail.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity as Context, "Email sent. Reset your password and log in.", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(
                        Intent(
                            activity as Context,
                            LoginActivity::class.java
                        )
                    )
                }
            }
    }

    fun validateFields() =
        etMobileNumber.text.toString().length == 10 && etEmail.text.toString().isNotEmpty()
}