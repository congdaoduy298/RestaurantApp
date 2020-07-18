package com.amantech.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.amantech.foodrunner.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var btnOkOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        btnOkOrder = findViewById(R.id.btnOkOrder)

        btnOkOrder.setOnClickListener {
            startHomeScreen()
        }
    }

    override fun onBackPressed() {
        btnOkOrder.callOnClick()
    }

    private fun startHomeScreen() {
        val intent = Intent(this@OrderPlacedActivity, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }
}