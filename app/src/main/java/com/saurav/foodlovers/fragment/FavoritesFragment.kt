package com.saurav.foodlovers.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.saurav.foodlovers.R
import com.saurav.foodlovers.adapter.FavoriteRecyclerAdapter
import com.saurav.foodlovers.adapter.RestaurantRecyclerAdapter
import com.saurav.foodlovers.database.RestaurantDatabase
import com.saurav.foodlovers.database.RestaurantEntity

class FavoritesFragment : Fragment() {

    lateinit var recyclerFavorites: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavoriteRecyclerAdapter
    lateinit var txtNoFav: TextView
    lateinit var txtNoFavLayout: RelativeLayout

    var dbRestaurantList= mutableListOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerFavorites=view.findViewById(R.id.recyclerFavorites)
//        progressLayout=view.findViewById(R.id.progressLayout)
//        progressBar=view.findViewById(R.id.progressBar)
        txtNoFav=view.findViewById(R.id.txtNoFav)
        txtNoFavLayout=view.findViewById(R.id.txtNoFavLayout)

        layoutManager= LinearLayoutManager(activity)

        dbRestaurantList=RetrieveFavourites(activity as Context).execute().get()

        setUpFavoritesFragment()

        return view
    }

    private fun setUpFavoritesFragment(){
        if (dbRestaurantList.size!=0)
            txtNoFavLayout.visibility=View.GONE
        else
            txtNoFavLayout.visibility=View.VISIBLE
        if (activity!=null){
//            progressLayout.visibility=View.GONE
            recyclerAdapter= FavoriteRecyclerAdapter(activity as Context,dbRestaurantList,txtNoFavLayout)
            recyclerFavorites.adapter=recyclerAdapter
            recyclerFavorites.layoutManager=layoutManager
        }
    }

    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, MutableList<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): MutableList<RestaurantEntity> {
            val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"foodlovers-db").build()       //Necessary to initialize database

            return db.restaurantDao().getAllRestaurants()
        }

    }

}
