package com.example.pantomonitor.view

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pantomonitor.databinding.FragmentHomeBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel


class HomeFrag : Fragment() {

    private lateinit var binding: FragmentHomeBinding
   private lateinit var viewModel: BdMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)  // Inflate using ViewBinding

        viewModel = ViewModelProvider(this).get(BdMainViewModel::class.java)

        viewModel.getGoodData().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            binding.tvgoodcounter.text = data.toString()
            binding.tvdefectcounter.text = data.toString()
        })
        viewModel.getDefectData().observe(viewLifecycleOwner, Observer { data ->
            // Update your UI with the data from the ViewModel
            binding.tvdefectcounter.text = data.toString()
        })


        return binding.root
    }


}