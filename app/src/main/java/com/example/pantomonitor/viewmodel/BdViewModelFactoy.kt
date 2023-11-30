package com.example.pantomonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BdViewModelFactoy : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BdMainViewModel::class.java)) {
                return BdMainViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


