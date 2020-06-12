package com.saurav.foodlovers.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.saurav.foodlovers.R
import com.saurav.foodlovers.adapter.FaqRecyclerAdapter
import com.saurav.foodlovers.model.Faq

class FaqFragment : Fragment() {

    lateinit var recyclerFaq: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FaqRecyclerAdapter

    var faqList= mutableListOf<Faq>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_faq, container, false)
        recyclerFaq=view.findViewById(R.id.recyclerFaq)
        layoutManager= LinearLayoutManager(activity)

        val questions= mutableListOf<String>(
            "What kind of restaurants are listed on Food Lovers?",
            "How much do you charge for delivery fee?",
            "Can I order from more than one restaurant at the same time?",
            "Can I change the delivery address after placing the order?",
            "Where do the ratings and reviews come from?",
            "How long does it take for a delivery?"
        )

        val answers= mutableListOf<String>(
            "We have more than 20+ top restaurants listed. Our restaurants are throughout your city and offer multiple cuisine options including Thai, Chinese, Indian, Burgers, Pizza, Afghani, Vietnamese, Japanese, Italian, Mexican and so on. You name it, we have it!",
            "We charge a fixed delivery fee of ₹ 10 (up to 7 km) and ₹ 2 per additional km over 7 kms.",
            "Sorry, you cannot place items from different restaurant in the same cart. But you can simultaneously place orders from more than one restaurants.",
            "Any major change in delivery address is not possible after you have placed an order with us. However, slight modifications like changing the flat number, street name, landmark etc. are allowed. Please get in touch with our team for that. ",
            "All ratings come from the user's experience, user can only leave a rating after completing a purchase with that merchant restaurant.",
            "Our normal delivery time is between 45 minutes and 1 hour, however certain situations such as traffic, weather, and restaurant preparation time require extra time. Please know that we are always working hard to get your food delivered as quickly as possible. We appreciate your patience. Placing orders in advance is appreciated."
        )

        for (i in 0 until questions.size){
            faqList.add(Faq(questions[i],answers[i]))
        }

        setUpFaqFragment()

        return view
    }

    private fun setUpFaqFragment(){
        recyclerAdapter= FaqRecyclerAdapter(activity as Context,faqList)
        recyclerFaq.adapter=recyclerAdapter
        recyclerFaq.layoutManager=layoutManager
    }

}
