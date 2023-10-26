package com.example.pantomonitor.view


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.ActivityMainBinding
import com.example.pantomonitor.ml.LiteModel
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BdMainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: LiteModel
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        model = LiteModel.newInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        // Set your menu icon here
        }
        replaceFragment(HomeFrag())


        val headerView: View = binding.navViewer.getHeaderView(0)
        val headerTextView: TextView = headerView.findViewById(R.id.usernametxtview)
        val headerTextView1: TextView = headerView.findViewById(R.id.dateviewheader)
        val headerTextView2: TextView = headerView.findViewById(R.id.timetodatheader)



        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {

                headerTextView1.text = updateDate()
                headerTextView2.text = updateTime()

                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)










        val firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth.currentUser



        if (user != null) {
            user?.let {
                // Name, email address, and profile photo Url
                val email = user!!.email
                headerTextView.text = email.toString()


            }
        }





        binding.navViewer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFrag())
                R.id.nav_timeline -> replaceFragment(TimelineFragment())
                R.id.nav_prediction -> replaceFragment(PlaceHolder())
                R.id.nav_about -> replaceFragment(AboutFragment())
                R.id.nav_logout -> logout()

                else -> {
                }
            }
                binding.drawerLayout.closeDrawers()
            true
        }

        binding.botNavMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bot_Home -> replaceFragment(HomeFrag())
                R.id.bot_Timeline -> replaceFragment(TimelineFragment())
                R.id.bot_Placeholder -> replaceFragment(PlaceHolder())
                else -> {
                }
            }
            true
        }



        }

    private fun updateDate(): String {
        val currentDateTime = Date()

        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())


        return dateFormat.format(currentDateTime)
    }

    private fun updateTime(): String {
        val currentDateTime = Date()


        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        return timeFormat.format(currentDateTime)
    }









    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_main,fragment)
        fragmentTransaction.commit()

    }
    fun getLiteModel(): LiteModel {
        return model
    }

    private fun logout() {
        firebaseAuth.signOut()
        // Redirect the user to the login screen or any other appropriate screen after logout
        // For example, you can start a new LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    override fun onSupportNavigateUp(): Boolean {
        // Open the drawer when the menu icon is selected
        binding.drawerLayout.openDrawer(GravityCompat.START)
        return true
    }








    }

