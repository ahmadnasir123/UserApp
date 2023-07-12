package com.sirdev.userapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondScreenActivity : AppCompatActivity() {
    private lateinit var selectedUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_screen)

        val welcomeText: TextView = findViewById(R.id.welcomeText)
        selectedUserName = findViewById(R.id.selectedUserName)
        val chooseUserButton: Button = findViewById(R.id.chooseUserButton)

        val name = intent.getStringExtra("name")
        welcomeText.text = "Welcome, $name!"

        chooseUserButton.setOnClickListener {
            goToThirdScreen()
        }
    }

    private fun goToThirdScreen() {
        // Start ThirdScreenActivity
    }

    // Other methods and logic for Second Screen
}
