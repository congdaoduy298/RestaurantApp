package com.amantech.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.CartItemsRecyclerAdapter
import com.amantech.foodrunner.adapter.FavoritesRecyclerAdapter
import com.amantech.foodrunner.database.ItemDatabase
import com.amantech.foodrunner.database.ItemEntity
import com.amantech.foodrunner.database.ResDatabase
import com.amantech.foodrunner.database.ResEntity
import com.amantech.foodrunner.model.ItemOrder
import com.amantech.foodrunner.model.Restaurant
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.json.JSONException
import org.json.JSONObject

class CartPageActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var txtRestaurantName: TextView
    lateinit var btnPlaceOrder: Button

    lateinit var recyclerCartItems: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartItemsRecyclerAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences

    lateinit var cartItemList: ArrayList<ItemOrder>

    var orderPlaced: Int = 0

    fun calculateTotalCost(itemList: List<ItemEntity>): Int {
        var total = 0;
        for (item in itemList) {
            total += item.itemPrice.toInt()
        }
        return total
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cart_page)
        initializeView()

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )

        //get cart items
        cartItemList = arrayListOf<ItemOrder>()

        recyclerAdapter = CartItemsRecyclerAdapter(this@CartPageActivity, cartItemList)
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
        layoutManager = LinearLayoutManager(this@CartPageActivity)

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        if (intent != null) {
            txtRestaurantName.text = intent.getStringExtra("name")//name as restaurant name
        }
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        btnPlaceOrder.setOnClickListener {

            orderPlaced = 1

            //make progress layout visible
            progressLayout.visibility = View.VISIBLE

            //process payment
            val foodItems = arrayListOf<FoodItem>()
            val user_id = sharedPreferences.getString("user_id", "0")!!.toInt()
            val restaurant_id = intent.getIntExtra("restaurant_id", -1)
//            val total_cost = calculateTotalCost(cartItemList)
            val foodItemList: List<FoodItem> = foodItems
//            val params = Params(user_id, restaurant_id, total_cost, foodItemList)

            val gson = GsonBuilder().create()
            val parameter = gson.toJson(params)
            print(parameter)

            placeOrder(parameter)


        }


    }


    fun placeOrder(jsonString: String) {

        val queue = Volley.newRequestQueue(this@CartPageActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        //check correct parsing
        val jsonParams = JSONObject(jsonString)

        if (ConnectionManager().checkConnectivity(this@CartPageActivity)) {

            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    val mainData = it.getJSONObject("data")
                    orderPlaced = 0
                    try {
                        progressLayout.visibility = View.GONE
                        val success = mainData.getBoolean("success")

                        if (success) {
                            println("Payment Successful!!")
                            if (DBAsyncTask(this@CartPageActivity).execute().get()) {

                                super.onBackPressed()
                            }

                            //move to order placed screen
                            val intent =
                                Intent(this@CartPageActivity, OrderPlacedActivity::class.java)
                            startActivity(intent)
                            finishAffinity()

                        } else {
                            Toast.makeText(
                                this@CartPageActivity,
                                "Payment did not succeed",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: JSONException) {
                        orderPlaced = 0
                        Toast.makeText(
                            this@CartPageActivity,
                            "Some unexpected error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    orderPlaced = 0
                    Toast.makeText(
                        this@CartPageActivity,
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
            val dialog = AlertDialog.Builder(this@CartPageActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@CartPageActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context) :
        AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, ItemDatabase::class.java, "cart-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.itemDao().clearCart()
            db.close()
            return true
        }

    }


    class RetrieveCartItems(val context: Context) :
        AsyncTask<Void, Void, List<ItemEntity>>() {

        private val db = Room.databaseBuilder(context, ItemDatabase::class.java, "cart-db").build()

        override fun doInBackground(vararg params: Void?): List<ItemEntity> {
            return db.itemDao().getAllItems()
        }

    }


    override fun onBackPressed() {
        when (orderPlaced) {
            0 -> {
                super.onBackPressed()
            }
            1 -> {
                Toast.makeText(
                    this@CartPageActivity,
                    "Please wait while payment is processing!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

class Params(
    val user_id: Int,
    val restaurant_id: Int,
    val total_cost: Int,
    val food: List<FoodItem>
)

class FoodItem(val food_item_id: Int)
