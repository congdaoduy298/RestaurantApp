package com.amantech.foodrunner.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.HomeRecyclerAdapter
import com.amantech.foodrunner.model.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    private lateinit var listRes: ArrayList<Restaurant>
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    //lateinit var customDialog: CustomDialog

    //var selectedOption: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
    }
    fun getRestaurantdata(){
        db = FirebaseDatabase.getInstance().getReference("restaurant")
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()) {
                   for (restaurantSnapshot in snapshot.children) {
                       val res = restaurantSnapshot.getValue(Restaurant::class.java)
                       listRes.add(res!!)
                   }
                   recyclerHome.adapter = context?.let { HomeRecyclerAdapter(it,listRes) }
                   recyclerHome.layoutManager = layoutManager
               }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        listRes = arrayListOf<Restaurant>()
        getRestaurantdata()
        recyclerHome.adapter = context?.let { HomeRecyclerAdapter(it,listRes) }
        recyclerHome.layoutManager = layoutManager
        return  view
    }

    class Restaurants(val data: ArrayList<Restaurant>)
}
