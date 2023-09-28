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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import java.io.File
import java.lang.reflect.Modifier
import java.util.Objects


class BdMainViewModel : ViewModel() {
    private var database = FirebaseDatabase.getInstance().getReference("New_Entries")
    private val getbad = database.orderByChild("Assessment").equalTo("Bad")
    private val getgood = database.orderByChild("Assessment").equalTo("Good")
    private val getjan = database.orderByChild("Date").startAt("01").endAt("01\uf8ff")
    private val getfeb = database.orderByChild("Date").startAt("02").endAt("02\uf8ff")
    private val getmar = database.orderByChild("Date").startAt("03").endAt("03\uf8ff")
    private val getapr = database.orderByChild("Date").startAt("04").endAt("04\uf8ff")
    private val getmay = database.orderByChild("Date").startAt("05").endAt("05\uf8ff")
    private val getjune = database.orderByChild("Date").startAt("06").endAt("06\uf8ff")
    private val getjuly = database.orderByChild("Date").startAt("07").endAt("07\uf8ff")
    private val getaug = database.orderByChild("Date").startAt("08").endAt("08\uf8ff")
    private val getsept = database.orderByChild("Date").startAt("09").endAt("09\uf8ff")
    private val getoct = database.orderByChild("Date").startAt("10").endAt("10\uf8ff")
    private val getnov = database.orderByChild("Date").startAt("11").endAt("11\uf8ff")
    private val getdec = database.orderByChild("Date").startAt("12").endAt("12\uf8ff")


    private var stats = StatsProvider()
    private val _pieChartData = MutableLiveData<List<PieEntry>>()
    val pieChartData: LiveData<List<PieEntry>> = _pieChartData


    init {
        val entries = mutableListOf<PieEntry>()
       getbad.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(querySnapshot: DataSnapshot) {
                if (querySnapshot.exists()) {
                    stats.Defectcounterdata.value = querySnapshot.childrenCount.toString()
                    val defect = querySnapshot.childrenCount.toFloat()
                    entries.add(PieEntry(defect, "defect"))
                    }

                getgood.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(querySnapshot: DataSnapshot) {
                        if (querySnapshot.exists()) {
                            stats.Goodcounterdata.value = querySnapshot.childrenCount.toString()
                             // Now you can use 'documentCount' as the total count of documents that match your query.
                            val good = querySnapshot.childrenCount.toFloat()
                            entries.add(PieEntry(good, "Good"))
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

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        stats.lateststatus.value = parse.Assessment
                        stats.timestampdate.value = parse.Date
                        stats.latestimg.value =parse.Img
                        stats.timestamptime.value = parse.Time
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

        getjan.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounterjan.value = (stats.goodcounterjan.value ?: 0) + 1
                        }
                        else{
                            stats.badcounterjan.value = (stats.badcounterjan.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

        getfeb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                      if (parse.Assessment == "Good"){
                          stats.goodcounterfeb.value = (stats.goodcounterfeb.value ?: 0) + 1
                      }
                        else{
                          stats.badcounterfeb.value = (stats.badcounterfeb.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

        getmar.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcountermar.value = (stats.goodcountermar.value ?: 0) + 1
                        }
                        else{
                            stats.badcountermar.value = (stats.badcountermay.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getapr.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounterapr.value = (stats.goodcounterapr.value ?: 0) + 1
                        }
                        else{
                            stats.badcounterapr.value = (stats.badcounterapr.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getmay.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcountermay.value = (stats.goodcountermay.value ?: 0) + 1
                        }
                        else{
                            stats.badcountermay.value = (stats.badcountermay.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getjune.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounterjune.value = (stats.goodcounterjune.value ?: 0) + 1
                        }
                        else{
                            stats.badcounterjune.value = (stats.badcounterjune.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getjuly.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounterjuly.value = (stats.goodcounterjuly.value ?: 0) + 1
                        }
                        else{
                            stats.badcounterjuly.value = (stats.badcounterjuly.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getaug.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounteraug.value = (stats.goodcounteraug.value ?: 0) + 1
                        }
                        else{
                            stats.badcounteraug.value = (stats.badcounteraug.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getsept.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcountersep.value = (stats.goodcountersep.value ?: 0) + 1
                        }
                        else{
                            stats.badcountersep.value = (stats.badcountersep.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getoct.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounteroct.value = (stats.goodcounteroct.value ?: 0) + 1
                        }
                        else{
                            stats.badcounteroct.value = (stats.badcounteroct.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getnov.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounternov.value = (stats.goodcounternov.value ?: 0) + 1
                        }
                        else{
                            stats.badcounternov.value = (stats.badcounternov.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getdec.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.goodcounterdec.value = (stats.goodcounterdec.value ?: 0) + 1
                        }
                        else{
                            stats.badcounterdec.value = (stats.goodcounterdec.value ?: 0) + 1
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

    }




    fun getGoodData(): LiveData<String>{
        return stats.Goodcounterdata
    }

    fun getDefectData(): LiveData<String> {
        return stats.Defectcounterdata
    }
    fun getlatestStatus(): LiveData<String> {
        return stats.lateststatus
    }
    fun getlatestDate(): LiveData<String> {
        return stats.timestampdate
    }
    fun getlatestTime(): LiveData<String> {
        return stats.timestamptime
    }
    fun getlatestImg(): LiveData<String> {
        return stats.latestimg
    }

    fun getlatestgoodjan(): LiveData<Int>{
        return  stats.goodcounterjan
    }
    fun getlatestbadjan(): LiveData<Int>{
        return  stats.badcounterjan
    }
    fun getlatestbadfeb(): LiveData<Int>{
        return  stats.badcounterfeb
    } fun getlatestgoodfeb(): LiveData<Int>{
        return  stats.goodcounterfeb
    }
    fun getlatestbadmar(): LiveData<Int>{
        return  stats.badcountermar
    } fun getlatestgoodmar(): LiveData<Int>{
        return  stats.goodcountermar
    }
    fun getlatestbadapr(): LiveData<Int>{
        return  stats.badcounterapr
    } fun getlatestgoodapr(): LiveData<Int>{
        return  stats.goodcounterapr
    }
    fun getlatestbadmay(): LiveData<Int>{
        return  stats.badcountermay
    } fun getlatestgoodmay(): LiveData<Int>{
        return  stats.goodcountermay
    }
    fun getlatestbadjune(): LiveData<Int>{
        return  stats.badcounterjune
    } fun getlatestgoodjune(): LiveData<Int>{
        return  stats.goodcounterjune
    }
    fun getlatestbadjuly(): LiveData<Int>{
        return  stats.badcounterjuly
    } fun getlatestgoodjuly(): LiveData<Int>{
        return  stats.goodcounterjuly
    }
    fun getlatestbadaug(): LiveData<Int>{
        return  stats.badcounteraug
    } fun getlatestgoodaug(): LiveData<Int>{
        return  stats.goodcounteraug
    }
    fun getlatestbadsep(): LiveData<Int>{
        return  stats.badcountersep
    } fun getlatestgoodsep(): LiveData<Int>{
        return  stats.goodcountersep
    }
    fun getlatestbadoct(): LiveData<Int>{
        return  stats.badcounteroct
    } fun getlatestgoodoct(): LiveData<Int>{
        return  stats.goodcounteroct
    }
    fun getlatestbadnov(): LiveData<Int>{
        return  stats.badcounternov
    } fun getlatestgoodnov(): LiveData<Int>{
        return  stats.goodcounternov
    }
    fun getlatestbaddec(): LiveData<Int>{
        return  stats.badcounterdec
    }
    fun getlatestgooddec(): LiveData<Int>{
        return  stats.goodcounterdec
    }


}
data class parsed(
    val Assessment: String = "",
    val Date: String = "",
    val Img: String = "",
    val Time: String = ""

)


