package com.saurav.foodlovers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saurav.foodlovers.R
import com.saurav.foodlovers.model.ParentModel

class HistoryParentRecyclerAdapter(val context: Context, val parentOrderList: List<ParentModel>): RecyclerView.Adapter<HistoryParentRecyclerAdapter.HistoryParentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryParentViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.parent_recycler,parent,false)

        return HistoryParentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parentOrderList.size
    }

    override fun onBindViewHolder(holder: HistoryParentViewHolder, position: Int) {
        val info=parentOrderList[position]

        holder.txtOrderId.text=info.order_id
        holder.txtRestaurantName.text=info.restaurant_name
        holder.txtDate.text=info.order_placed_at.split(" ")[0]
        holder.txtTotalCost.text="Total Cost: ₹ ${info.total_cost}"

        holder.layoutManager=LinearLayoutManager(context)
        holder.historyChildRecyclerAdapter=HistoryChildRecyclerAdapter(context,info.food_items)
        holder.recyclerChild.adapter=holder.historyChildRecyclerAdapter
        holder.recyclerChild.layoutManager=holder.layoutManager
    }

    class HistoryParentViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtOrderId: TextView =view.findViewById(R.id.txtOrderId)
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtDate: TextView=view.findViewById(R.id.txtDate)
        val recyclerChild: RecyclerView=view.findViewById(R.id.recyclerChild)
        val txtTotalCost: TextView=view.findViewById(R.id.txtTotalCost)

        lateinit var layoutManager: RecyclerView.LayoutManager
        lateinit var historyChildRecyclerAdapter: HistoryChildRecyclerAdapter
    }
}