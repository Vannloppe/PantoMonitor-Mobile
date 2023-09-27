package com.example.pantomonitor.view
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.FragmentHomeBinding
import com.example.pantomonitor.databinding.FragmentTimelineBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class TimelineFragment : Fragment() {

    private lateinit var binding: FragmentTimelineBinding
    private lateinit var viewModel: BdMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTimelineBinding.inflate(inflater, container, false)  // Inflate using ViewBinding
        //viewModel = ViewModelProvider(this).get(BdMainViewModel::class.java)
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)


        viewModel.getGoodData().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getDefectData().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarjan.max = total
                binding.progressBarjan.progress = data.toInt()
                binding.defectcounterjan.text = data2.toString()
                binding.goodcounterjan.text = data.toString()
                binding.jantotal.text = total.toString()



            })
        })











        return binding.root
    }














    }

