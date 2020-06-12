package com.saurav.foodlovers.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.saurav.foodlovers.R
import com.saurav.foodlovers.adapter.CartRecyclerAdapter
import com.saurav.foodlovers.adapter.MenuRecyclerAdapter
import com.saurav.foodlovers.database.OrderEntity
import com.saurav.foodlovers.database.RestaurantDatabase
import com.saurav.foodlovers.model.Menu
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar
    lateinit var txtRestaurantName: TextView
    lateinit var recyclerCart: RecyclerView
    lateinit var btnOrderPlace: Button
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var cartRecyclerAdapter: CartRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var rlCart: RelativeLayout

    var restaurantId=0
    var restaurantName=""
    var billingAmt=0

    val orderList= arrayListOf<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        progressBar=findViewById(R.id.progressBar)
        progressLayout=findViewById(R.id.progressLayout)
        toolbar=findViewById(R.id.toolbar)
        txtRestaurantName=findViewById(R.id.txtRestaurantName)
        recyclerCart=findViewById(R.id.recyclerCart)
        btnOrderPlace=findViewById(R.id.btnOrderPlace)
        rlCart=findViewById(R.id.rlCart)

        val bundle=intent.getBundleExtra("data")
        restaurantId=bundle?.getInt("restaurantId",0) as Int
        restaurantName=bundle.getString("restaurantName","")

        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtRestaurantName.text="Ordering From: $restaurantName"

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        progressLayout.visibility=View.VISIBLE
        rlCart.visibility=View.GONE

        setUpCart()
        updateTotalCost()
    }

    private fun setUpCart(){
        val listOfItems=RetrieveItemsDBAsync(applicationContext).execute().get()

        //Converting the String to List of items using Gson
        for (item in listOfItems){
            orderList.addAll(Gson().fromJson(
                item.foodItems,
                Array<Menu>::class.java
            ).asList())
        }

        if(orderList.isEmpty())
        {
            rlCart.visibility=View.GONE
            progressLayout.visibility=View.VISIBLE
        }else{
            rlCart.visibility=View.VISIBLE
            progressLayout.visibility=View.GONE
        }

        cartRecyclerAdapter= CartRecyclerAdapter(this@CartActivity,orderList)
        layoutManager=LinearLayoutManager(this@CartActivity)
        recyclerCart.layoutManager=layoutManager
        recyclerCart.itemAnimator=DefaultItemAnimator()
        recyclerCart.adapter=cartRecyclerAdapter

    }

    private fun updateTotalCost(){
        //Displaying the total billing amount

        for (i in 0 until orderList.size){
            billingAmt+=orderList[i].cost_for_one.toInt()
        }

        val template="Place Order (Total: ₹ $billingAmt)"
        btnOrderPlace.text=template

        btnOrderPlace.setOnClickListener {
            progressLayout.visibility=View.VISIBLE
            rlCart.visibility=View.INVISIBLE
            placeOrder()
        }
    }

    private fun placeOrder(){
        val placeOrderUrl="http://13.235.250.119/v2/place_order/fetch_result/"
        val queue= Volley.newRequestQueue(this@CartActivity)

        val userId=sharedPreferences.getString("user_id","") as String
        val jsonParams=JSONObject()
        jsonParams.put("user_id",userId)
        jsonParams.put("restaurant_id",restaurantId.toString())
        jsonParams.put("total_cost",billingAmt.toString())

        val foodJSON=JSONArray()
        for (i in 0 until orderList.size){
            val foodId=JSONObject()
            foodId.put("food_item_id",orderList[i].dish_id)
            foodJSON.put(i,foodId)
        }
        jsonParams.put("food",foodJSON)

        val jsonObject=object : JsonObjectRequest(Method.POST, placeOrderUrl, jsonParams,
        Response.Listener {
            try {
                val data=it.getJSONObject("data")
                val success=data.getBoolean("success")

                Log.e("Success:",""+success)
                if(success){
                    //Clear the DB once order is placed
                    val clearCart=DeleteItemsDBAsync(applicationContext,restaurantId.toString()).execute().get()
                    MenuRecyclerAdapter.isCartEmpty=true
                    Log.e("Order History URL:","${placeOrderUrl+userId}")
                    //Toast.makeText(this@CartActivity,"Order Placed Successfully",Toast.LENGTH_LONG).show()

                    //Using dialog box instead of making a full activity
                    val dialog=Dialog(this@CartActivity,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                    dialog.setContentView(R.layout.order_placed)
                    dialog.show()
                    dialog.setCancelable(false)
                    val btnOk: Button=dialog.findViewById(R.id.btnOk)
                    btnOk.setOnClickListener {
                        dialog.dismiss()
//                        btnOk.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.green))
                        val intent= Intent(this@CartActivity,NavigationActivity::class.java)
                        startActivity(intent)
                        ActivityCompat.finishAffinity(this@CartActivity)
                    }
                }else{
                    rlCart.visibility=View.VISIBLE
                    Toast.makeText(this@CartActivity,"Some error occurred while ordering",Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                rlCart.visibility=View.VISIBLE
                Toast.makeText(this@CartActivity,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }, Response.ErrorListener {
                rlCart.visibility=View.VISIBLE
                Toast.makeText(this@CartActivity,it.message,Toast.LENGTH_SHORT).show()
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "YOUR_TOKEN_HERE"           //Unique token for everyone

                return headers
            }
        }
        queue.add(jsonObject)
    }

    override fun onSupportNavigateUp(): Boolean {
        val clearCart=DeleteItemsDBAsync(applicationContext,restaurantId.toString()).execute().get()
        MenuRecyclerAdapter.isCartEmpty=true
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val clearCart=DeleteItemsDBAsync(applicationContext,restaurantId.toString()).execute().get()
        MenuRecyclerAdapter.isCartEmpty=true
        super.onBackPressed()
    }

    //AsyncTask Class for extracting all items from database
    class RetrieveItemsDBAsync(context: Context): AsyncTask<Void, Void, List<OrderEntity>>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java, "foodlovers-db").build()

        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllItems()
        }
    }

    //AsyncTask Class for deleting all items from database
    class DeleteItemsDBAsync(context: Context, val restaurantId: String): AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java, "foodlovers-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteAllOrders(restaurantId)
            db.close()
            return true
        }
    }
}
