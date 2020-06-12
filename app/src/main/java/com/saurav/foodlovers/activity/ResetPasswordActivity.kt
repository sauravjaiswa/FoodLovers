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
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
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

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etOtp: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var mobile: String
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etOtp=findViewById(R.id.etOTP)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        btnSubmit=findViewById(R.id.btnSubmit)
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility= View.GONE

        if (intent!=null){
            mobile=intent.getStringExtra("mobile_number")
        }

        btnSubmit.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword(){
        val otp=etOtp.text.toString()
        val password=etPassword.text.toString()
        val cPassword=etConfirmPassword.text.toString()

        //checking if no fields are empty
        if (TextUtils.isEmpty(otp)) {
            etOtp.error="OTP Required!"
            return
        }else if (otp.length != 4) {
            etOtp.error="Invalid OTP!"
            return
        } else {
            etOtp.error=null
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
        if (TextUtils.isEmpty(cPassword)) {
            etConfirmPassword.error="Confirm your Password!"
            return
        }else if (password != cPassword) {
            etConfirmPassword.error="Incorrect Confirmation Password!"
            return
        } else {
            etConfirmPassword.error=null
        }

        val queue= Volley.newRequestQueue(this@ResetPasswordActivity)
        val resetURL="http://13.235.250.119/v2/reset_password/fetch_result"

        val jsonParams= JSONObject()     //To pass in the JSONObjectRequest
        jsonParams.put("mobile_number",mobile)
        jsonParams.put("password",password)
        jsonParams.put("otp",otp)


        if(ConnectionManager().checkConnectivity(this@ResetPasswordActivity)){

            val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,resetURL,jsonParams,
                Response.Listener {

                    try {
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")
                        val succesMessage=data.getString("successMessage")
                        if(success){

                            Toast.makeText(this@ResetPasswordActivity,succesMessage,
                                Toast.LENGTH_SHORT).show()
                            Log.e("Message",succesMessage)

                            progressLayout.visibility=View.VISIBLE
                            val intent= Intent(this@ResetPasswordActivity,LoginActivity::class.java)
                            sharedPreferences.edit().clear().apply()
                            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
//                            finish()

                        }else{
                            progressLayout.visibility=View.GONE
                            val errorMessage=data.getString("errorMessage")
                            Toast.makeText(this@ResetPasswordActivity,"Invalid Credentials\t $errorMessage",
                                Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){
                        progressLayout.visibility=View.GONE
                        Toast.makeText(this@ResetPasswordActivity,"Some unexpected error occurred",
                            Toast.LENGTH_SHORT).show()
                        Log.e("Error::",""+e.printStackTrace())
                    }

                }, Response.ErrorListener {

                    if(application!=null){
                        progressLayout.visibility=View.GONE
                        Toast.makeText(this@ResetPasswordActivity,"Volley Error ${it.message}",
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
            alertDialog()
        }
    }

    private fun alertDialog(){
        val dialog= AlertDialog.Builder(this@ResetPasswordActivity)
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
            ActivityCompat.finishAffinity(this@ResetPasswordActivity)
        }
        dialog.create()
        dialog.show()
    }
}
