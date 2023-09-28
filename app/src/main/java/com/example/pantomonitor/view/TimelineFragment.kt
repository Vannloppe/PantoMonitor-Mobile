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




        viewModel.getlatestgoodjan().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadjan().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarjan.max = total
                binding.progressBarjan.progress = data.toInt()
                binding.defectcounterjan.text = data2.toString()
                binding.goodcounterjan.text = data.toString()
                binding.jantotal.text = total.toString()
            })
        })




        viewModel.getlatestgoodfeb().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadfeb().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarfeb.max = total
                binding.progressBarfeb.progress = data.toInt()
                binding.defectcounterfeb.text = data2.toString()
                binding.goodcounterfeb.text = data.toString()
                binding.febtotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodmar().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadmar().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarmar.max = total
                binding.progressBarmar.progress = data.toInt()
                binding.defectcountermar.text = data2.toString()
                binding.goodcountermar.text = data.toString()
                binding.martotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodapr().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadapr().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarapr.max = total
                binding.progressBarapr.progress = data.toInt()
                binding.defectcounterapr.text = data2.toString()
                binding.goodcounterapr.text = data.toString()
                binding.aprtotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodmay().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadmay().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarmay.max = total
                binding.progressBarmay.progress = data.toInt()
                binding.defectcountermay.text = data2.toString()
                binding.goodcountermay.text = data.toString()
                binding.maytotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodjune().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadjune().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarjune.max = total
                binding.progressBarjune.progress = data.toInt()
                binding.defectcounterjune.text = data2.toString()
                binding.goodcounterjune.text = data.toString()
                binding.junetotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodjuly().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadjuly().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarjuly.max = total
                binding.progressBarjuly.progress = data.toInt()
                binding.defectcounterjuly.text = data2.toString()
                binding.goodcounterjuly.text = data.toString()
                binding.julytotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodaug().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadaug().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBaraug.max = total
                binding.progressBaraug.progress = data.toInt()
                binding.defectcounteraug.text = data2.toString()
                binding.goodcounteraug.text = data.toString()
                binding.augtotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodsep().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadsep().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarsept.max = total
                binding.progressBarsept.progress = data.toInt()
                binding.defectcountersept.text = data2.toString()
                binding.goodcountersept.text = data.toString()
                binding.septtotal.text = total.toString()
            })
        })



        viewModel.getlatestgoodoct().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadoct().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBaroct.max = total
                binding.progressBaroct.progress = data.toInt()
                binding.defectcounteroct.text = data2.toString()
                binding.goodcounteroct.text = data.toString()
                binding.octtotal.text = total.toString()
            })

        })




        viewModel.getlatestgoodnov().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbadnov().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBarnov.max = total
                binding.progressBarnov.progress = data.toInt()
                binding.defectcounternov.text = data2.toString()
                binding.goodcounternov.text = data.toString()
                binding.novtotal.text = total.toString()
            })



        })



        viewModel.getlatestgooddec().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            viewModel.getlatestbaddec().observe(viewLifecycleOwner, Observer { data2 ->
                // Update your UI with the data from the ViewModel
                val total = data.toInt() + data2.toInt()
                binding.progressBardec.max = total
                binding.progressBardec.progress = data.toInt()
                binding.defectcounterdec.text = data2.toString()
                binding.goodcounterdec.text = data.toString()
                binding.dectotal.text = total.toString()
            })
        })











        return binding.root
    }














    }

