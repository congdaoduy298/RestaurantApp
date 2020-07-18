package com.amantech.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.database.ItemEntity

class CartItemsRecyclerAdapter(val context: Context, val cartItems: List<ItemEntity>) :
    RecyclerView.Adapter<CartItemsRecyclerAdapter.CartRecyclerViewHolder>() {

    class CartRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
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
        val item: ItemEntity = cartItems[position]

        holder.txtItemName.text = item.itemName
        val price = "Rs. ${item.itemPrice}"
        holder.txtItemPrice.text = price
    }


}