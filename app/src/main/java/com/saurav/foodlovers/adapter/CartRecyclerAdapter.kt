package com.saurav.foodlovers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saurav.foodlovers.R
import com.saurav.foodlovers.model.Menu

class CartRecyclerAdapter(val context: Context, val itemList:ArrayList<Menu>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row,parent,false)

        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val itemDetails=itemList[position]

        holder.txtDishName.text=itemDetails.name
        val price="₹ ${itemDetails.cost_for_one}"
        holder.txtDishPrice.text=price

    }

    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtDishName:TextView=view.findViewById(R.id.txtDishName)
        val txtDishPrice:TextView=view.findViewById(R.id.txtDishPrice)
    }
}