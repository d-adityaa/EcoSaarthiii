package com.example.ecosaarthi

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CompassActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        val forestGroup = findViewById<RadioGroup>(R.id.forestGroup)
        val tempGroup = findViewById<RadioGroup>(R.id.tempGroup)
        val button = findViewById<Button>(R.id.detectBtn)

        button.setOnClickListener {

            val forestId = forestGroup.checkedRadioButtonId
            val tempId = tempGroup.checkedRadioButtonId

            if (forestId == -1 || tempId == -1) {
                Toast.makeText(this, "Select all options", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val forest = findViewById<RadioButton>(forestId).text.toString()
            val temp = findViewById<RadioButton>(tempId).text.toString()

            val result = when {
                forest == "Dense Forest" && temp == "Hot" ->
                    "Tropical Forest 🌴\n\nDo:\n• Stay hydrated\n• Avoid insects\n\nDon't:\n• Don't go alone"

                forest == "Dry Area" && temp == "Hot" ->
                    "Desert Area 🏜\n\nDo:\n• Carry water\n• Wear light clothes\n\nDon't:\n• Avoid sun exposure"

                else ->
                    "Mountain Region 🏔\n\nDo:\n• Wear warm clothes\n• Check oxygen\n\nDon't:\n• Avoid risky paths"
            }

            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
        }
    }
}