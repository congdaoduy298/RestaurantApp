package com.amantech.foodrunner.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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

        etMobileNumber = view.findViewById(R.id.etEmail)
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

        val queue = Volley.newRequestQueue(activity)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", etMobileNumber.text.toString())
        jsonParams.put("email", etEmail.text.toString())

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    val mainData = it.getJSONObject("data")

                    try {
                        val success = mainData.getBoolean("success")
                        val first_try = mainData.getBoolean("first_try")

                        if (success) {
                            val dialog = AlertDialog.Builder(activity as Context)
                            dialog.setTitle("Reset Password")
                            dialog.setCancelable(false)
                            if (first_try) {
                                dialog.setMessage("Check you email, we sent an OTP")
                            } else {
                                dialog.setMessage("Please enter the last OTP you received in email")
                            }

                            dialog.setPositiveButton("OK") { text, listener ->
                                //goto OTP screen
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(
                                        R.id.frame,
                                        ForgotPasswordOTPFragment(etMobileNumber.text.toString())
                                    )
                                    ?.commit()
                            }

                            dialog.create()
                            dialog.show()


                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Account with this email or phone doesn't exist!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(
                        activity as Context,
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                requireActivity().finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(requireActivity())
            }
            dialog.create()
            dialog.show()
        }


    }

    fun validateFields() =
        etMobileNumber.text.toString().length == 10 && etEmail.text.toString().isNotEmpty()
}