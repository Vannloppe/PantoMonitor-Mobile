package com.example.pantomonitor.view


import android.annotation.SuppressLint
import android.content.Intent

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText

import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.ActivityMainBinding
import com.example.pantomonitor.ml.NasnetmobileModel985
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook

import java.io.File

import java.io.FileOutputStream

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BdMainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private  lateinit var  nasnetlatest: NasnetmobileModel985
    private var isButtonClickable = true
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)




        if (Environment.isExternalStorageManager()) {
            // The app has been granted the MANAGE_EXTERNAL_STORAGE permission
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        }
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        nasnetlatest = NasnetmobileModel985.newInstance((this))

        setSupportActionBar(binding.toolbar)



        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setDisplayHomeAsUpEnabled(true)



            val customActionBar = LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.actionbar_font, null)
            customView = customActionBar
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
            user.let {
                // Name, email address, and profile photo Url
                val email = user.email
                headerTextView.text = email.toString()


            }
        }

        binding.navViewer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFrag())
                R.id.nav_timeline -> replaceFragment(TimelineFragment())
                R.id.nav_prediction -> replaceFragment(PlaceHolder())
                R.id.nav_analyics -> replaceFragment(AnalyticsFragment())
                R.id.nav_export -> {
                    val anchorView: View = findViewById(R.id.nav_export)
                    showPopup(anchorView)
                    true
                }

                R.id.nav_about -> replaceFragment(AboutFragment())
                R.id.nav_logout -> logout()

                else -> {
                }
            }
            true
        }

        binding.botNavMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bot_Home -> replaceFragment(HomeFrag())
                R.id.bot_Timeline -> replaceFragment(TimelineFragment())
                R.id.bot_Placeholder -> replaceFragment(PlaceHolder())
                R.id.bot_Analytics -> replaceFragment(AnalyticsFragment())
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

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_main, fragment)
        fragmentTransaction.commit()

    }



    fun getLatestmodel():NasnetmobileModel985 {
        return nasnetlatest
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

    @SuppressLint("MissingInflatedId")
    private fun showPopup(anchorView: View) {
        val inflater = getSystemService(LayoutInflater::class.java)
        val popupView: View = inflater.inflate(R.layout.popupwindow, null)
        val editText: EditText = popupView.findViewById(R.id.editTextDate)
        val editText1: EditText = popupView.findViewById(R.id.editTextDate2)
        val editText2: EditText = popupView.findViewById(R.id.editTextNumber3)
        val btnFilter: Button = popupView.findViewById(R.id.buttonaccept)

        // Find the RecyclerView container

        // Create a PopupWindow with the inflated view
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Set some options for the popup window
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Show the popup window
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)

        btnFilter.setOnClickListener {
            if (isButtonClickable) {
                // Disable the button to prevent multiple clicks
                isButtonClickable = false
                btnFilter.isEnabled = false


                val enteredText1 = viewModel.getunixtimestampexport(editText.text.toString())
                val enteredText2 = viewModel. getunixtimestampexport(editText1.text.toString())
                val enteredText3 = editText2.text.toString()

                val Dataname = updateDate() + ".xls"

                viewModel.updateQueryexport(enteredText1,enteredText2,enteredText3)


                viewModel.dataList.observe(this) { newData ->

                    if (!newData.isNullOrEmpty()) {



                        val workbook: Workbook =   HSSFWorkbook()
                        val sheet = workbook.createSheet("Exported")

                        val headerRow: Row = sheet.createRow(0)
                        headerRow.createCell(0).setCellValue("Img-link")
                        headerRow.createCell(1).setCellValue("Img")
                        headerRow.createCell(2).setCellValue("Assessment")
                        headerRow.createCell(3).setCellValue("Date")
                        headerRow.createCell(4).setCellValue("Time")
                        headerRow.createCell(5).setCellValue("TrainNo")
                        headerRow.createCell(6).setCellValue("CartNo")

                        var rowNum = 1
                        for (data in newData) {
                            val row: Row = sheet.createRow(rowNum++)
                            row.createCell(0).setCellValue(gettimepic(data.Img).toString())
                            row.createCell(1).setCellValue(data.Img)
                            row.createCell(2).setCellValue(data.Assessment)
                            val date = data.Date.toLong() * 1000L
                            val dateFormat = SimpleDateFormat("MM-dd-yyyy")
                            row.createCell(3).setCellValue(dateFormat.format(date))
                            row.createCell(4).setCellValue(data.Time)
                            row.createCell(5).setCellValue(data.TrainNo)
                            row.createCell(6).setCellValue(data.CartNo)
                            // Add more cells for additional columns
                        }

                        // Write the workbook to the file


                        val filePath =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .absolutePath + File.separator + Dataname

                        val fileOut = FileOutputStream(filePath)
                        workbook.write(fileOut)
                        fileOut.close()


                    } else {
                        showToast("Filter Unsuccessful")
                    }


                }


                showToast("Success")
                popupWindow.dismiss()

                Handler().postDelayed({
                    // Re-enable the button after the delay
                    isButtonClickable = true
                    btnFilter.isEnabled = true
                }, 2000) // 2000 milliseconds = 2 seconds
            }
        }


        // Close the popup window when clicked
        popupView.setOnClickListener {
            //
        }
    }

    private fun showToast(message: String) {
        // Replace this with your desired method of displaying a message (e.g., Toast)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun gettimepic(img: String): StorageReference {

        return storageRef.child("images/${img}")
    }

}










