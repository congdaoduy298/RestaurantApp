package com.amantech.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amantech.foodrunner.R

class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtMobile: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtMobile = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)

        //getting data from shared preferences
        val sharedPreferences = this.activity?.getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )

        txtName.text = sharedPreferences?.getString("Name","DEFAULT_NAME")
        txtMobile.text = sharedPreferences?.getString("MobNo","DEFAULT_MOB")
        txtEmail.text = sharedPreferences?.getString("Email","DEFAULT_EMAIL")
        txtAddress.text = sharedPreferences?.getString("Address","DEFAULT_ADDRESS")

        return view
    }

}