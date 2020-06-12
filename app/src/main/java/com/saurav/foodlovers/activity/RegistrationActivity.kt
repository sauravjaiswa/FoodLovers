package com.saurav.foodlovers.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.saurav.foodlovers.R
import com.saurav.foodlovers.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmailAddress: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etName=findViewById(R.id.etName)
        etEmailAddress=findViewById(R.id.etEmailAddress)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etDeliveryAddress=findViewById(R.id.etDeliveryAddress)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        btnRegister=findViewById(R.id.btnRegister)
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility=View.GONE

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"


        btnRegister.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser()
    {
        //getting email and password from edit texts
        var name=etName.text.toString()
        var email=etEmailAddress.text.toString()
        var mobile=etMobileNumber.text.toString()
        var address=etDeliveryAddress.text.toString()
        var password=etPassword.text.toString()
        var cpassword=etConfirmPassword.text.toString()

        //checking if no fields are empty
        if (TextUtils.isEmpty(name)) {
            etName.error="Name Required!"
            return
        }else if (name.length < 3) {
            etName.error="Name should be of minimum 3 characters"
            return
        } else {
            etName.error=null
        }
        if (TextUtils.isEmpty(email)) {
            etEmailAddress.error="Email Address Required!"
            return
        } else {
            etEmailAddress.error=null
        }
        if (TextUtils.isEmpty(mobile)) {
            etMobileNumber.error="Mobile Number Required!"
            return
        }else if (mobile.length < 10) {
            etMobileNumber.error="Invalid Mobile Number!"
            return
        } else {
            etMobileNumber.error=null
        }
        if (TextUtils.isEmpty(address)) {
            etDeliveryAddress.error="Delivery Address Required!"
            return
        } else {
            etName.error=null
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.error="Password Required!"
            return
        }else if (password.length < 4) {
            etPassword.error="Password should be of minimum 4 characters"
            return
        } else {
            etPassword.error=null
        }
        if (TextUtils.isEmpty(cpassword)) {
            etConfirmPassword.error="Confirm your Password!"
            return
        }else if (password != cpassword) {
            etConfirmPassword.error="Incorrect Password!"
            return
        } else {
            etConfirmPassword.error=null
        }

        val queue= Volley.newRequestQueue(this@RegistrationActivity)
        val registerURL="http://13.235.250.119/v2/register/fetch_result"

        val jsonParams= JSONObject()     //To pass in the JSONObjectRequest
        jsonParams.put("name",name)
        jsonParams.put("mobile_number",mobile)
        jsonParams.put("password",password)
        jsonParams.put("address",address)
        jsonParams.put("email",email)


        if(ConnectionManager().checkConnectivity(this@RegistrationActivity)){

            val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,registerURL,jsonParams,
                Response.Listener {

                    try {
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")
                        if(success){
                            val response=data.getJSONObject("data")
                            sharedPreferences.edit()
                                .putString("user_id", response.getString("user_id")).apply()
                            sharedPreferences.edit()
                                .putString("user_name", response.getString("name")).apply()
                            sharedPreferences.edit()
                                .putString("user_email", response.getString("email")).apply()
                            sharedPreferences.edit()
                                .putString("user_mobile_number",response.getString("mobile_number")).apply()
                            sharedPreferences.edit()
                                .putString("user_address", response.getString("address")).apply()

                            progressLayout.visibility=View.VISIBLE
                            val intent=Intent(this@RegistrationActivity,NavigationActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            progressLayout.visibility=View.GONE
                            val errorMessage=data.getString("errorMessage")
                            Log.e("Error Message:",errorMessage)
                            Toast.makeText(this@RegistrationActivity,"Some unexpected error occurred!!!!!\t $errorMessage",Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:JSONException){
                        progressLayout.visibility=View.GONE
                        Toast.makeText(this@RegistrationActivity,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
                        Log.e("Error::",""+e.printStackTrace())
                    }

                }, Response.ErrorListener {

                    if(application!=null){
                        progressLayout.visibility=View.GONE
                        println("Volley Error ${it.message}")
                        Toast.makeText(this@RegistrationActivity,"Volley Error ${it.message}",Toast.LENGTH_SHORT).show()
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
            alertDialog()
        }
    }

    private fun alertDialog(){
        val dialog= AlertDialog.Builder(this@RegistrationActivity)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is Not Found")
        dialog.setPositiveButton("Open Settings"){text,listener->
            //Open Settings

            val openSettings= Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(openSettings)
            finish()
        }
        dialog.setNegativeButton("Exit"){text,listener->
            //Exit the app
            ActivityCompat.finishAffinity(this@RegistrationActivity)
        }
        dialog.create()
        dialog.show()
    }

}
