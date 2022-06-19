package com.amantech.foodrunner.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.model.Item
import com.amantech.foodrunner.model.ItemOrder
import com.squareup.picasso.Picasso

class RestaurantMenuAdapter(val context: Context,
                            private val itemList: ArrayList<Item>,
                            private val itemOrderList: ArrayList<ItemOrder>,
                            private val idItemList: ArrayList<Int>
) :
    RecyclerView.Adapter<RestaurantMenuAdapter.RestaurantMenuViewHolder>() {


    class RestaurantMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgItemImage: ImageView = view.findViewById(R.id.imgItemImage)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnAddItem: ImageView = view.findViewById(R.id.iv_add)
        val btnRm: ImageView = view.findViewById(R.id.iv_rm)
        val count: TextView = view.findViewById(R.id.count)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)

        return RestaurantMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {
        val menuItem: Item = itemList[position]
        holder.txtItemName.text = menuItem.name
        Picasso.get().load(menuItem.url).error(R.drawable.food_runner_logo)
            .into(holder.imgItemImage)
        val cost = "$" + menuItem.price
        holder.txtItemPrice.text = cost


        var temp = 0
        holder.btnAddItem.setOnClickListener {
            temp = temp + 1
            val currentItem = ItemOrder(menuItem.price, temp, menuItem.name, menuItem.url)
            if (idItemList.contains(position)) {
                val index: Int = idItemList.indexOf(position)
                itemOrderList.set(index, currentItem)
            }
            else {
                itemOrderList.add(currentItem)
                idItemList.add(position)
            }
            holder.count.setText(temp.toString())
        }
        holder.btnRm.setOnClickListener {
            if (temp > 0){
                temp = temp - 1
                val index: Int = idItemList.indexOf(position)
                if (temp == 0) itemOrderList.removeAt(index)
                else {
                    val currentItem = ItemOrder(menuItem.price, temp, menuItem.name, menuItem.url)
                    itemOrderList.set(index, currentItem)
                }
                holder.count.setText(temp.toString())
            }
        }
    }

}