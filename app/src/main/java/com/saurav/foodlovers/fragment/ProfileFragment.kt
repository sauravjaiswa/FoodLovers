package com.saurav.foodlovers.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.saurav.foodlovers.R

class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtMobile: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = activity!!.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        txtName=view.findViewById(R.id.txtName)
        txtMobile=view.findViewById(R.id.txtMobile)
        txtEmail=view.findViewById(R.id.txtEmail)
        txtAddress=view.findViewById(R.id.txtAddress)

        txtName.text=sharedPreferences.getString("user_name","")
        txtMobile.text=sharedPreferences.getString("user_mobile_number","")
        txtEmail.text=sharedPreferences.getString("user_email","")
        txtAddress.text=sharedPreferences.getString("user_address","")

        return view
    }

}
