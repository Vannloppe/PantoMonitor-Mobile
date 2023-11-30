package com.example.pantomonitor.viewmodel



import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.pantomonitor.model.StatsProvider
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.reflect.Array.get
import java.util.Calendar


class BdMainViewModel : ViewModel() {
    private var database = FirebaseDatabase.getInstance().getReference("New_Entries")
    private val getbad = database.orderByChild("Assessment").equalTo("Bad")
    private val getgood = database.orderByChild("Assessment").equalTo("Good")


    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference


    private var stats = StatsProvider()
    private val _pieChartData = MutableLiveData<List<PieEntry>>()
    val pieChartData: LiveData<List<PieEntry>> get() = _pieChartData


    private var _dataList = MutableLiveData<List<timelinephoto>?>()
    val dataList: MutableLiveData<List<timelinephoto>?> get() = _dataList

    private val _dataFromView = MutableLiveData<String>()
    val dataFromView: LiveData<String> get() = _dataFromView


    val calendar: Calendar = Calendar.getInstance()
    private val sumcalendar = calendar.get(Calendar.MONTH) + 1

/*
    fun fetdchData() {
        // Simulated data fetching from a repository or network call
        val newData = getNewEntriesFromRepository()

        // Update the LiveData with the new data
        _dataList.value = newData
    }

    private fun getNewEntriesFromRepository(): MutableList<timelinephoto> {
        val getdate = database.orderByChild("Date").startAt(_dataFromView.value)
            .endAt(_dataFromView.value + "\uf8ff")
        val data = mutableListOf<timelinephoto>()
        getdate.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (itemSnapshot in dataSnapshot.children) {
                    var parse = itemSnapshot.getValue(parsed::class.java)

                    if (parse != null) {
                        val assessmnet = parse.Assessment
                        val datedata = parse.Date
                        val imgdata = parse.Img
                        val timedata = parse.Time
                        val model = timelinephoto(assessmnet, datedata, imgdata, timedata)
                        data.add(model)
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
        return data
    }

 */


    init {
        val entries = mutableListOf<PieEntry>()
        fetchData()











        getbad.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(querySnapshot: DataSnapshot) {
                if (querySnapshot.exists()) {
                    stats.Defectcounterdata.value = querySnapshot.childrenCount.toString()
                    stats.totalcounterdata.value = querySnapshot.childrenCount.toInt()
                    val defect = querySnapshot.childrenCount.toFloat()
                    entries.add(PieEntry(defect, "defect"))
                }

                getgood.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(querySnapshot: DataSnapshot) {
                        if (querySnapshot.exists()) {
                            stats.Goodcounterdata.value = querySnapshot.childrenCount.toString()
                            stats.totalcounterdata.value = stats.totalcounterdata.value?.plus(
                                querySnapshot.childrenCount.toInt()
                            )
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

        database.limitToLast(1).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        stats.lateststatus.value = parse.Assessment
                        stats.timestampdate.value = parse.Date
                        stats.latestimg.value = parse.Img
                        stats.timestamptime.value = parse.Time
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

    }


    private fun fetchData() {
        // Read from the database
        val getdate = database.orderByChild("Date").startAt("$sumcalendar-").endAt("$sumcalendar-\uf8ff")
        getdate.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemList = mutableListOf<timelinephoto>()

                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(timelinephoto::class.java)
                    item?.let {
                        itemList.add(it)
                    }
                }
                _dataList.value = itemList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    fun updateQuery(userInput: String) {
        // Update the query based on user input
        val query = database.orderByChild("Date").startAt("$userInput-").endAt("$userInput-\uF8FF")

        // Fetch data using the updated query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<timelinephoto>()

                for (dataSnapshot in snapshot.children) {
                    val item = dataSnapshot.getValue(timelinephoto::class.java)
                    item?.let { items.add(it) }
                }

                // Update _data LiveData with the new filtered data
                _dataList.value = items
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
    }













    // CALENDAR


        fun getGoodData(): LiveData<String> {
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

        fun gettotalcounter(): LiveData<Int> {
            return stats.totalcounterdata
        }

        fun getlatestpic(img: String): StorageReference {

            return storageRef.child("images/${img}")
        }

        fun updateLiveData(newValue: String) {
            _dataFromView.value = newValue

        }





}





    data class parsed(
        val Assessment: String = "",
        val Date: String = "",
        val Img: String = "",
        val Time: String = ""

    )

    data class timelinephoto(
        val Assessment: String = "",
        val Date: String = "",
        val Img: String = "",
        val Time: String = ""

    )




