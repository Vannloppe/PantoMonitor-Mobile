package com.example.pantomonitor.view


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.media.Image
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
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.ActivityMainBinding
import com.example.pantomonitor.databinding.PdfexportBinding
import com.example.pantomonitor.ml.LiteModel
import com.example.pantomonitor.ml.Wearnet1

import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import com.google.firebase.auth.FirebaseAuth
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BdMainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: LiteModel
    private lateinit var errorhandling: Wearnet1
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var exadapter: Adapterexport


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)




        if (Environment.isExternalStorageManager()) {
            // The app has been granted the MANAGE_EXTERNAL_STORAGE permission
        } else {
            // The app has not been granted the MANAGE_EXTERNAL_STORAGE permission
            // Request the permission from the user
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        }
        setContentView(binding.root)

        errorhandling = Wearnet1.newInstance(this)
        model = LiteModel.newInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)



        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setDisplayHomeAsUpEnabled(true)

            //setHomeAsUpIndicator(R.drawable.baseline_menu_24)

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
                R.id.nav_export ->  {  val anchorView: View = findViewById(R.id.nav_export)
                    showPopup(anchorView)
                    true}
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

    fun geterrorhandling(): Wearnet1 {
        return errorhandling
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
        // Inflate the popup_layout.xml
        val inflater = getSystemService(LayoutInflater::class.java)
        val popupView: View = inflater.inflate(R.layout.popupwindow, null)


        val editText: EditText = popupView.findViewById(R.id.editTextDate)
        val editText1: EditText = popupView.findViewById(R.id.editTextDate2)
        val btnSubmit: Button = popupView.findViewById(R.id.button1)
        val btnFilter: Button = popupView.findViewById(R.id.buttonfilter)

        val pdfLayout: View = LayoutInflater.from(this).inflate(R.layout.pdfexport, null)

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

        btnFilter.setOnClickListener{
            val enteredText = editText.text.toString()

            viewModel.updateQuery(enteredText)

            val recyclerView = RecyclerView(this)
            viewModel.dataList.observe(this) { newData ->

                exadapter = newData?.let { it1 -> Adapterexport(it1) }!!

                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = exadapter
                exadapter.notifyDataSetChanged()

            }
            val recyclerViewContainer = pdfLayout.findViewById<FrameLayout>(R.id.recyclerViewContainer)
            recyclerViewContainer.addView(recyclerView)






            showToast("FILTERED")


        }


        btnSubmit.setOnClickListener {



                val timestr = updateDate() + ".pdf"
                val document = Document(PageSize.A4)
                val directoryPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(directoryPath, timestr)
                //val pageInfo = PdfDocument.PageInfo.Builder(pdfLayout.width, pdfLayout.height, 1).create()
                //val page = pdfDocument.startPage(pageInfo)

                try {
                    // Create a PdfWriter instance

                        pdfLayout.measure(
                            View.MeasureSpec.makeMeasureSpec(
                                PageSize.A4.width.toInt(),
                                View.MeasureSpec.EXACTLY
                            ),
                            View.MeasureSpec.makeMeasureSpec(
                                PageSize.A4.height.toInt(),
                                View.MeasureSpec.EXACTLY
                            )
                        )

                        pdfLayout.layout(0, 0, pdfLayout.measuredWidth, pdfLayout.measuredHeight)


                        val writer = PdfWriter.getInstance(document, FileOutputStream(file))
                        document.open()
                        // Convert the inflated view to a Bitmap..


                        val bitmap = createBitmapFromView(pdfLayout)

                        // Create an iText Image from the Bitmap
                        val image = com.itextpdf.text.Image.getInstance(bitmapToByteArray(bitmap))

                        // Set the size of the image to fit the PDF page

                        image.scaleToFit(document.pageSize.width, document.pageSize.height)

                        // Add the image to the PDF document
                        document.add(image)







                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Failed")
                } finally {
                    showToast("Success")
                    document.close()
                }












            // Close the popup window
            popupWindow.dismiss()
        }
        
        
        

        // Close the popup window when clicked
        popupView.setOnClickListener {
           // popupWindow.dismiss()
        }
    }

    private fun showToast(message: String) {
        // Replace this with your desired method of displaying a message (e.g., Toast)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun createBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }







}

