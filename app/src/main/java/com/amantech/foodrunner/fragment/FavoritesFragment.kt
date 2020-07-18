package com.amantech.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.FavoritesRecyclerAdapter
import com.amantech.foodrunner.database.ResDatabase
import com.amantech.foodrunner.database.ResEntity

class FavoritesFragment : Fragment() {

    lateinit var recyclerFavorite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavoritesRecyclerAdapter
    lateinit var defaultLayout: RelativeLayout

    var dbResList = listOf<ResEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerFavorite = view.findViewById(R.id.recyclerFav)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        defaultLayout = view.findViewById(R.id.favEmpty)

        layoutManager = LinearLayoutManager(activity)


        dbResList = RetrieveFavorites(activity as Context).execute().get()

        if (activity != null) {
            defaultLayout.visibility = View.GONE
            progressLayout.visibility = View.GONE

            if (dbResList.isEmpty()) {
                defaultLayout.visibility = View.VISIBLE
            }
            else {
                recyclerAdapter = FavoritesRecyclerAdapter(activity as Context, dbResList)
                recyclerFavorite.adapter = recyclerAdapter
                recyclerFavorite.layoutManager = layoutManager
            }

        }

        return view
    }

    class RetrieveFavorites(val context: Context) : AsyncTask<Void, Void, List<ResEntity>>() {
        override fun doInBackground(vararg params: Void?): List<ResEntity> {
            val db = Room.databaseBuilder(context, ResDatabase::class.java, "books-db").build()
            return db.resDao().getAllRestaurants()
        }

    }

}