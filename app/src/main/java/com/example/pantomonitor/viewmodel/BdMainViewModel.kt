package com.example.pantomonitor.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BdMainViewModel : ViewModel() {
    private var databasegood = FirebaseDatabase.getInstance().getReference("New_Entries")
    private val getbad = databasegood.orderByChild("Assessment").equalTo("Bad")
    private val getgood = databasegood.orderByChild("Assessment").equalTo("Good")

    private var Goodcounterdata = MutableLiveData<String>()
    private var Defectcounterdata = MutableLiveData<String>()

    private val _pieChartData = MutableLiveData<List<PieEntry>>()
    val pieChartData: LiveData<List<PieEntry>> = _pieChartData



    init {
        val entries = mutableListOf<PieEntry>()



       getgood.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(querySnapshot: DataSnapshot) {
                if (querySnapshot.exists()) {
                    Goodcounterdata.value = querySnapshot.childrenCount.toString() // Now you can use 'documentCount' as the total count of documents that match your query.
                    val good = querySnapshot.childrenCount.toFloat()
                    entries.add(PieEntry(good, "Good"))
                    }

                getbad.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(querySnapshot: DataSnapshot) {
                        if (querySnapshot.exists()) {
                            Defectcounterdata.value = querySnapshot.childrenCount.toString() // Now you can use 'documentCount' as the total count of documents that match your query.
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


                        /** getgood.get().addOnSuccessListener {
                 querySnapshot ->
            Goodcounterdata.value = querySnapshot.childrenCount.toFloat() // Now you can use 'documentCount' as the total count of documents that match your query.
            val good = querySnapshot.childrenCount.toFloat()


           }
             .addOnFailureListener {

                }

        getbad.get().addOnSuccessListener {
                querySnapshot ->



        }
            .addOnFailureListener {

            }**/




    }



    fun getGoodData(): LiveData<String>{
        return Goodcounterdata
    }

    fun getDefectData(): LiveData<String> {
        return Defectcounterdata
    }








}



