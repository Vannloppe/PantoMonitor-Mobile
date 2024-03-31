package com.example.pantomonitor.viewmodel



import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pantomonitor.model.StatsProvider
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.EventListener
import java.util.Locale


class BdMainViewModel : ViewModel() {
    private var database = FirebaseDatabase.getInstance().getReference("New_Entries")
    private val getbad = database.orderByChild("Assessment").equalTo("Replace")
    private val getgood = database.orderByChild("Assessment").equalTo("Good")
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private var stats = StatsProvider()
    private val _pieChartData = MutableLiveData<List<PieEntry>>()
    val pieChartData: LiveData<List<PieEntry>> get() = _pieChartData


    private var _dataList = MutableLiveData<List<timelinephoto>?>()
    val dataList: MutableLiveData<List<timelinephoto>?> get() = _dataList


    private var _dataListentry = MutableLiveData<List<timelinephoto>?>()
    val dataListEntry: MutableLiveData<List<timelinephoto>?> get() = _dataListentry




    init {
        val entries = mutableListOf<PieEntry>()
        fetchData()


        getbad.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(querySnapshot: DataSnapshot) {
                if (querySnapshot.exists()) {
                    stats.Defectcounterdata.value = querySnapshot.childrenCount.toString()
                    stats.totalcounterdata.value = querySnapshot.childrenCount.toInt()
                    val defect = querySnapshot.childrenCount.toFloat()
                    entries.add(PieEntry(defect, "Replace"))
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

        database.limitToLast(1).addValueEventListener(object : ValueEventListener { // GETTING THE LAST ENTRY FOR LATEST ENTRY

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
        val current = getUnixTimestamp(curdate).toString()

        val getdatedaily = database.orderByChild("Date").startAt(current)

        getdatedaily.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.Goodcounterdatadaily.value = (stats.Goodcounterdatadaily.value ?: 0) + 1

                        }
                        else if (parse.Assessment == "Replace"){
                            stats.Defectcounterdatadaily.value = (stats.Defectcounterdatadaily.value ?: 0) + 1

                        }
                    }

                }

            }
            override fun onCancelled(databaseError: DatabaseError) {

            }



        })


        //WEEKLY
        var currentDatew = Calendar.getInstance()

        currentDatew.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        currentDatew.add(Calendar.WEEK_OF_YEAR, -1)
        var dateFormat = SimpleDateFormat("MM-dd-yyyy")
        var startDateweek = getunixtimestampexport(dateFormat.format(currentDatew.time).toString())


        currentDatew.add(Calendar.DAY_OF_YEAR,7 )
        var endDateweek = getunixtimestampexport(dateFormat.format(currentDatew.time).toString())
        val getdateweekly = database.orderByChild("Date").startAt("$startDateweek\uF8FF").endAt("$endDateweek\uF8FF")

        getdateweekly.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good"){
                            stats.Goodcounterdataweekly.value = (stats.Goodcounterdataweekly.value ?: 0) + 1

                        }
                        else if (parse.Assessment == "Replace"){
                            stats.Defectcounterdataweekly.value = (stats.Defectcounterdataweekly.value ?: 0) + 1

                        }
                    }

                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })


        //MONTHLY
        val currentDatem = Calendar.getInstance()



        currentDatem.set(Calendar.DAY_OF_MONTH, 0)



        var startDatemon = currentDatem.time.toString()
        var startmon = getunixtimestamp(startDatemon)

        currentDatem.add(Calendar.MONTH, 1)
        currentDatem.add(Calendar.DAY_OF_MONTH, -1)


        var endDatemon = currentDatem.time.toString()
        var endmon = getunixtimestamp(endDatemon)

        val getdatemonthly = database.orderByChild("Date").endAt("$endmon\uF8FF").startAt("$startmon\uF8FF")

        getdatemonthly.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    var parse = entrySnapshot.getValue(parsed::class.java)
                    //    val check = entrySnapshot.getValue(StatsProvider.parsed::class.java)
                    if (parse != null) {
                        if (parse.Assessment == "Good") {
                            stats.Goodcounterdatamonthly.value =
                                (stats.Goodcounterdatamonthly.value ?: 0) + 1

                        } else if (parse.Assessment == "Replace") {
                            stats.Defectcounterdatamonthly.value =
                                (stats.Defectcounterdatamonthly.value ?: 0) + 1

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }



        })





    }

    //FETCH DATA FOR TIMELINE
    fun fetchData() {
        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.DAY_OF_MONTH, 0)


        var startDatemon = currentDate.time.toString()
        var startmon = getunixtimestamp(startDatemon)

        currentDate.add(Calendar.MONTH, 1)
        currentDate.add(Calendar.DAY_OF_MONTH, -1)


        var endDatemon = currentDate.time.toString()
        var endmon = getunixtimestamp(endDatemon)


        val getdate = database.orderByChild("Date").endAt("$endmon").startAt("$startmon")
        getdate.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemList = mutableListOf<timelinephoto>()

                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(timelinephoto::class.java)
                    item?.let {
                        itemList.add(it)
                    }
                }
                _dataList.value = itemList.asReversed()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


//FETCH DATA FROM USER FOR TIMELINE


    fun updateQuery(userInput: String, userInput2: String) {
        val query = database.orderByChild("Date").endAt(userInput2).startAt(userInput)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<timelinephoto>()

                for (dataSnapshot in snapshot.children) {
                    val item = dataSnapshot.getValue(timelinephoto::class.java)
                    item?.let { items.add(it) }
                }

                _dataList.value = items.asReversed()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    //FETCH DATA FROM USER FOR EXPORT XLS

    fun updateQueryexport(userInput:String, userInput2: String,) {

        val query = database.orderByChild("Date").startAt(userInput).endAt(userInput2)


        query.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var items = mutableListOf<timelinephoto>();
                for (dataSnapshot in snapshot.children) {
                    val item = dataSnapshot.getValue(timelinephoto::class.java)
                    item?.let { items.add(it) }
                }

                _dataListentry.value = items
            }



            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
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

    fun getUnixTimestamp(dateString: String): Long {
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)
        val date = dateFormat.parse(dateString)
        return date?.time ?: 0L / 1000 // dividing by 1000 to convert milliseconds to seconds
    }

    fun getunixtimestampexport(dateString: String):String {
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




