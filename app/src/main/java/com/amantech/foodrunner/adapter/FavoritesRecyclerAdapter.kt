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
import com.squareup.picasso.Picasso

class FavoritesRecyclerAdapter(val context: Context,private val itemList: List<ResEntity>) :
    RecyclerView.Adapter<FavoritesRecyclerAdapter.FavoriteRecyclerViewHolder>() {

    class FavoriteRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtResName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtResPrice: TextView = view.findViewById(R.id.txtResPrice)
        val txtResRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgResImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favorite_single_row, parent, false)
        return FavoritesRecyclerAdapter.FavoriteRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavoriteRecyclerViewHolder, position: Int) {
        val res: ResEntity = itemList[position]
        holder.txtResName.text = res.resName
        holder.txtResPrice.text = res.resPrice + "/person"
        holder.txtResRating.text = res.resRating
        Picasso.get().load(res.resImage).error(R.drawable.food_runner_logo).into(holder.imgResImage)
//        holder.imgFav.setImageResource(R.drawable.ic_favorite_selected)
        holder.imgFav.setImageResource(R.drawable.ic_favorite_selected)

        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("id", res.res_id.toString())
            intent.putExtra("name", res.resName)
            context.startActivity(intent)
        }

        /*val checkFav = DBAsyncTask(context, res, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgFav.setImageResource(R.drawable.ic_favorite_selected)
        } else {
            holder.imgFav.setImageResource(R.drawable.ic_favorite_default)
        }

        holder.imgFav.setOnClickListener {
            //if isNotFav
            if (!DBAsyncTask(holder.imgFav.context, res, 1).execute()
                    .get()
            ) {
                val async =
                    HomeRecyclerAdapter.DBAsyncTask(holder.imgFav.context, res, 2).execute()
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
                    HomeRecyclerAdapter.DBAsyncTask(context, res, 3).execute()
                val result = async.get()

                //if successfully removed from favorite database
                if (result) {
                    holder.imgFav.setImageResource(R.drawable.ic_favorite_default)
                } else {
                    //some error occurred while removing from database
                }

            }

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
                    val book: ResEntity? =
                        db.resDao().getRestaurantById(resEntity.res_id.toString())
                    db.close()
                    return book != null
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
        }*/


    }

}