package com.example.pantomonitor.model
import androidx.lifecycle.MutableLiveData
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.timelinephoto
import com.google.gson.Gson


class StatsProvider {




    var Goodcounterdata = MutableLiveData<String>()
    var Defectcounterdata = MutableLiveData<String>()
    var totalcounterdata = MutableLiveData<Int>(0)

    var latestimg = MutableLiveData<String>()
    var lateststatus = MutableLiveData<String>()
    var timestampdate = MutableLiveData<String>()
    var timestamptime = MutableLiveData<String>()
    var trainno = MutableLiveData<String>()
    var cartno = MutableLiveData<String>()



    var Goodcounterdatadaily = MutableLiveData<Int>(0)
    var Defectcounterdatadaily = MutableLiveData<Int>(0)
    var Goodcounterdataweekly = MutableLiveData<Int>(0)
    var Defectcounterdataweekly = MutableLiveData<Int>(0)
    var Goodcounterdatamonthly = MutableLiveData<Int>(0)
    var Defectcounterdatamonthly = MutableLiveData<Int>(0)






}







