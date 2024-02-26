package com.example.pantomonitor.view

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pantomonitor.databinding.FragmentHomeBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat


class HomeFrag : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: BdMainViewModel
    private lateinit var pieChart: PieChart





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)  // Inflate using ViewBinding
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)
        pieChart = binding.pieChart

        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        val legend = pieChart.legend
        legend.isEnabled = false

        pieChart.isRotationEnabled = false
        pieChart.minAngleForSlices = 10f
        pieChart.holeRadius = 60f
        pieChart.setTransparentCircleAlpha(255)






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

        //TOTAL
        viewModel.gettotalcounter().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            binding.totalnumEntries.text = data.toString()
        })


        // Pie chart settings
        viewModel.pieChartData.observe(viewLifecycleOwner) { entries ->
            setupPieChart(entries)
        }


        viewModel.gettrainno().observe(viewLifecycleOwner) { data ->
            binding.Trainno.text = data.toString()
        }

        viewModel.getcartno().observe(viewLifecycleOwner) { data ->
            binding.cartno.text = data.toString()
        }

        viewModel.getlatestStatus().observe(viewLifecycleOwner) { data ->
            binding.statusview.text = data.toString()
        }

        viewModel.getlatestDate().observe(viewLifecycleOwner) { data ->

            val date = data.toLong() * 1000L
            val dateFormat = SimpleDateFormat("MM-dd-yyyy")
            binding.dateview.text = dateFormat.format(date)
        }
        viewModel.getlatestTime().observe(viewLifecycleOwner) { data ->
            binding.timeview.text = data.toString()
        }
        viewModel.getlatestImg().observe(viewLifecycleOwner) { data ->
            var imageRef = viewModel.getlatestpic(data)
            imageRef.downloadUrl.addOnSuccessListener { uri: Uri? ->
                // Load the image into an ImageView using a library like Picasso or Glide
                Picasso.get().load(uri).into(binding.imageView)


            }.addOnFailureListener { exception: Exception? -> }

        }

            return binding.root
    }


    private fun setupPieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Daily Assessment")
        dataSet.colors = mutableListOf(Color.rgb(241, 201, 59), Color.rgb(159, 187, 115))
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 5f
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }




}



