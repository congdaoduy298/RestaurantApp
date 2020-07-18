package com.amantech.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amantech.foodrunner.R
import com.amantech.foodrunner.activity.RestaurantMenuActivity
import com.amantech.foodrunner.database.ResDatabase
import com.amantech.foodrunner.database.ResEntity
import com.amantech.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeRecyclerViewHolder>() {

    class HomeRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtResName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtResPrice: TextView = view.findViewById(R.id.txtResPrice)
        val txtResRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgResImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_restaurant, parent, false)
        return HomeRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
        val res: Restaurant = itemList[position]
        holder.txtResName.text = res.name
        holder.txtResPrice.text = res.cost_for_one + "/person"
        holder.txtResRating.text = res.rating
        Picasso.get().load(res.image_url).error(R.drawable.food_runner_logo)
            .into(holder.imgResImage)

        val resEntity = ResEntity(
            res.id.toInt(),
            res.name,
            res.cost_for_one,
            res.rating,
            res.image_url
        )

        val checkFav = DBAsyncTask(context, resEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgFav.setImageResource(R.drawable.ic_favorite_selected)
        } else {
            holder.imgFav.setImageResource(R.drawable.ic_favorite_default)
        }

        holder.imgFav.setOnClickListener {

            //if isNotFav
            if (!DBAsyncTask(holder.imgFav.context, resEntity, 1).execute()
                    .get()
            ) {
                val async =
                    DBAsyncTask(holder.imgFav.context, resEntity, 2).execute()
                val result = async.get()

                if (result) {
                    //if successfully added to favorites database..
                    holder.imgFav.setImageResource(R.drawable.ic_favorite_selected)
                } else {
                    //some error occurred while adding to database
                }
            }

            //else if isFav
            else {
                val async =
                    DBAsyncTask(context, resEntity, 3).execute()
                val result = async.get()

                //if successfully removed from favorite database
                if (result) {
                    holder.imgFav.setImageResource(R.drawable.ic_favorite_default)
                } else {
                    //some error occurred while removing from database
                }

            }

        }

        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("id", res.id)
            intent.putExtra("name", res.name)
            context.startActivity(intent)
        }
    }


    class DBAsyncTask(val context: Context, val resEntity: ResEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        Mode1->Check DB if book is favourite or not
        Mode2->Save the book into DB
        Mode3->Remove the favourite book
         */

        private val db = Room.databaseBuilder(context, ResDatabase::class.java, "books-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
//                    Check DB if Restaurant is favourite or not
                    val res: ResEntity? = db.resDao().getRestaurantById(resEntity.res_id.toString())
                    db.close()
                    return res != null
                }
                2 -> {
//                    Save the Restaurant into DB
                    db.resDao().insertRestaurant(resEntity)
                    db.close()
                    return true
                }
                3 -> {
//                    Remove the favourite restaurant
                    db.resDao().deleteRestaurant(resEntity)
                    db.close()
                    return true
                }
            }

            return false
        }


    }
}