package com.saurav.foodlovers.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saurav.foodlovers.R
import com.saurav.foodlovers.database.RestaurantEntity
import com.saurav.foodlovers.fragment.FavoritesFragment
import com.saurav.foodlovers.fragment.RestaurantFragment
import com.squareup.picasso.Picasso

class FavoriteRecyclerAdapter(val context: Context, val restaurantList: MutableList<RestaurantEntity>, val txtNoFavLayout: RelativeLayout): RecyclerView.Adapter<FavoriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurants_single_row,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size

    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val info=restaurantList[position]
        var fav:Boolean=true

        holder.txtRestaurantName.text=info.restaurantName
        holder.txtRestaurantCost.text="\u20B9 ${info.restaurantCost}/person"
        holder.txtRestaurantRating.text=info.restaurantRating
//        Picasso.get().load(info.restaurantImage).error(R.drawable.default_restaurant).into(holder.imgRestaurantImage)
        Glide.with(holder.imgRestaurantImage.context).load(info.restaurantImage).error(R.drawable.default_restaurant).into(holder.imgRestaurantImage)
        holder.imgFav.setImageResource(R.drawable.ic_fav_filled)

        val restaurantEntity=RestaurantEntity(
            info.restaurant_Id,
            info.restaurantName,
            info.restaurantRating,
            info.restaurantCost,
            info.restaurantImage
        )

        holder.imgFav.setOnClickListener {
            val async= RestaurantRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 3).execute()
            val result=async.get()
            if (result){
                Toast.makeText(context,"Restaurant Removed from Favourites", Toast.LENGTH_SHORT).show()
                holder.imgFav.setImageResource(R.drawable.ic_imgfavorite)
                restaurantList.removeAt(position)
                notifyDataSetChanged()
                fav=false
            }else{
                Toast.makeText(context,"Some error occurred!!", Toast.LENGTH_SHORT).show()
            }

            if (restaurantList.size!=0)
                txtNoFavLayout.visibility=View.GONE
            else
                txtNoFavLayout.visibility=View.VISIBLE
        }

        holder.llContent.setOnClickListener {

            val manager: FragmentManager =
                (context as AppCompatActivity).supportFragmentManager
            val fragment= RestaurantFragment()
            context.supportActionBar?.title=info.restaurantName

            var bundle= Bundle()
            bundle.putString("restaurant_id",info.restaurant_Id.toString())
            bundle.putString("restaurant_name",info.restaurantName)
            bundle.putBoolean("favorite",fav)
            fragment.arguments=bundle
            manager.beginTransaction().replace(R.id.frame,fragment).commit()
        }

    }

    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantCost: TextView =view.findViewById(R.id.txtIndividualPrice)
        val txtRestaurantRating: TextView =view.findViewById(R.id.txtRestaurantRating)
        val imgRestaurantImage: ImageView =view.findViewById(R.id.imgRestaurantImage)
        val imgFav: ImageView =view.findViewById(R.id.imgFav)

        val llContent: LinearLayout =view.findViewById(R.id.llContent)

    }
}