package com.saurav.foodlovers.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.saurav.foodlovers.R
import com.saurav.foodlovers.fragment.*
import com.saurav.foodlovers.model.DrawerLocker
import java.lang.Compiler.enable




class NavigationActivity : AppCompatActivity(), DrawerLocker {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var txtUserName: TextView
    lateinit var txtUserMobile: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)

        val header:View=navigationView.getHeaderView(0)

        txtUserName=header.findViewById(R.id.txtUserName)
        txtUserMobile=header.findViewById(R.id.txtUserMobile)

        setUpToolbar()

        openDashboard()

        Log.e("User id:",sharedPreferences.getString("user_id",""))
        txtUserName.text=sharedPreferences.getString("user_name","")
        txtUserMobile.text=sharedPreferences.getString("user_mobile_number","")

        actionBarDrawerToggle=object: ActionBarDrawerToggle(
            this@NavigationActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        ) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                val pendingRunnable= Runnable {
                    val inputMethodManager=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
                }
                Handler().postDelayed(pendingRunnable,50)
            }
        }
        drawerLayout.addDrawerListener(actionBarDrawerToggle)       //Makes hamburger icon functional
        actionBarDrawerToggle.syncState()       //When drawer is outside we press hamburger icon it changes to back arrow and vice versa

        navigationView.setNavigationItemSelectedListener {          //To add click listener to navigation menu

            if(previousMenuItem!=null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it

            when(it.itemId){
                R.id.itemHome ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            HomeFragment()
                        )
                        .commit()

                    supportActionBar?.title="All Restaurants"

                    drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )
                        .commit()

                    supportActionBar?.title="My Profile"

                    drawerLayout.closeDrawers()
                }
                R.id.favorite ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavoritesFragment()
                        )
                        .commit()

                    supportActionBar?.title="Favorite Restaurants"

                    drawerLayout.closeDrawers()
                }
                R.id.history ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            HistoryFragment()
                        )
                        .commit()

                    supportActionBar?.title="My Previous Orders"

                    drawerLayout.closeDrawers()
                }
                R.id.faq ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqFragment()
                        )
                        .commit()

                    supportActionBar?.title="Frequently Asked Questions"

                    drawerLayout.closeDrawers()
                }
                R.id.logout ->{
                    drawerLayout.closeDrawers()

                    val dialog= AlertDialog.Builder(this@NavigationActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("Yes"){text,listener->
                        //Open Settings

                        val intent = Intent(this@NavigationActivity,LoginActivity::class.java)
                        sharedPreferences.edit().clear().apply()
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No"){text,listener->
                        //Does Nothing

                    }
                    dialog.create()
                    dialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id=item.itemId
        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openDashboard(){
        val fragment= HomeFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.itemHome)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frame)

        when(frag){
            !is HomeFragment ->openDashboard()
            else->super.onBackPressed()
        }
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled=enabled

    }
}
