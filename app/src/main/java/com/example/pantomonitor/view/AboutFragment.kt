package com.example.pantomonitor.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.example.pantomonitor.databinding.FragmentAboutBinding

import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy


class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: BdMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(inflater, container, false)  // Inflate using ViewBinding
        //viewModel = ViewModelProvider(this).get(BdMainViewModel::class.java)
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)



        return binding.root
    }














}
