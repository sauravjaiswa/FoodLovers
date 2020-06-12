package com.saurav.foodlovers.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.saurav.foodlovers.R
import com.saurav.foodlovers.activity.CartActivity
import com.saurav.foodlovers.adapter.MenuRecyclerAdapter
import com.saurav.foodlovers.database.OrderEntity
import com.saurav.foodlovers.database.RestaurantDatabase
import com.saurav.foodlovers.model.DrawerLocker
import com.saurav.foodlovers.model.Menu
import com.saurav.foodlovers.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_navigation.*
import org.json.JSONException

class RestaurantFragment : Fragment() {

    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var imgSetFav: ImageView
    lateinit var btnProceed: Button
    var restaurantId:String?="-999"
    var restaurantName:String?="Restaurant Details"
    var fav:Boolean?=false

    lateinit var menuRecyclerAdapter: MenuRecyclerAdapter

    val menuInfoList= arrayListOf<Menu>()
    val orderList= arrayListOf<Menu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_restaurant, container, false)

        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
//        toolbar=findViewById(R.id.toolbar)
        imgSetFav=view.findViewById(R.id.imgSetFav)
        btnProceed=view.findViewById(R.id.btnProdeed)

        btnProceed.visibility=View.INVISIBLE
        restaurantId=arguments?.getString("restaurant_id","")
        restaurantName=arguments?.getString("restaurant_name","")
        fav= arguments?.getBoolean("favorite",false)

        (activity as DrawerLocker).setDrawerEnabled(false)
        setHasOptionsMenu(true)

        toolbar?.title=restaurantName

        Log.e("Check","Restaurant $restaurantId  $restaurantName  $fav")

        if(fav==true){
            imgSetFav.setImageResource(R.drawable.ic_fav_filled)
        }else{
            imgSetFav.setImageResource(R.drawable.ic_imgfavorite)
        }

        progressLayout.visibility=View.VISIBLE

        setUpMenu(view)

        btnProceed.setOnClickListener {
            proceedToCart(view)
        }


        return view
    }


    private fun setUpMenu(view:View){
        recyclerMenu=view.findViewById(R.id.recyclerMenu)
        layoutManager= LinearLayoutManager(context)

        val queue= Volley.newRequestQueue(context)

        val menuUrl="http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest=object: JsonObjectRequest(Request.Method.GET,menuUrl,null, Response.Listener {

                try {
                    progressLayout.visibility= View.INVISIBLE
                    val data=it.getJSONObject("data")
                    val success=data.getBoolean("success")

                    if(success){
                        val menuData=data.getJSONArray("data")

                        for(i in 0 until menuData.length()){
                            val menuJsonObject=menuData.getJSONObject(i)
                            val menuObject= Menu(
                                menuJsonObject.getString("id"),
                                menuJsonObject.getString("name"),
                                menuJsonObject.getString("cost_for_one")
                            )

                            menuInfoList.add(menuObject)
                            val dishList= arrayListOf<String>()

                            menuRecyclerAdapter= MenuRecyclerAdapter(activity as Context,menuInfoList,dishList,
                                object : MenuRecyclerAdapter.OnItemClickListener {
                                    override fun onAddItemClick(menu: Menu) {
                                        dishList.add(menu.dish_id)
                                        orderList.add(menu)
                                        if (orderList.size > 0) {
//                                            val dpRatio:Float= context.resources.displayMetrics.density
//                                            val pixelForDp:Int=56*dpRatio.toInt()

                                            val displayMetrics: DisplayMetrics=context!!.resources.displayMetrics
                                            val dpToPixel=(56*(displayMetrics.xdpi/DisplayMetrics.DENSITY_DEFAULT)).toInt()

                                            val paramsOfRecycler:RelativeLayout.LayoutParams=recyclerMenu.layoutParams as RelativeLayout.LayoutParams
                                            paramsOfRecycler.setMargins(0,0,0,dpToPixel)
                                            btnProceed.visibility = View.VISIBLE
                                            MenuRecyclerAdapter.isCartEmpty = false
                                        }
                                    }

                                    override fun onRemoveItemClick(menu: Menu) {
                                        dishList.remove(menu.dish_id)
                                        orderList.remove(menu)
                                        if (orderList.isEmpty()) {
                                            val paramsOfRecycler=recyclerMenu.layoutParams as RelativeLayout.LayoutParams
                                            paramsOfRecycler.setMargins(0,0,0,0)
                                            btnProceed.visibility = View.INVISIBLE
                                            MenuRecyclerAdapter.isCartEmpty = true
                                        }
                                    }
                                })

                            recyclerMenu.adapter=menuRecyclerAdapter
                            recyclerMenu.itemAnimator = DefaultItemAnimator()
                            recyclerMenu.layoutManager=layoutManager


                        }
                    }else{
                        Toast.makeText(context,"Some error has occurred!!", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(context,"Some unexpected error occurred!!", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {

                if (activity!=null){
                    Toast.makeText(context,"Volley error occurred!!!", Toast.LENGTH_SHORT).show()
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
//                finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                //Exit the app
                ActivityCompat.finishAffinity(activity!!)
            }
            dialog.create()
            dialog.show()

        }
    }

    private fun proceedToCart(view: View){
        val gson= Gson()
        val foodItems=gson.toJson(orderList)

        val async=ItemsInCart(activity as Context, restaurantId.toString(), foodItems, 1).execute()
        val result=async.get()

        if(result){
            val data=Bundle()
            data.putInt("restaurantId",restaurantId!!.toInt())
            data.putString("restaurantName",restaurantName)

            val intent=Intent(context, CartActivity::class.java)
            intent.putExtra("data",data)
            startActivity(intent)
        }else{
            Toast.makeText(context,"Some unexpected error occurred!!",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            activity?.drawerLayout?.closeDrawer(GravityCompat.START)
            activity?.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as DrawerLocker).setDrawerEnabled(true)
    }

    class ItemsInCart(context: Context,val restaurantId: String, val foodItems: String, val mode: Int): AsyncTask<Void, Void, Boolean>(){

        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"foodlovers-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    db.orderDao().insertOrder(OrderEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }
                2->{
                    db.orderDao().deleteOrder(OrderEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }
    }



}
