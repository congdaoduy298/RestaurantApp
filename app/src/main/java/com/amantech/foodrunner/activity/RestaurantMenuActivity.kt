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
import android.util.Log
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
import com.amantech.foodrunner.model.Item
import com.amantech.foodrunner.model.ItemOrder
import com.amantech.foodrunner.model.Restaurant
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import com.google.gson.GsonBuilder
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    private lateinit var db: DatabaseReference
    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var menuItemList: ArrayList<Item>
    private lateinit var itemOrderList: ArrayList<ItemOrder>
    private lateinit var idItemList: ArrayList<Int>
    lateinit var btnProceedToCart: Button
    lateinit var txtMenuHeader: TextView


    lateinit var recyclerAdapter: RestaurantMenuAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    fun getMenudata() {
        val position = intent.getStringExtra("position").toString()
        db = FirebaseDatabase.getInstance().getReference("restaurant/" + position + "/menus")
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (menusSnapshot in snapshot.children) {
                        val itemlist = menusSnapshot.getValue(Item::class.java)
                        menuItemList.add(itemlist!!)
                        //Log.d("khang",res.address.toString())
                    }
                    Log.d("cailonnguhoc", menuItemList.toString())
                    recyclerMenu.adapter = recyclerAdapter
                    recyclerMenu.layoutManager = layoutManager
                    //Log.d("khang1","ok")
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        var temp = 0
        menuItemList = arrayListOf<Item>()
        itemOrderList = arrayListOf<ItemOrder>()
        idItemList = arrayListOf<Int>()

        recyclerMenu = findViewById(R.id.recyclerMenu)

        initializeView()
        recyclerAdapter = RestaurantMenuAdapter(this@RestaurantMenuActivity, menuItemList, itemOrderList, idItemList)
        layoutManager = LinearLayoutManager(this@RestaurantMenuActivity)

        getMenudata()
        recyclerMenu.adapter = recyclerAdapter
        recyclerMenu.layoutManager = layoutManager


    }

    private fun initializeView() {
        toolbar = findViewById(R.id.toolbar)
        val name = intent.getStringExtra("name")
        //here set title to restaurant name received in intent
        if (intent != null) {
            toolbar.title = name
        } else {
            toolbar.title = "Restaurant Menu"
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerMenu = findViewById(R.id.recyclerMenu)

        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        txtMenuHeader = findViewById(R.id.txtMenuHeader)


        layoutManager = LinearLayoutManager(this@RestaurantMenuActivity)
        btnProceedToCart.setOnClickListener {
          val intent = Intent(this@RestaurantMenuActivity, CartPageActivity::class.java)
            intent.putExtra("name", name)
            intent.putParcelableArrayListExtra("itemOrderList", itemOrderList)
            startActivity(intent)
       }
    }

}
class Items(val data: ArrayList<Item>)