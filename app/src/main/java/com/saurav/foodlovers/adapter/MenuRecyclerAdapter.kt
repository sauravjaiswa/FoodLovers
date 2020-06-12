package com.saurav.foodlovers.adapter

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.saurav.foodlovers.R
import com.saurav.foodlovers.model.Menu

class MenuRecyclerAdapter(val context: Context,val menuList:ArrayList<Menu>,val dishList:ArrayList<String>,val listener: OnItemClickListener): RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    companion object{
        var isCartEmpty=true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.menu_single_row,parent,false)

        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    interface OnItemClickListener{
        fun onAddItemClick(menu:Menu)
        fun onRemoveItemClick(menu:Menu)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        val info=menuList[position]

        holder.txtItemNo.text=(position+1).toString()
        holder.txtDishName.text=info.name
        holder.txtDishPrice.text="\u20B9 ${info.cost_for_one}"

        holder.imgAdd.visibility=View.VISIBLE
        holder.imgRemove.visibility=View.GONE

//        Log.e("Menu list",":"+dishList.size)
        if (dishList.contains(info.dish_id)){
            holder.imgAdd.visibility=View.GONE
            holder.imgRemove.visibility=View.VISIBLE
        }else{
            holder.imgRemove.visibility=View.GONE
            holder.imgAdd.visibility=View.VISIBLE
        }

        holder.imgAdd.setOnClickListener {
            holder.imgAdd.visibility=View.GONE
            holder.imgRemove.visibility=View.VISIBLE
            listener.onAddItemClick(info)
        }

        holder.imgRemove.setOnClickListener {
            holder.imgRemove.visibility=View.GONE
            holder.imgAdd.visibility=View.VISIBLE
            listener.onRemoveItemClick(info)
        }

    }

    class MenuViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtItemNo: TextView =view.findViewById(R.id.txtItemNo)
        val txtDishName: TextView =view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView =view.findViewById(R.id.txtDishPrice)
        val imgAdd: ImageButton =view.findViewById(R.id.imgAdd)
        val imgRemove: ImageButton =view.findViewById(R.id.imgRemove)

        val llContent: LinearLayout =view.findViewById(R.id.llContent)
    }
}