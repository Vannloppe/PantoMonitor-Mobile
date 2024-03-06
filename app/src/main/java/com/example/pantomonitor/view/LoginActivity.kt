package com.example.pantomonitor.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pantomonitor.databinding.ActivityLoginBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseemu: Firebase




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // databaseemu.auth.useEmulator("10.0.2.2", 9000)
        firebaseAuth = FirebaseAuth.getInstance()


        binding.RegTextBut.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.SignIn.setOnClickListener {

            val email = binding.UserEditText.text.toString()
            val pass = binding.PassTextTextPassword.text.toString()


            if ( email.isNotEmpty() && pass.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Login Error! Check your email or password", Toast.LENGTH_LONG).show()

                        }
                    }
              } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_LONG).show()

            }
        }

    }
}