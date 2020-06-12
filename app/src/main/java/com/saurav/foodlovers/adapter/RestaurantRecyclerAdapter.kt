package com.saurav.foodlovers.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.saurav.foodlovers.R
import com.saurav.foodlovers.database.RestaurantDatabase
import com.saurav.foodlovers.database.RestaurantEntity
import com.saurav.foodlovers.fragment.RestaurantFragment
import com.saurav.foodlovers.model.Restaurant
import com.squareup.picasso.Picasso


class RestaurantRecyclerAdapter(val context: Context,val restaurantList: ArrayList<Restaurant>): RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurants_single_row,parent,false)

        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val info=restaurantList[position]
        var fav:Boolean=false

        holder.txtRestaurantName.text=info.name
        holder.txtRestaurantCost.text="\u20B9 ${info.cost_for_one}/person"
        holder.txtRestaurantRating.text=info.rating
//        Picasso.get().load(info.image_url).error(R.drawable.default_restaurant).into(holder.imgRestaurantImage)
        Glide.with(holder.imgRestaurantImage.context).load(info.image_url).error(R.drawable.default_restaurant).into(holder.imgRestaurantImage)

        val restaurantEntity=RestaurantEntity(
            info.id.toInt(),
            info.name,
            info.rating,
            info.cost_for_one,
            info.image_url
        )

        val checkFav=DBAsyncTask(context,restaurantEntity,1).execute()     //To execute th doInBackGround()
        val isFav=checkFav.get()        //Gets the return value of the doInBackGround() in AsyncTask

        if (isFav){
            holder.imgFav.setImageResource(R.drawable.ic_fav_filled)
            fav=true
        }else{
            holder.imgFav.setImageResource(R.drawable.ic_imgfavorite)
            fav=false
        }

        holder.imgFav.setOnClickListener {
            if (!DBAsyncTask(context,restaurantEntity,1).execute().get()){

                val async=DBAsyncTask(context,restaurantEntity,2).execute()
                val result=async.get()
                if (result){
                    Toast.makeText(context,"Restaurant Added to Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFav.setImageResource(R.drawable.ic_fav_filled)
                    fav=true
                }else{
                    Toast.makeText(context,"Some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            }else{

                val async=DBAsyncTask(context,restaurantEntity,3).execute()
                val result=async.get()
                if (result){
                    Toast.makeText(context,"Restaurant Removed from Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFav.setImageResource(R.drawable.ic_imgfavorite)
                    fav=false
                }else{
                    Toast.makeText(context,"Some error occurred!!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        holder.llContent.setOnClickListener {

            val manager: FragmentManager =
                (context as AppCompatActivity).supportFragmentManager
            val fragment=RestaurantFragment()
            context.supportActionBar?.title=info.name

            var bundle=Bundle()
            bundle.putString("restaurant_id",info.id)
            bundle.putString("restaurant_name",info.name)
            bundle.putBoolean("favorite",fav)
            fragment.arguments=bundle
            manager.beginTransaction().replace(R.id.frame,fragment).commit()

        }
    }

    class RestaurantViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantCost: TextView =view.findViewById(R.id.txtIndividualPrice)
        val txtRestaurantRating: TextView =view.findViewById(R.id.txtRestaurantRating)
        val imgRestaurantImage: ImageView =view.findViewById(R.id.imgRestaurantImage)
        val imgFav: ImageView =view.findViewById(R.id.imgFav)

        val llContent: LinearLayout =view.findViewById(R.id.llContent)
    }


    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int):
        AsyncTask<Void, Void, Boolean>() {

        //Mode 1->Check DB if the restaurant is favourite or not
        //Mode 2->Save the restaurant into DB as favourite
        //Mode 3->Remove the restaurant from favourite

        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"foodlovers-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1->{
                    //Check DB if the restaurant is favourite or not
                    val restaurant:RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.restaurant_Id)
                    db.close()
                    return restaurant!=null

                }
                2->{
                    //Save the restaurant into DB as favourite
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    return true
                }
                3->{
                    //Remove the restaurant from favourite
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    return true
                }
            }
            return false
        }

    }
}