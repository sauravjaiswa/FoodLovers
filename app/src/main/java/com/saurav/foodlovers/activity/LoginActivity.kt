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
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.saurav.foodlovers.R
import com.saurav.foodlovers.util.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber:EditText
    lateinit var etPassword:EditText
    lateinit var btnLogin:Button
    lateinit var txtForgotPassword:TextView
    lateinit var txtRegister:TextView
    lateinit var progressLayout: RelativeLayout

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etMobileNumber=findViewById(R.id.etMobileNumber)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogin)
        txtForgotPassword=findViewById(R.id.txtForgotPassword)
        txtRegister=findViewById(R.id.txtRegister)
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility= View.GONE

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        if(sharedPreferences.contains("user_id") && sharedPreferences.contains("user_email")){
            val intent = Intent(this@LoginActivity,NavigationActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtRegister.setOnClickListener {
            val intent= Intent(this@LoginActivity,RegistrationActivity::class.java)
            startActivity(intent)
        }

        txtForgotPassword.setOnClickListener {
            val intent= Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {

            loginUser()

        }
    }

    private fun loginUser(){
        //getting mobile and password from edit texts
        var mobile=etMobileNumber.text.toString()
        var password=etPassword.text.toString()

        //checking if no fields are empty
        if (TextUtils.isEmpty(mobile)) {
            etMobileNumber.error="Mobile Number Required!"
            return
        }else if (mobile.length < 10) {
            etMobileNumber.error="Invalid Mobile Number!"
            return
        } else {
            etMobileNumber.error=null
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

        val queue= Volley.newRequestQueue(this@LoginActivity)
        val loginURL="http://13.235.250.119/v2/login/fetch_result"

        val jsonParams= JSONObject()     //To pass in the JSONObjectRequest
        jsonParams.put("mobile_number",mobile)
        jsonParams.put("password",password)


        if(ConnectionManager().checkConnectivity(this@LoginActivity)){

            val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,loginURL,jsonParams,
                Response.Listener {

                    try {
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")
                        if(success){
                            val response=data.getJSONObject("data")
                            sharedPreferences.edit().putString("user_id", response.getString("user_id")).apply()
                            sharedPreferences.edit().putString("user_name", response.getString("name")).apply()
                            sharedPreferences.edit().putString("user_email", response.getString("email")).apply()
                            sharedPreferences.edit().putString("user_mobile_number",response.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("user_address", response.getString("address")).apply()

                            Log.e("Name:",response.getString("name"))

                            progressLayout.visibility=View.VISIBLE
                            val intent=Intent(this@LoginActivity,NavigationActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            progressLayout.visibility=View.GONE
                            val errorMessage=data.getString("errorMessage")
                            Toast.makeText(this@LoginActivity,"Invalid Credentials\t $errorMessage",
                                Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){
                        progressLayout.visibility=View.GONE
                        Toast.makeText(this@LoginActivity,"Some unexpected error occurred",
                            Toast.LENGTH_SHORT).show()
                        Log.e("Error::",""+e.printStackTrace())
                    }

                }, Response.ErrorListener {

                    if(application!=null){
                        progressLayout.visibility=View.GONE
                        Toast.makeText(this@LoginActivity,"Volley Error ${it.message}",
                            Toast.LENGTH_SHORT).show()
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
            val dialog= AlertDialog.Builder(this@LoginActivity)
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
                ActivityCompat.finishAffinity(this@LoginActivity)
            }
            dialog.create()
            dialog.show()
        }
    }
}
