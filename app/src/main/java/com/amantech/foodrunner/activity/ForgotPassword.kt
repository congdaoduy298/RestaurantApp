package com.amantech.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.amantech.foodrunner.R
import com.amantech.foodrunner.fragment.ForgotPasswordDetailsFragment
import com.amantech.foodrunner.fragment.HomeFragment

class ForgotPassword : AppCompatActivity() {

    lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        frameLayout = findViewById(R.id.frame)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                ForgotPasswordDetailsFragment()
            )
            .commit()

    }
}
