package com.saurav.foodlovers.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.saurav.foodlovers.R
import com.saurav.foodlovers.util.ConnectionManager
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobileNumber=findViewById(R.id.etMobileNumber)
        etEmail=findViewById(R.id.etEmailAddress)
        btnNext=findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            sendRequest()
        }
    }

    private fun sendRequest(){
        var mobile=etMobileNumber.text.toString()
        var email=etEmail.text.toString()

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
        if (TextUtils.isEmpty(email)) {
            etEmail.error="Email Required!"
            return
        }else {
            etEmail.error=null
        }

        val queue= Volley.newRequestQueue(this@ForgotPasswordActivity)
        val requestURL="http://13.235.250.119/v2/forgot_password/fetch_result"

        val jsonParams= JSONObject()     //To pass in the JSONObjectRequest
        jsonParams.put("mobile_number",mobile)
        jsonParams.put("email",email)


        if(ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)){

            val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,requestURL,jsonParams,
                Response.Listener {

                    try {
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")
                        val firstTry=data.getBoolean("first_try")
                        if(success && firstTry){

                            Toast.makeText(this@ForgotPasswordActivity,"OTP sent to registered Email Id",Toast.LENGTH_SHORT).show()
                            Log.e("First_Try:","$firstTry")

                            val intent= Intent(this@ForgotPasswordActivity,ResetPasswordActivity::class.java)
                            intent.putExtra("mobile_number",mobile)
                            startActivity(intent)

                        }else if(success && !firstTry){

                            Toast.makeText(this@ForgotPasswordActivity,"OTP is already sent to registered Email Id previously",Toast.LENGTH_SHORT).show()
                            Log.e("First_Try:","$firstTry")

                            val intent= Intent(this@ForgotPasswordActivity,ResetPasswordActivity::class.java)
                            intent.putExtra("mobile_number",mobile)
                            startActivity(intent)

                        }else{
                            val errorMessage=data.getString("errorMessage")
                            Toast.makeText(this@ForgotPasswordActivity,"Invalid Credentials\t $errorMessage",
                                Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){
                        Toast.makeText(this@ForgotPasswordActivity,"Some unexpected error occurred",
                            Toast.LENGTH_SHORT).show()
                        Log.e("Error::",""+e.printStackTrace())
                    }

                }, Response.ErrorListener {

                    if(application!=null){
                        Toast.makeText(this@ForgotPasswordActivity,"Volley Error ${it.message}",
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
            val dialog= AlertDialog.Builder(this@ForgotPasswordActivity)
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
                ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
            }
            dialog.create()
            dialog.show()
        }
    }
}
