package com.amantech.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.opengl.Visibility
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.HomeRecyclerAdapter
import com.amantech.foodrunner.adapter.RestaurantMenuAdapter
import com.amantech.foodrunner.database.ItemDatabase
import com.amantech.foodrunner.database.ItemEntity
import com.amantech.foodrunner.fragment.Restaurants
import com.amantech.foodrunner.model.Item
import com.amantech.foodrunner.model.Restaurant
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var menuItemList = arrayListOf<Item>()
    lateinit var btnProceedToCart: Button
    lateinit var txtMenuHeader: TextView

    lateinit var recyclerAdapter: RestaurantMenuAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var resultCode: Int = -1

    var restaurant_id: Int? = null

    fun disableProceedToCart() {
        btnProceedToCart.visibility = View.GONE
        Toast.makeText(
            this@RestaurantMenuActivity,
            "Proceed button removed",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun enableProceedToCart() {
        btnProceedToCart.visibility = View.VISIBLE
        Toast.makeText(
            this@RestaurantMenuActivity,
            "Proceed button Enabled",
            Toast.LENGTH_SHORT
        ).show()
    }


    override fun onBackPressed() {
        if (DBAsyncTask(this@RestaurantMenuActivity, 1).execute().get()) {
            //create alert dialog

            val dialog = AlertDialog.Builder(this@RestaurantMenuActivity)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will reset cart items. Do you still want to proceed?")

            dialog.setPositiveButton("YES") { text, listener ->
                //clear cart and go back
                if (DBAsyncTask(this@RestaurantMenuActivity, 0).execute().get()) {
                    //cleared
                    Toast.makeText(
                        this@RestaurantMenuActivity,
                        "Cart database cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                    super.onBackPressed()
                } else {
                    Toast.makeText(
                        this@RestaurantMenuActivity,
                        "Cart database not cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.setNegativeButton("NO") { text, listener ->
                //clear cart and go back
                dialog.create().dismiss()
            }

            dialog.create()
            dialog.show()

        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        initializeView()

        //networking part
        val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)

        val id = intent.getStringExtra("id")
        restaurant_id = id!!.toInt()
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$id"

        if (ConnectionManager().checkConnectivity(this@RestaurantMenuActivity)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(
                    Request.Method.GET, url, null, Response.Listener {
                        val mainData = it.getJSONObject("data")

                        try {
                            progressLayout.visibility = View.GONE
                            val success = mainData.getBoolean("success")


                            if (success) {
                                val gson = GsonBuilder().create()
                                val allRes =
                                    gson.fromJson(mainData.toString(), Items::class.java)
                                menuItemList = allRes.data
                                recyclerAdapter =
                                    RestaurantMenuAdapter(this@RestaurantMenuActivity, menuItemList)

                                recyclerMenu.adapter = recyclerAdapter
                                recyclerMenu.layoutManager = layoutManager

                            } else {
                                Toast.makeText(
                                    this@RestaurantMenuActivity,
                                    "Some Error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@RestaurantMenuActivity,
                                "Some unexpected error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    },
                    Response.ErrorListener {
                        //Handle Errors

                        Toast.makeText(
                            this@RestaurantMenuActivity,
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

            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(this@RestaurantMenuActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantMenuActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    private fun initializeView() {
        toolbar = findViewById(R.id.toolbar)
        //here set title to restaurant name received in intent
        if (intent != null) {
            toolbar.title = intent.getStringExtra("name")
        } else {
            toolbar.title = "Restaurant Menu"
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerMenu = findViewById(R.id.recyclerMenu)

        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        txtMenuHeader = findViewById(R.id.txtMenuHeader)

        progressLayout = findViewById(R.id.progressLayout)

        progressBar = findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(this@RestaurantMenuActivity)

        btnProceedToCart.setOnClickListener {
            val intent = Intent(this@RestaurantMenuActivity, CartPageActivity::class.java)
            intent.putExtra("name", toolbar.title.toString())

            intent.putExtra("restaurant_id", restaurant_id)

            startActivity(intent)
        }
    }


    class DBAsyncTask(val context: Context, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, ItemDatabase::class.java, "cart-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                //Remove all items from cart
                0 -> {
                    db.itemDao().clearCart()
                    db.close()
                    return true
                }
                //check if cart is empty or not
                1 -> {
                    val cartItems = db.itemDao().getAllItems()
                    db.close()
                    return cartItems.size > 0
                }
            }
            return false
        }

    }
}

class Items(val data: ArrayList<Item>)