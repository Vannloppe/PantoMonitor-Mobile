package com.example.pantomonitor.model
import androidx.lifecycle.MutableLiveData
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.google.gson.Gson


class StatsProvider {

    var Goodcounterdata = MutableLiveData<String>()
    var Defectcounterdata = MutableLiveData<String>()

    var latestimg = MutableLiveData<String>()
    var lateststatus = MutableLiveData<String>()
    var timestampdate = MutableLiveData<String>()
    var timestamptime = MutableLiveData<String>()

    val goodcounterjan = MutableLiveData<Int>(0)
    val badcounterjan = MutableLiveData<Int>(0)

    val goodcounterfeb = MutableLiveData<Int>(0)
    val badcounterfeb = MutableLiveData<Int>(0)


    val goodcountermar = MutableLiveData<Int>(0)
    val badcountermar = MutableLiveData<Int>(0)

    val goodcounterapr = MutableLiveData<Int>(0)
    val badcounterapr = MutableLiveData<Int>(0)

    val goodcountermay = MutableLiveData<Int>(0)
    val badcountermay = MutableLiveData<Int>(0)

    val goodcounterjune = MutableLiveData<Int>(0)
    val badcounterjune = MutableLiveData<Int>(0)

    val goodcounterjuly = MutableLiveData<Int>(0)
    val badcounterjuly = MutableLiveData<Int>(0)

    val goodcounteraug = MutableLiveData<Int>(0)
    val badcounteraug = MutableLiveData<Int>(0)

    val goodcountersep = MutableLiveData<Int>(0)
    val badcountersep = MutableLiveData<Int>(0)

    val goodcounteroct = MutableLiveData<Int>(0)
    val badcounteroct = MutableLiveData<Int>(0)

    val goodcounternov = MutableLiveData<Int>(0)
    val badcounternov = MutableLiveData<Int>(0)

    val goodcounterdec = MutableLiveData<Int>(0)
    val badcounterdec = MutableLiveData<Int>(0)


}







