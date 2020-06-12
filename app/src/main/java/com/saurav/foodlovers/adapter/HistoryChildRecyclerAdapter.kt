package com.saurav.foodlovers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saurav.foodlovers.R
import com.saurav.foodlovers.model.ChildModel
import kotlinx.android.synthetic.main.child_recycler.view.*

class HistoryChildRecyclerAdapter(val context: Context, val foodItemsList: List<ChildModel>): RecyclerView.Adapter<HistoryChildRecyclerAdapter.HistoryChildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryChildViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.child_recycler,parent,false)

        return HistoryChildViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodItemsList.size
    }

    override fun onBindViewHolder(holder: HistoryChildViewHolder, position: Int) {
        val info=foodItemsList[position]

        holder.txtDishName.text=info.name
        holder.txtDishPrice.text="₹ ${info.cost}"
    }

    class HistoryChildViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtDishName: TextView=view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView=view.findViewById(R.id.txtDishPrice)
    }
}