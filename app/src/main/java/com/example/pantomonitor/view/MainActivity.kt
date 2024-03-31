package com.example.pantomonitor.view


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText

import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.ActivityMainBinding
import com.example.pantomonitor.ml.NasNetMobile1st
import com.example.pantomonitor.ml.NasnetmobileModel
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.apache.poi.hssf.usermodel.HSSFClientAnchor
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import java.io.ByteArrayOutputStream

import java.io.File

import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BdMainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private  lateinit var  nasnetlatest: NasnetmobileModel
    private  lateinit var  error_handling: NasNetMobile1st
    private var isButtonClickable = true
    private val storage = FirebaseStorage.getInstance()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)

        if (Environment.isExternalStorageManager()) {
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        }
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        nasnetlatest = NasnetmobileModel.newInstance((this))
        error_handling = NasNetMobile1st.newInstance((this))

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setDisplayHomeAsUpEnabled(true)

            val customActionBar = LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.actionbar_font, null)
            customView = customActionBar
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


        user?.let {
            // Name, email address, and profile photo Url
            val email = user.email
            headerTextView.text = email.toString()


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



    fun getLatestmodel():NasnetmobileModel {
        return nasnetlatest
    }

    fun geterrorhandling():NasNetMobile1st {
        return error_handling
    }

    private fun logout() {
        firebaseAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


        override fun onSupportNavigateUp(): Boolean {
        binding.drawerLayout.openDrawer(GravityCompat.START)
        return  super.onSupportNavigateUp()
    }



    @SuppressLint("MissingInflatedId")
    private fun showPopup(anchorView: View) {
        val inflater = getSystemService(LayoutInflater::class.java)
        val popupView: View = inflater.inflate(R.layout.popupwindow, null)
        val editText: EditText = popupView.findViewById(R.id.editTextDate)
        val editText1: EditText = popupView.findViewById(R.id.editTextDate2)
        val btnFilter: Button = popupView.findViewById(R.id.buttonaccept)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true


        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
        editText.addTextChangedListener(DateTextWatcher(editText))
        editText1.addTextChangedListener(DateTextWatcher(editText1))


        btnFilter.setOnClickListener {
            if (isButtonClickable) {
                isButtonClickable = false
                btnFilter.isEnabled = false

                if (editText.text.isNotEmpty() && editText1.text.isNotEmpty()) {

                    val enteredText1 = viewModel.getUnixTimestamp(editText.text.toString())
                    val enteredText2 = viewModel.getUnixTimestamp(editText1.text.toString())

                    val Dataname = updateDate() + " "+editText.text.toString() +" "+ editText1.text.toString() + ".xls"
                    viewModel.updateQueryexport(enteredText1.toString(), enteredText2.toString())


                    viewModel.dataListEntry.observe(this) { newData ->

                        if (!newData.isNullOrEmpty()) {


                            val workbook: Workbook = HSSFWorkbook()
                            val sheet = workbook.createSheet("Exported")

                            val headerRow: Row = sheet.createRow(0)
                            headerRow.createCell(0).setCellValue("Img")
                            headerRow.createCell(1).setCellValue("Img-name")
                            headerRow.createCell(2).setCellValue("Assessment")
                            headerRow.createCell(3).setCellValue("Date")
                            headerRow.createCell(4).setCellValue("Time")
                            headerRow.createCell(5).setCellValue("TrainNo")
                            headerRow.createCell(6).setCellValue("CartNo")

                            var rowNum = 1
                            for (data in newData) {
                                val row: Row = sheet.createRow(rowNum++)

                                row.createCell(0).setCellValue("storage.googleapis.com/pantosnap.appspot.com/images/"+data.Img)
                                row.createCell(1).setCellValue(data.Img)
                                row.createCell(2).setCellValue(data.Assessment)
                                val date = data.Date.toLong() * 1000L
                                val dateFormat = SimpleDateFormat("MM-dd-yyyy")
                                row.createCell(3).setCellValue(dateFormat.format(date))
                                row.createCell(4).setCellValue(data.Time)
                                row.createCell(5).setCellValue(data.TrainNo)
                                row.createCell(6).setCellValue(data.CartNo)
                            }

                            val filePath =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .absolutePath + File.separator + Dataname
                            val fileOut = FileOutputStream(filePath)
                            workbook.write(fileOut)
                            fileOut.close()
                            showToast("Success")
                        } else {
                            showToast("Recheck your Entries")
                        }

                    }
                }else{ showToast("Fill up both Entries.")}

                popupWindow.dismiss()

                Handler().postDelayed({
                    isButtonClickable = true
                    btnFilter.isEnabled = true
                }, 2000)
            }
        }
        popupView.setOnClickListener {

        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout_main) as? PlaceHolder
            fragment?.captureImage()
            return true
        }
        return super.onKeyDown(keyCode, event)

    }

    class DateTextWatcher(private val editText: EditText) : TextWatcher {
        private var isDelete = false
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isDelete = count > 0 && after == 0
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            if (!isDelete) {
                if (editable != null) {
                    if (editable != null && editable.length == 2 || editable.length == 5) {
                        editText.text.append("-")
                    }
                }
                if (editable != null && editable.length > 10) {
                    editText.text.delete(10, editable.length)
                }
            } else {
                if (editable != null && (editable.length == 3 || editable.length == 6)) {
                    editText.text.delete(editable.length - 1, editable.length)
                }
            }
        }
    }

}








