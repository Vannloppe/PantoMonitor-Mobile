package com.example.pantomonitor.viewmodel


import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.pantomonitor.model.StatsProvider
import com.github.mikephil.charting.data.PieEntry

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.reflect.Modifier
import java.util.Objects


class BdMainViewModel : ViewModel() {
    private var databasegood = FirebaseDatabase.getInstance().getReference("New_Entries")
    private val getbad = databasegood.orderByChild("Assessment").equalTo("Bad")
    private val getgood = databasegood.orderByChild("Assessment").equalTo("Good")
    private var stats = StatsProvider()




    val storage = FirebaseStorage.getInstance().getReference("images/newImage0.jpg")


    private val _pieChartData = MutableLiveData<List<PieEntry>>()
    val pieChartData: LiveData<List<PieEntry>> = _pieChartData




    init {
        val entries = mutableListOf<PieEntry>()



       getgood.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(querySnapshot: DataSnapshot) {
                if (querySnapshot.exists()) {
                    stats.Goodcounterdata.value = querySnapshot.childrenCount.toString() // Now you can use 'documentCount' as the total count of documents that match your query.
                    val good = querySnapshot.childrenCount.toFloat()
                    entries.add(PieEntry(good, "Good"))
                    }

                getbad.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(querySnapshot: DataSnapshot) {
                        if (querySnapshot.exists()) {
                            stats.Defectcounterdata.value = querySnapshot.childrenCount.toString() // Now you can use 'documentCount' as the total count of documents that match your query.
                            val defect = querySnapshot.childrenCount.toFloat()
                            entries.add(PieEntry(defect, "Defects"))
                        }
                        _pieChartData.postValue(entries)
                    }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle error for the second collection
                        }
                    })
                }

           override fun onCancelled(databaseError: DatabaseError) {
               // Handle error for the first collection
           }

    })

    }



    fun getGoodData(): LiveData<String>{
        return stats.Goodcounterdata
    }

    fun getDefectData(): LiveData<String> {
        return stats.Defectcounterdata
    }


}



