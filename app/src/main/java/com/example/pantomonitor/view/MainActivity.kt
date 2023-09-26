package com.example.pantomonitor.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFrag())


        binding.botNavMenu.setOnItemSelectedListener {
            when(it.itemId){

                R.id.Home -> replaceFragment(HomeFrag())
                R.id.Timeline ->replaceFragment(TimelineFragment())
                R.id.Settings -> replaceFragment(SettingFragment())
                else ->{

                }
            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_main,fragment)
        fragmentTransaction.commit()
    }
}