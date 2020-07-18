package com.amantech.foodrunner.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordOTPFragment(val mobile: String) : Fragment() {

    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_forgot_password_o_t_p, container, false)

        etOTP = view.findViewById(R.id.etOTP)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )

        btnSubmit.setOnClickListener {
            if (validateFields()) {

                //internet validation part
                validateAndReset()

            } else {
                Toast.makeText(activity as Context, "Incomplete credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    //check for only integer passwords of 6 digits
    fun validateFields() =
        etOTP.text.toString().length == 4 && (etNewPassword.text.toString() == etConfirmPassword.text.toString()) && etNewPassword.text.toString().length >= 6


    fun validateAndReset() {

        val queue = Volley.newRequestQueue(activity)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", etNewPassword.text.toString())
        jsonParams.put("otp", etOTP.text.toString())

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    val mainData = it.getJSONObject("data")

                    try {
                        val success = mainData.getBoolean("success")
//                        val message = mainData.getString("successMessage")


                        //so getting incorrect otp
                        if (success) {
                            val dialog = AlertDialog.Builder(activity as Context)
                            dialog.setTitle("Reset Password")
                            dialog.setCancelable(false)
                            dialog.setMessage("Password changed successfully")


                            dialog.setPositiveButton("OK") { text, listener ->
                                //clear shared preferences
                                //goto login screen
                                sharedPreferences.edit().clear().apply()
                                activity?.onBackPressed()
                            }

                            dialog.create()
                            dialog.show()


                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Incorrect OTP",
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


}