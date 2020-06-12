package com.saurav.foodlovers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saurav.foodlovers.R
import com.saurav.foodlovers.model.Faq

class FaqRecyclerAdapter(val context: Context,val faqList: MutableList<Faq>): RecyclerView.Adapter<FaqRecyclerAdapter.FaqViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.faq_single_row,parent,false)
        return FaqViewHolder(view)
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val data=faqList[position]

        holder.txtQuestion.text=data.question
        holder.txtAnswer.text=data.answer
    }

    class FaqViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtQuestion: TextView =view.findViewById(R.id.txtQuestion)
        val txtAnswer: TextView =view.findViewById(R.id.txtAnswer)
    }

}