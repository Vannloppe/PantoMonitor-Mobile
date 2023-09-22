package com.example.pantomonitor.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class BdMainViewModel : ViewModel() {

    private var databasegood = FirebaseDatabase.getInstance().getReference("New_Entries")
    private val getbad = databasegood.orderByChild("Assessment").equalTo("Bad")
    private val getgood = databasegood.orderByChild("Assessment").equalTo("Good")

    private var Goodcounterdata = MutableLiveData<String>()
    private var Defectcounterdata = MutableLiveData<String>()


    init {
        getgood.get().addOnSuccessListener {
                 querySnapshot ->
            Goodcounterdata.value = querySnapshot.childrenCount.toString() // Now you can use 'documentCount' as the total count of documents that match your query.

           }
             .addOnFailureListener {

                }
    }

    fun getGoodData(): LiveData<String> {
        return Goodcounterdata
    }

    init {
        getbad.get().addOnSuccessListener {
                querySnapshot ->
            Defectcounterdata.value = querySnapshot.childrenCount.toString() // Now you can use 'documentCount' as the total count of documents that match your query.

        }
            .addOnFailureListener {

            }
    }

    fun getDefectData(): LiveData<String> {
        return Defectcounterdata
    }



}