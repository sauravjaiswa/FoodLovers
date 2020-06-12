package com.saurav.foodlovers.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.saurav.foodlovers.R
import com.saurav.foodlovers.adapter.HistoryParentRecyclerAdapter
import com.saurav.foodlovers.model.ChildModel
import com.saurav.foodlovers.model.ParentModel
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerParent: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var historyParentRecyclerAdapter: HistoryParentRecyclerAdapter
    lateinit var txtNoOrdersLayout: RelativeLayout
    lateinit var progressLayout: RelativeLayout

    var historyList= arrayListOf<ParentModel>()
    var itemsList= arrayListOf<ChildModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_history, container, false)

        recyclerParent=view.findViewById(R.id.recyclerParent)
        txtNoOrdersLayout=view.findViewById(R.id.txtNoOrdersLayout)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE
        sharedPreferences = activity!!.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        layoutManager=LinearLayoutManager(activity)

        fetchHistory()

        return view
    }

    private fun fetchHistory(){

        val queue= Volley.newRequestQueue(activity as Context)
        val user_id=sharedPreferences.getString("user_id","")

        val historyUrl="http://13.235.250.119/v2/orders/fetch_result/$user_id"

        val jsonObjectRequest=object: JsonObjectRequest(Request.Method.GET,historyUrl,null, Response.Listener {

            try {
                progressLayout.visibility=View.GONE
                val data=it.getJSONObject("data")
                val success=data.getBoolean("success")

                if(success){
                    val history=data.getJSONArray("data")

                    for(i in 0 until history.length()){

                        val historyJsonObject=history.getJSONObject(i)

                        val foodItemsObject=historyJsonObject.getJSONArray("food_items")
                        for (j in 0 until foodItemsObject.length())
                        {
                            val foodJsonObject=foodItemsObject.getJSONObject(j)
                            val foodObject=ChildModel(
                                foodJsonObject.getString("name"),
                                foodJsonObject.getString("cost")
                            )
                            itemsList.add(foodObject)
                        }

                        val historyObject=ParentModel(
                            historyJsonObject.getString("order_id"),
                            historyJsonObject.getString("restaurant_name"),
                            historyJsonObject.getString("total_cost"),
                            historyJsonObject.getString("order_placed_at"),
                            itemsList
                        )

                        historyList.add(historyObject)

                        if (historyList.size!=0)
                            txtNoOrdersLayout.visibility=View.GONE
                        else
                            txtNoOrdersLayout.visibility=View.VISIBLE

                        historyParentRecyclerAdapter= HistoryParentRecyclerAdapter(activity as Context,historyList)
                        recyclerParent.adapter=historyParentRecyclerAdapter
                        recyclerParent.itemAnimator=DefaultItemAnimator()
                        recyclerParent.layoutManager=layoutManager

//                        itemsList.clear()
                        itemsList= arrayListOf<ChildModel>()
                    }
                }else{
                    progressLayout.visibility=View.GONE
                    Toast.makeText(activity as Context,"Some error has occurred!!", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                progressLayout.visibility=View.GONE
                Toast.makeText(activity as Context,"Some unexpected error occurred!!", Toast.LENGTH_SHORT).show()
                println(e.printStackTrace())
            }

        }, Response.ErrorListener {

            //Here we will handle the error
            progressLayout.visibility=View.GONE
            println("Error is $it")
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

    }

}
