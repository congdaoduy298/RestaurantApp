package com.amantech.foodrunner.fragment

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.OrderHistoryRecyclerAdapter
import com.amantech.foodrunner.model.ItemOrder
import com.amantech.foodrunner.model.Order
import com.amantech.foodrunner.model.Restaurant
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var orderHistoryList: ArrayList<Order>
    lateinit var itemOrderList: ArrayList<ItemOrder>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurant)
        layoutManager = LinearLayoutManager(activity)


        val sharedPreferences = this.activity?.getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )

        orderHistoryList = arrayListOf<Order>()



        val user = Firebase.auth.currentUser
        user?.let {
            val uid = user.uid
            var dataReference = FirebaseDatabase.getInstance().getReference("profiles").child(uid)
            dataReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val numPreviousOrder =
                        snapshot.child("numPreviousOrder").getValue(Int::class.java)

                    for (item: Int in 0..numPreviousOrder!!.minus(1)) {

                        val restaurantName = snapshot.child("listOrder").child(item.toString()).child("restaurant_name").getValue(String::class.java)
                        val orderPlacedAt = snapshot.child("listOrder").child(item.toString()).child("order_placed_at").getValue(String::class.java)
                        val total_cost = snapshot.child("listOrder").child(item.toString()).child("total_cost").getValue(Int::class.java)

                        itemOrderList = arrayListOf<ItemOrder>()
                        for (restaurantSnapshot in snapshot.child("listOrder").child(item.toString()).child("food_items").children) {
                            val itemOrder = restaurantSnapshot.getValue(ItemOrder::class.java)
                            itemOrderList.add(itemOrder!!)
                        }
                        Log.d("Item Order List", itemOrderList.toString())
                        val orderHistory = Order(restaurantName, total_cost, orderPlacedAt!!, itemOrderList)
                        orderHistoryList.add(orderHistory!!)
                        recyclerAdapter = OrderHistoryRecyclerAdapter(
                            activity as Context,
                            orderHistoryList
                        )

                        recyclerRestaurant.adapter = recyclerAdapter
                        recyclerRestaurant.layoutManager = layoutManager
                    }

                } else {
                    Log.d("TAG", task.exception!!.message!!) //Don't ignore potential errors!
                }
            }
        }

        Log.d("Order History List", orderHistoryList.toString())
        recyclerAdapter = OrderHistoryRecyclerAdapter(
            activity as Context,
            orderHistoryList
        )

        recyclerRestaurant.adapter = recyclerAdapter
        recyclerRestaurant.layoutManager = layoutManager

        return view
    }
}




