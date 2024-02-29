package com.example.pantomonitor.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pantomonitor.databinding.FragmentAnalyticsBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AnalyticsFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding
    private lateinit var viewModel: BdMainViewModel
    private lateinit var pieChartdaily: PieChart
    private lateinit var pieChartmonthly: PieChart
    private lateinit var pieChartweekly: PieChart
    val normal = "Normal parameters"
    val abnormal = "high number designated for replacement"
    val norecord = "no records marked for replacement"





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAnalyticsBinding.inflate(
            inflater,
            container,
            false
        )  // Inflate using ViewBinding
        //viewModel = ViewModelProvider(this).get(BdMainViewModel::class.java)
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)

        pieChartdaily = binding.pieChartDaily

        pieChartdaily.setDrawEntryLabels(false)
        pieChartdaily.description.isEnabled = false
        val legend = pieChartdaily.legend
        legend.isEnabled = false

        pieChartdaily.isRotationEnabled = false
        pieChartdaily.minAngleForSlices = 10f
        pieChartdaily.holeRadius = 60f
        pieChartdaily.setTransparentCircleAlpha(255)

        pieChartweekly = binding.pieChartWeekly

        pieChartweekly.setDrawEntryLabels(false)
        pieChartweekly.description.isEnabled = false
        val legendweekly = pieChartweekly.legend
        legendweekly.isEnabled = false

        pieChartweekly.isRotationEnabled = false
        pieChartweekly.minAngleForSlices = 10f
        pieChartweekly.holeRadius = 60f
        pieChartweekly.setTransparentCircleAlpha(255)


        pieChartmonthly = binding.pieChartMonthly

        pieChartmonthly.setDrawEntryLabels(false)
        pieChartmonthly.description.isEnabled = false
        val legendmonthly = pieChartmonthly.legend
        legendmonthly.isEnabled = false

        pieChartmonthly.isRotationEnabled = false
        pieChartmonthly.minAngleForSlices = 10f
        pieChartmonthly.holeRadius = 60f
        pieChartmonthly.setTransparentCircleAlpha(255)




        viewModel.getdgood().observe(viewLifecycleOwner, Observer { data1 ->
            // Update your UI with the data from the ViewModel
            binding.Dgood.text = data1.toString()


            viewModel.getdbad().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel

                // var cur = viewModel.getCurrentDate()

                var currentDated = Calendar.getInstance()
                var dateFormat = SimpleDateFormat("MMMM dd, yyyy")
                var current = dateFormat.format(currentDated.time).toString()


                var total = data1 + data2
                var good = data1.toInt()
                var bad = data2.toInt()
                binding.Dbad.text = data2.toString()

                binding.tvgoodcounterdaily.text = good.toString()
                binding.tvdefectcounterdaily.text = bad.toString()
                binding.totacounterdaily.text = total.toString()
                binding.datedaily.text = current



                if ( total== 0 ){
                    binding.dailyconclu.text = "The data reveals that,there are ${boldWholeString("zero records for both operational and replacement carbon strips")}. Please provide additional data for further analysis."

                }
                else if (good > bad){
                    binding.dailyconclu.text = "The records indicate that the ratio between functional and replacement-worthy carbon strips is within ${boldWholeString(normal)}."
                }
                else if (good < bad) {
                    binding.dailyconclu.text =  "The records suggest an abnormal ratio between functional and replacement carbon strips, with a notably ${boldWholeString(abnormal)}."
                }

                else if (bad == 0){
                    binding.dailyconclu.text = "The records indicate an abnormal ratio between operational and replacement carbon strips, as there are currently ${boldWholeString("no records marked for replacement")}."
                }




            })



        })



        viewModel.getwgood().observe(viewLifecycleOwner, Observer { data1 ->
            binding.Wgood.text = data1.toString()


            viewModel.getwbad().observe(viewLifecycleOwner, Observer { data2 ->

                var currentDatew = Calendar.getInstance()
                currentDatew.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                currentDatew.add(Calendar.WEEK_OF_YEAR, -1)
                var dateFormat = SimpleDateFormat("MMMM dd, yyyy")
                currentDatew.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                var startDateweek = dateFormat.format(currentDatew.time).toString()


                currentDatew.add(Calendar.DAY_OF_YEAR,6)
                var endDateweek = (dateFormat.format(currentDatew.time).toString())



                var total = data1 + data2
                var good = data1.toInt()
                var bad = data2.toInt()



                binding.Wbad.text = data2.toString()
                binding.tvgoodcounterweekly.text = good.toString()
                binding.tvdefectcounterweekly.text = bad.toString()
                binding.totacounterweekly.text = total.toString()
                binding.dateweekly.text = "$startDateweek - $endDateweek"





                if (good > bad){
                    binding.weeklyconclu.text = "The records indicate that the ratio between functional and replacement-worthy carbon strips is within ${boldWholeString(normal)}."
                }
                else if (good < bad) {
                    binding.weeklyconclu.text =  "The records suggest an abnormal ratio between functional and replacement carbon strips, with a notably ${boldWholeString(abnormal)}."
                }
                else if (bad == 0){
                    binding.weeklyconclu.text = "The records indicate an abnormal ratio between operational and replacement carbon strips, as there are currently ${boldWholeString(norecord)}."
                }


            })

        })



        viewModel.getmgood().observe(viewLifecycleOwner, Observer { data1 ->
            // Update your UI with the data from the ViewModel
            binding.Mgood.text = data1.toString()

            viewModel.getmbad().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val calendar = Calendar.getInstance()
                var monthh = SimpleDateFormat("MMMM", Locale.getDefault())
                var monthhh = monthh.format(calendar.time)








                binding.Mbad.text = data2.toString()
                var cur = viewModel.getCurrentDate()
                var total = data1 + data2
                var good = data1.toInt()
                var bad = data2.toInt()

                binding.Wbad.text = data2.toString()
                binding.tvgoodcountermonthly.text = good.toString()
                binding.tvdefectcountermonthly.text = bad.toString()
                binding.totacountermonthly.text = total.toString()
                binding.datemonthly.text = "Month of $monthhh"


                if (good > bad){
                    binding.monthlyconclu.text = "The records indicate that the ratio between functional and replacement-worthy carbon strips is within .${boldWholeString("normal parameters")}."
                }
                else if (good < bad) {
                    binding.monthlyconclu.text =  "The records suggest an abnormal ratio between functional and replacement carbon strips, with a notably ${boldWholeString("high number designated for replacement")}."
                }
                else if (bad == 0){
                    binding.monthlyconclu.text = "The records indicate an abnormal ratio between operational and replacement carbon strips, as there are currently ${boldWholeString(" no records marked for replacement")}."
                }
            })

        })



        viewModel.pieChartDatad.observe(viewLifecycleOwner) { entries ->
            setupPieChartdaily(entries)
        }

        viewModel.pieChartDataw.observe(viewLifecycleOwner) { entries ->
            setupPieChartweekly(entries)
        }

        viewModel.pieChartDatam.observe(viewLifecycleOwner) { entries ->
            setupPieChartmonthly(entries)
        }










        return binding.root
    }

    private fun setupPieChartdaily(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Daily")
        dataSet.colors = mutableListOf(Color.rgb(241, 201, 59), Color.rgb(159, 187, 115))
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 5f
        val data = PieData(dataSet)
        pieChartdaily.data = data
        pieChartdaily.invalidate()
    }

    private fun setupPieChartweekly(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Weekly")
        dataSet.colors = mutableListOf(Color.rgb(241, 201, 59), Color.rgb(159, 187, 115))
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 5f
        val data = PieData(dataSet)
        pieChartweekly.data = data
        pieChartweekly.invalidate()
    }

    private fun setupPieChartmonthly(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Monthly")
        dataSet.colors = mutableListOf(Color.rgb(241, 201, 59), Color.rgb(159, 187, 115))
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 5f
        val data = PieData(dataSet)
        pieChartmonthly.data = data
        pieChartmonthly.invalidate()
    }


    private fun boldWholeString(inputString: String): SpannableString {
        val spannableString = SpannableString(inputString)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, inputString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }




}