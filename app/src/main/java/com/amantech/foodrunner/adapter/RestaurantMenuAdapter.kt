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
                            private val itemOrderList: ArrayList<ItemOrder>
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
            if (itemOrderList.contains(currentItem)) {
                val index: Int = itemOrderList.indexOf(currentItem)
                itemOrderList.removeAt(index)
                val currentItem = ItemOrder(menuItem.price, temp, menuItem.name, menuItem.url)
                itemOrderList.add(index, currentItem)
            }
            else itemOrderList.add(currentItem)
            Log.d("SOS", itemOrderList.toString())
            holder.count.setText(temp.toString())
            itemOrderList.add(currentItem)
        }
        holder.btnRm.setOnClickListener {
            if (temp==0){
                temp=0
            }else{
                temp = temp - 1
                holder.count.setText(temp.toString())
                itemOrderList.removeLast()
            }
        }
        //        btnAddItem.setOnClickListener {
//            temp = temp + 1
//            count.setText(temp.toString())
//        }
//        btnRm.setOnClickListener {
//            if (temp==0){
//                Toast.makeText(this, "No Item", Toast.LENGTH_SHORT).show()
//            }else{
//                temp = temp - 1
//                count.setText(temp.toString())
//            }
//        }
//        val itemEntity = ItemEntity(
//            menuItem.id.toInt(),
//            menuItem.name,
//            menuItem.cost_for_one
//        )

        //check if cart is empty or not


//        holder.btnAddItem.setOnClickListener {
////                //if successfully added to cart
////                if (result) {
////                    //change text of button to "Remove"
////                    holder.btnAddItem.text = "Remove"
////                    //change background of button to "Yellow"
//            val removeColor =
//                ContextCompat.getColor(holder.btnAddItem.context, R.color.cartYellowColor)
//            holder.btnAddItem.setBackgroundColor(removeColor)
//
//            holder.btnAddItem.text = "Add"
////                    //change background of button to "Primary Color"
//            val addColor =
//                ContextCompat.getColor(holder.btnAddItem.context, R.color.colorPrimary)
//            holder.btnAddItem.setBackgroundColor(addColor)
//                } else {
//                    //some error occurred while adding to database
//                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
//                }
//
//            }
//
//            if (context is RestaurantMenuActivity) {
//                val myActivity = context
//                //CHECK
//                //if cart is empty
//                if (DBAsyncTask(holder.btnAddItem.context, itemEntity, 4).execute().get()) {
//                    myActivity.enableProceedToCart()
//
//                } else {
//                    myActivity.disableProceedToCart()
//                }
//            }
//
//        }
//    }
//
//
//    class DBAsyncTask(val context: Context, val itemEntity: ItemEntity, val mode: Int) :
//        AsyncTask<Void, Void, Boolean>() {
//
//
//        private val db = Room.databaseBuilder(context, ItemDatabase::class.java, "cart-db").build()
//
//        /*
//        Mode1->Check DB if item is in cart or not
//        Mode2->Add the item into cart
//        Mode3->Remove the item from cart
//        Mode4->Remove all item from cart
//        Mode5->Check if cart is empty or not
//         */
//
//        override fun doInBackground(vararg params: Void?): Boolean {
//
//            when (mode) {
//                1 -> {
////                    Check DB if item is in cart or not
//                    val item: ItemEntity? = db.itemDao().getItemById(itemEntity.item_id.toString())
//                    db.close()
//                    return item != null
//                }
//                2 -> {
////                    Add the item into cart
//                    db.itemDao().insertItem(itemEntity)
//                    db.close()
//                    return true
//                }
//                3 -> {
////                    Remove the item from cart
//                    db.itemDao().deleteItem(itemEntity)
//                    db.close()
//                    return true
//                }
//                4 -> {
////                    Get all items and check it the length of list is 0 or not
//                    val cartItems = db.itemDao().getAllItems()
//                    db.close()
//                    return cartItems.size > 0
//                }
//            }
//
//            return false
//        }


    }

}