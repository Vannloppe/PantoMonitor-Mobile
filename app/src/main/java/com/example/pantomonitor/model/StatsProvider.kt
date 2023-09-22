package com.example.pantomonitor.model
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log;


class StatsProvider {

    private val dbGood = 0
    private lateinit var database : DatabaseReference

    var databasegood = FirebaseDatabase.getInstance().getReference("New_Entries")
    val getgbad = databasegood.orderByChild("Assessment").equalTo("Bad").get()
    val getggood = databasegood.orderByChild("Assessment").equalTo("Good").get()

    val goodcounterjan = 0;
    val badcounterjan = 0;

    val goodcounterfeb = 0;
    val badcounterfeb = 0;

    val goodcountermar = 0;
    val badcountermar = 0;

    val goodcounterapr = 0;
    val badcounterapr = 0;

    val goodcountermay = 0;
    val badcountermay = 0;

    val goodcounterjune = 0;
    val badcounterjune = 0;

    val goodcounterjuly = 0;
    val badcounterjuly = 0;

    val goodcounteraug = 0;
    val badcounteraug = 0;

    val goodcountersep = 0;
    val badcountersep = 0;

    val goodcounteroct = 0;
    val badcounteroct = 0;

    val goodcounternov = 0;
    val badcounternov = 0;

    val goodcounterdec = 0;
    val badcounterdec = 0;


}



