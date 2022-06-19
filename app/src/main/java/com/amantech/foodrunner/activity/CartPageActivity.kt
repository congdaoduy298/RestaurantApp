package com.amantech.foodrunner.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.CartItemsRecyclerAdapter
import com.amantech.foodrunner.database.ItemEntity
import com.amantech.foodrunner.model.ItemOrder

class CartPageActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var txtRestaurantName: TextView
    lateinit var txtTotalCost: TextView
    lateinit var btnPlaceOrder: Button

    lateinit var recyclerCartItems: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartItemsRecyclerAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences

    private var itemOrderList: ArrayList<ItemOrder>? = null


    var orderPlaced: Int = 0

    fun calculateTotalCost(itemList: ArrayList<ItemOrder>): Int {
        var total = 0;
        for (item in itemList) {
            total += item.price*item.count
        }
        return total
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cart_page)

        itemOrderList = arrayListOf<ItemOrder>()
        initializeView()
        Log.d("SOS CarPage 2", itemOrderList.toString())

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )

        recyclerAdapter = CartItemsRecyclerAdapter(this@CartPageActivity, itemOrderList!!)
        recyclerCartItems.adapter = recyclerAdapter
        recyclerCartItems.layoutManager = layoutManager
    }

    private fun initializeView() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "My Cart"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        txtRestaurantName = findViewById(R.id.txtRestaurantName)
        recyclerCartItems = findViewById(R.id.recyclerCartItems)
        txtTotalCost = findViewById(R.id.txtTotalCost)
        layoutManager = LinearLayoutManager(this@CartPageActivity)

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        if (intent != null) {
            txtRestaurantName.text = intent.getStringExtra("name")//name as restaurant name
            itemOrderList = intent.getParcelableArrayListExtra("itemOrderList")
            val total_cost = calculateTotalCost(itemOrderList!!)
            txtTotalCost.text = "Total: $" + total_cost.toString()
            intent.putExtra("itemOrderList", itemOrderList)
            Log.d("SOS CarPage 1","Total: " + total_cost.toString())
        }

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        btnPlaceOrder.setOnClickListener {
            orderPlaced = 1
            progressLayout.visibility = View.VISIBLE
        }
    }
}
