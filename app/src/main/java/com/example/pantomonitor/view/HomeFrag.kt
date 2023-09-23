package com.example.pantomonitor.view

import android.graphics.Color
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pantomonitor.databinding.FragmentHomeBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class HomeFrag : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: BdMainViewModel
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)  // Inflate using ViewBinding
        //viewModel = ViewModelProvider(this).get(BdMainViewModel::class.java)
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)
        pieChart = binding.pieChart


        // GOOD
        viewModel.getGoodData().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            binding.tvgoodcounter.text = data.toString()
        })
        // DEFECT
        viewModel.getDefectData().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            binding.tvdefectcounter.text = data.toString()
        })

        // Pie chart settings
        viewModel.pieChartData.observe(viewLifecycleOwner) { entries ->
            setupPieChart(entries)
        }



        return binding.root
    }
    private fun setupPieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Semi-Circle Pie Chart")
        dataSet.colors = mutableListOf(Color.BLUE, Color.GREEN, Color.RED)
        dataSet.valueTextSize = 12f

        val data = PieData(dataSet)
        pieChart.data = data



        pieChart.invalidate()
    }


}
