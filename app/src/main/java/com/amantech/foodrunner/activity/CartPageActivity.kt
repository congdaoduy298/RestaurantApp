package com.amantech.foodrunner.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.CartItemsRecyclerAdapter
import com.amantech.foodrunner.adapter.HomeRecyclerAdapter
import com.amantech.foodrunner.database.ItemEntity
import com.amantech.foodrunner.model.ItemOrder
import com.amantech.foodrunner.model.Order
import com.amantech.foodrunner.model.Restaurant
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    private var orderHistory: Order? = null

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
        val restaurantName = intent.getStringExtra("name")
        var total_cost = 0

        if (intent != null) {
            txtRestaurantName.text = restaurantName
            itemOrderList = intent.getParcelableArrayListExtra("itemOrderList")
            total_cost = calculateTotalCost(itemOrderList!!)
            txtTotalCost.text = "Total: $" + total_cost.toString()
            intent.putExtra("itemOrderList", itemOrderList)
        }

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnPlaceOrder.setOnClickListener {
            orderPlaced = 1
            val user = Firebase.auth.currentUser
            val uid = user!!.uid
            Toast.makeText(
                this@CartPageActivity,
                "Place order successfully!",
                Toast.LENGTH_SHORT
            ).show()
            var dataReference = FirebaseDatabase.getInstance().getReference("profiles").child(uid)
            dataReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val numPreviousOrder =
                        snapshot.child("numPreviousOrder").getValue(Int::class.java)
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    val formatted = current.format(formatter)
                    orderHistory = Order(restaurantName, total_cost, formatted, itemOrderList!!)
                    dataReference.child("listOrder").child(numPreviousOrder.toString()).setValue(orderHistory)
                    dataReference.child("numPreviousOrder").setValue(numPreviousOrder!! + 1)
                } else {
                    Log.d("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
