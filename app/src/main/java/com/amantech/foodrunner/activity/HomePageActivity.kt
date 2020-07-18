package com.amantech.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.amantech.foodrunner.R
import com.amantech.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_profile.*

class HomePageActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var txtHeaderName: TextView
    lateinit var txtHeaderMobile: TextView
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_profile_details),
            Context.MODE_PRIVATE
        )

        setContentView(R.layout.activity_home_page)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        val headerView = navigationView.getHeaderView(0)
        txtHeaderName = headerView.findViewById(R.id.txtHeaderName)
        txtHeaderMobile = headerView.findViewById(R.id.txtHeaderMobile)

        val name = sharedPreferences.getString("Name","DEFAULT_NAME")
        val mobile = "+91-" + sharedPreferences.getString("MobNo","DEFAULT_MOB")

        txtHeaderName.text = name
        txtHeaderMobile.text = mobile

        setUpToolbar()

        openHome()

        drawerLayout.closeDrawers()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomePageActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.favorite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavoritesFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favorite Restaurants"
                    drawerLayout.closeDrawers()
                }

                R.id.history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()
                }

                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FAQFragment()
                        )

                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }

                R.id.logout -> {

                    val dialog = AlertDialog.Builder(this@HomePageActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to exit?")

                    dialog.setPositiveButton("YES") { text, listener ->
                        sharedPreferences.edit().clear().apply()
                        finishAffinity()
                    }
                    dialog.setNegativeButton("NO") { text, listener ->
                        //close dialog and open home
                        openHome()
                        drawerLayout.closeDrawers()
                    }
                    dialog.create()
                    dialog.show()

                    //in case move to home screen
                    /*
                    val intent = Intent(this@HomePageActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()*/
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    private fun openHome() {
        print("opening HomePage")
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                HomeFragment()
            )
            .commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)    //1
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //2

    }
}