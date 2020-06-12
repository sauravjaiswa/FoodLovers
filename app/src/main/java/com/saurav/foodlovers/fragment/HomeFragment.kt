package com.saurav.foodlovers.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.saurav.foodlovers.R
import com.saurav.foodlovers.adapter.RestaurantRecyclerAdapter
import com.saurav.foodlovers.model.Restaurant
import com.saurav.foodlovers.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_navigation.*
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerRestaurants: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var restaurantRecyclerAdapter: RestaurantRecyclerAdapter

    val restaurantInfoList= arrayListOf<Restaurant>()

    var ratingComparator= Comparator<Restaurant> { restaurant1, restaurant2 ->

        if(restaurant1.rating.compareTo(restaurant2.rating,true)==0){
            restaurant1.name.compareTo(restaurant2.name,true)
        }else{
            restaurant1.rating.compareTo(restaurant2.rating,true)
        }
    }

    var costComparator= Comparator<Restaurant> { restaurant1, restaurant2 ->
        if(restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one,true)==0){
            restaurant1.rating.compareTo(restaurant2.rating,true)
        }else{
            restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one,true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_home, container, false)

        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        setHasOptionsMenu(true)

        progressLayout.visibility=View.VISIBLE
        setUpHomeFragment(view)

        return view
    }

    private fun setUpHomeFragment(view: View){
        recyclerRestaurants=view.findViewById(R.id.recyclerRestaurants)
        layoutManager=LinearLayoutManager(activity)
        val queue= Volley.newRequestQueue(activity as Context)

        val restaurantUrl="http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest=object: JsonObjectRequest(Request.Method.GET,restaurantUrl,null, Response.Listener {

                try {
                    progressLayout.visibility=View.GONE
                    val data=it.getJSONObject("data")
                    val success=data.getBoolean("success")

                    if(success){
                        val restaurant=data.getJSONArray("data")

                        for(i in 0 until restaurant.length()){
                            val restaurantJsonObject=restaurant.getJSONObject(i)
                            val restaurantObject=Restaurant(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )

                            restaurantInfoList.add(restaurantObject)

                            restaurantRecyclerAdapter= RestaurantRecyclerAdapter(activity as Context,restaurantInfoList)
//                            restaurantRecyclerAdapter.notifyDataSetChanged()
                            recyclerRestaurants.adapter=restaurantRecyclerAdapter
                            recyclerRestaurants.layoutManager=layoutManager

                        }
                    }else{
                        Toast.makeText(activity as Context,"Some error has occurred!!", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(activity as Context,"Some unexpected error occurred!!", Toast.LENGTH_SHORT).show()
                    println(e.printStackTrace())
                }

            }, Response.ErrorListener {

                //Here we will handle the error
//                println("Error is $it")
                if (activity!=null){
                    Toast.makeText(activity as Context,"Volley error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }){

                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="YOUR_TOKEN_HERE"           //Unique token for everyone

                    return headers      //Returning HashMap instead of MutableMap since both are almost same
                }
            }

            queue.add(jsonObjectRequest)

        }else{

            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is Not Found")
            dialog.setPositiveButton("Open Settings"){text,listener->
                //Open Settings

                val openSettings= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(openSettings)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                //Exit the app
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }
    }

    private fun createSortMenu(){
        val dialogSort=AlertDialog.Builder(activity as Context)
        dialogSort.setTitle("Sort By-")
        val options= arrayOf<String>("Rating","Cost (Low to High)","Cost (High to Low)")
        var selected=-1

        dialogSort.setSingleChoiceItems(options,selected){dialog, which ->
            when(which){
                0->sortByRating()
                1->costLowToHigh()
                2->costHighToLow()
            }
            restaurantRecyclerAdapter.notifyDataSetChanged()     //To arrange the recycler items accordingly
            dialog.dismiss()
        }
        dialogSort.create()
        dialogSort.show()
    }

    private fun sortByRating(){
        Collections.sort(restaurantInfoList,ratingComparator)
        restaurantInfoList.reverse()      //To print in descending order
    }

    private fun costLowToHigh(){
        Collections.sort(restaurantInfoList,costComparator)
    }

    private fun costHighToLow(){
        Collections.sort(restaurantInfoList,costComparator)
        restaurantInfoList.reverse()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_restaurant_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId
        if(id==R.id.action_sort){
            createSortMenu()
        }
        return super.onOptionsItemSelected(item)
    }
}
