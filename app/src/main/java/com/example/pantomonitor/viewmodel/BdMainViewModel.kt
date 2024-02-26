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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class BdMainViewModel : ViewModel() {
    private var database = FirebaseDatabase.getInstance().getReference("New_Entries")
    private val getbad = database.orderByChild("Assessment").equalTo("Bad")
    private val getgood = database.orderByChild("Assessment").equalTo("Good")



    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference


    private var stats = StatsProvider()
    private val _pieChartData = MutableLiveData<List<PieEntry>>()
    private val _pieChartDatad = MutableLiveData<List<PieEntry>>()
    private val _pieChartDataw = MutableLiveData<List<PieEntry>>()
    private val _pieChartDatam = MutableLiveData<List<PieEntry>>()
    val pieChartData: LiveData<List<PieEntry>> get() = _pieChartData
    val pieChartDatad: LiveData<List<PieEntry>> get() = _pieChartDatad
    val pieChartDataw: LiveData<List<PieEntry>> get() = _pieChartDataw
    val pieChartDatam: LiveData<List<PieEntry>> get() = _pieChartDatam


    private var _dataList = MutableLiveData<List<timelinephoto>?>()
    val dataList: MutableLiveData<List<timelinephoto>?> get() = _dataList

    private val _dataFromView = MutableLiveData<String>()
    val dataFromView: LiveData<String> get() = _dataFromView







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
        val entriesdaily = mutableListOf<PieEntry>()
        val entriesweekly = mutableListOf<PieEntry>()
        val entriesmonthly = mutableListOf<PieEntry>()
        val gooddaaily = 0


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
                        stats.trainno.value = parse.TrainNo
                        stats.cartno.value = parse.CartNo
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })


//ANALYTICS

        //DAILY
        val curdate = getCurrentDate()
        val current = getunixtimestampexport(curdate)


        val getdatedaily = database.orderByChild("Date").startAt("$current")


        getdatedaily.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.Goodcounterdatadaily.value = (stats.Goodcounterdatadaily.value ?: 0) + 1
                                stats.Goodcounterdatadaily.value?.toFloat()
                                    ?.let { PieEntry(it, "Good") }?.let { entriesdaily.add(it) }
                        }
                        else{
                            stats.Defectcounterdatadaily.value = (stats.Defectcounterdatadaily.value ?: 0) + 1
                            stats.Defectcounterdatadaily.value?.toFloat()
                                ?.let { PieEntry(it, "defect") }?.let { entriesdaily.add(it) }
                        }
                    }

                }

                _pieChartDatad.postValue(entriesdaily)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }





        })


        //WEEKLY
        var currentDatew = Calendar.getInstance()

        currentDatew.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        currentDatew.add(Calendar.WEEK_OF_YEAR, -1)
        var dateFormat = SimpleDateFormat("MM-dd-yyyy")
        var startDateweek = getunixtimestampexport(dateFormat.format(currentDatew.time).toString())


        currentDatew.add(Calendar.DAY_OF_YEAR,6)
        var endDateweek = getunixtimestampexport(dateFormat.format(currentDatew.time).toString())


        val getdateweekly = database.orderByChild("Date").startAt(startDateweek).endAt(
            endDateweek
        )
        getdateweekly.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    var good = 0
                    var bad = 0
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.Goodcounterdataweekly.value = (stats.Goodcounterdataweekly.value ?: 0) + 1
                            good = + 1
                            entriesweekly.add(PieEntry(good.toFloat(), "Good"))

                        }
                        else{
                            stats.Defectcounterdataweekly.value = (stats.Defectcounterdataweekly.value ?: 0) + 1
                            bad = + 1
                            entriesweekly.add(PieEntry(bad.toFloat(), "Defect"))

                        }
                    }

                }



                _pieChartDataw.postValue(entriesweekly)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })


        //MONTHLY
        val currentDatem = Calendar.getInstance()


// Set the calendar to the first day of the month
        currentDatem.set(Calendar.DAY_OF_MONTH, 1)

// Format the start date (first day of the current month)

        var startDatemon = currentDatem.time.toString()
        var startmon = getunixtimestamp(startDatemon)

// Move to the end of the month
        currentDatem.add(Calendar.MONTH, 1)
        currentDatem.add(Calendar.DAY_OF_MONTH, -1)

// Format the end date (last day of the current month)
        var endDatemon = currentDatem.time.toString()
        var endmon = getunixtimestamp(endDatemon)

        val getdatemonthly = database.orderByChild("Date").startAt("$startmon\uF8FF").endAt("$endmon\uF8FF")

        getdatemonthly.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good") {
                            stats.Goodcounterdatamonthly.value =
                                (stats.Goodcounterdatamonthly.value ?: 0) + 1

                        } else {
                            stats.Defectcounterdatamonthly.value =
                                (stats.Defectcounterdatamonthly.value ?: 0) + 1

                        }
                    }
                }
                stats.Goodcounterdatamonthly.value?.toFloat()
                    ?.let { PieEntry(it, "Good") }?.let { entriesmonthly.add(it) }

                stats.Defectcounterdatamonthly.value?.toFloat()


                    ?.let { PieEntry(it, "Defect") }?.let { entriesmonthly.add(it) }
                _pieChartDatam.postValue(entriesmonthly)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }



        })





    }


    fun fetchData() {
        // Read from the database
        val currentDate = Calendar.getInstance()

// Set the calendar to the first day of the month
        currentDate.set(Calendar.DAY_OF_MONTH, 1)

// Format the start date (first day of the current month)

        var startDatemon = currentDate.time.toString()
        var startmon = getunixtimestamp(startDatemon)

// Move to the end of the month
        currentDate.add(Calendar.MONTH, 1)
        currentDate.add(Calendar.DAY_OF_MONTH, -1)

// Format the end date (last day of the current month)
        var endDatemon = currentDate.time.toString()
        var endmon = getunixtimestamp(endDatemon)


        val getdate = database.orderByChild("Date").startAt("$startmon").endAt("$endmon\uf8ff")
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

    fun updateQueryexport(userInput: String, userInput2: String) {
        // Update the query based on user input


        val query = database.orderByChild("Date").startAt("$userInput").endAt("$userInput2")

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

    fun updateQuery(userInput: String, userInput2: String) {
        // Update the query based on user input
        val query = database.orderByChild("Date").startAt("$userInput").endAt("$userInput2")

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

    // ANALYTICS














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

        fun getdgood(): LiveData<Int> {
        return stats.Goodcounterdatadaily
        }

        fun getdbad(): LiveData<Int> {
        return stats.Defectcounterdatadaily
        }

        fun getwgood(): LiveData<Int> {
        return stats.Goodcounterdataweekly
        }

        fun getwbad(): LiveData<Int> {
        return stats.Defectcounterdataweekly
        }

        fun getmgood(): LiveData<Int> {
        return stats.Goodcounterdatamonthly
        }

        fun getmbad(): LiveData<Int> {
        return stats.Defectcounterdatamonthly
        }

        fun gettrainno(): LiveData<String> {
        return stats.trainno
        }
        fun getcartno(): LiveData<String> {
        return stats.trainno
        }


        fun getlatestpic(img: String): StorageReference {

            return storageRef.child("images/${img}")
        }

        fun updateLiveData(newValue: String) {
            _dataFromView.value = newValue

        }


    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MM-dd-yyyy")
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun getunixtimestamp(dateString:String):String {
        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")
        val dateTime = LocalDateTime.parse(dateString, formatter)
        val unixTimestamp = dateTime.toEpochSecond(ZoneOffset.UTC)
        return unixTimestamp.toString()
    }

    fun getunixtimestampexport(dateString:String):String {
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val dateTime = LocalDate.parse(dateString, formatter)
        val unixtimestamp = LocalDateTime.of(dateTime, LocalDateTime.MIN.toLocalTime())
        return unixtimestamp.toEpochSecond(ZoneOffset.UTC).toString()
    }









}





    data class parsed(
        val Assessment: String = "",
        val Date: String = "",
        val Img: String = "",
        val Time: String = "",
        val TrainNo: String = "",
        val CartNo: String = ""

    )

    data class timelinephoto(
        val Assessment: String = "",
        val Date: String = "",
        val Img: String = "",
        val Time: String = "",
        val TrainNo: String = "",
        val CartNo: String = ""
    )




