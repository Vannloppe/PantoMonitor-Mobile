package com.example.pantomonitor.utils

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthinit : Initializer<FirebaseAuth> {

    override fun create(context: Context): FirebaseAuth {

        return Firebase.auth
    }

    // No dependencies on other libraries
    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}