package com.amantech.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.database.ItemEntity
import com.amantech.foodrunner.model.ItemOrder

class CartItemsRecyclerAdapter(val context: Context,private val cartItems: List<ItemOrder>) :
    RecyclerView.Adapter<CartItemsRecyclerAdapter.CartRecyclerViewHolder>() {

    class CartRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtNumItem: TextView = view.findViewById(R.id.txtNumItem)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_item, parent, false)
        return CartRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartRecyclerViewHolder, position: Int) {
        val item: ItemOrder = cartItems[position]

        holder.txtItemName.text = item.name
        holder.txtNumItem.text = item.count.toString()
        val cost = "$" + item.price
        holder.txtItemPrice.text = cost
    }


}