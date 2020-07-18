package com.amantech.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amantech.foodrunner.R
import com.amantech.foodrunner.activity.RestaurantMenuActivity
import com.amantech.foodrunner.database.ItemDatabase
import com.amantech.foodrunner.database.ItemEntity
import com.amantech.foodrunner.database.ResDatabase
import com.amantech.foodrunner.database.ResEntity
import com.amantech.foodrunner.model.Item
import com.amantech.foodrunner.model.Restaurant

class RestaurantMenuAdapter(val context: Context, val itemList: ArrayList<Item>) :
    RecyclerView.Adapter<RestaurantMenuAdapter.RestaurantMenuViewHolder>() {


    class RestaurantMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSrNo: TextView = view.findViewById(R.id.txtSrNo)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnAddItem: Button = view.findViewById(R.id.btnAddItem)
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
        holder.txtSrNo.text = (itemList.indexOf(menuItem) + 1).toString()
        holder.txtItemName.text = menuItem.name
        val cost = "Rs. " + menuItem.cost_for_one
        holder.txtItemPrice.text = cost

        val itemEntity = ItemEntity(
            menuItem.id.toInt(),
            menuItem.name,
            menuItem.cost_for_one
        )

        //check if cart is empty or not


        holder.btnAddItem.setOnClickListener {
            //if not in cart
            if (!DBAsyncTask(holder.btnAddItem.context, itemEntity, 1).execute()
                    .get()
            ) {
                //add to cart
                val async =
                    DBAsyncTask(context, itemEntity, 2).execute()
                val result = async.get()

                //if successfully added to cart
                if (result) {
                    //change text of button to "Remove"
                    holder.btnAddItem.text = "Remove"
                    //change background of button to "Yellow"
                    val removeColor =
                        ContextCompat.getColor(holder.btnAddItem.context, R.color.cartYellowColor)
                    holder.btnAddItem.setBackgroundColor(removeColor)
                } else {
                    //some error occurred while adding to database
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
//            else remove from cart
            else {
                //remove from cart
                //CHECK
                //if successfully removed from cart
                if (DBAsyncTask(holder.btnAddItem.context, itemEntity, 3).execute().get()) {
                    //change text of button to "Add"
                    holder.btnAddItem.text = "Add"
                    //change background of button to "Primary Color"
                    val addColor =
                        ContextCompat.getColor(holder.btnAddItem.context, R.color.colorPrimary)
                    holder.btnAddItem.setBackgroundColor(addColor)
                } else {
                    //some error occurred while adding to database
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            }

            if (context is RestaurantMenuActivity) {
                val myActivity = context
                //CHECK
                //if cart is empty
                if (DBAsyncTask(holder.btnAddItem.context, itemEntity, 4).execute().get()) {
                    myActivity.enableProceedToCart()

                } else {
                    myActivity.disableProceedToCart()
                }
            }

        }
    }


    class DBAsyncTask(val context: Context, val itemEntity: ItemEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {


        private val db = Room.databaseBuilder(context, ItemDatabase::class.java, "cart-db").build()

        /*
        Mode1->Check DB if item is in cart or not
        Mode2->Add the item into cart
        Mode3->Remove the item from cart
        Mode4->Remove all item from cart
        Mode5->Check if cart is empty or not
         */

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
//                    Check DB if item is in cart or not
                    val item: ItemEntity? = db.itemDao().getItemById(itemEntity.item_id.toString())
                    db.close()
                    return item != null
                }
                2 -> {
//                    Add the item into cart
                    db.itemDao().insertItem(itemEntity)
                    db.close()
                    return true
                }
                3 -> {
//                    Remove the item from cart
                    db.itemDao().deleteItem(itemEntity)
                    db.close()
                    return true
                }
                4 -> {
//                    Get all items and check it the length of list is 0 or not
                    val cartItems = db.itemDao().getAllItems()
                    db.close()
                    return cartItems.size > 0
                }
            }

            return false
        }


    }

}