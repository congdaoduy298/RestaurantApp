package com.amantech.foodrunner.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.fragment.Order

class OrderHistoryRecyclerAdapter(val context: Context, val orderHistory: List<Order>) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryRecyclerViewHolder>() {


    class OrderHistoryRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        val llFoodItems: LinearLayout = view.findViewById(R.id.llFoodItems)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryRecyclerViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_single_restaurant_history, parent, false)

        return OrderHistoryRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistory.size
    }

    override fun onBindViewHolder(holder: OrderHistoryRecyclerViewHolder, position: Int) {

        //for not recycling view holders
        holder.setIsRecyclable(false)

        val order = orderHistory[position]
        holder.txtRestaurantName.text = order.restaurant_name

        val date = order.order_placed_at.subSequence(0, 8) as String
        val myDate = date.replace("-", "/")
        holder.txtOrderDate.text = myDate

        for (item in order.food_items) {

            val inflater: LayoutInflater? =
                context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            val llSingleItem =
                inflater?.inflate(R.layout.recycler_cart_single_item, null) as LinearLayout

            val txtItemName: TextView = llSingleItem.findViewById(R.id.txtItemName)

            val txtItemPrice: TextView = llSingleItem.findViewById(R.id.txtItemPrice)

            val itemName = item.name
            val itemCost = "Rs. ${item.cost}"
            txtItemName.text = itemName
            txtItemPrice.text = itemCost

            holder.llFoodItems.addView(llSingleItem)

        }
    }

}