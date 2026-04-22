package com.example.ecosaarthi

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class OfflineActivity : AppCompatActivity() {

    private lateinit var stateSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var placeSpinner: Spinner
    private lateinit var showBtn: Button

    private var jsonData: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)

        stateSpinner = findViewById(R.id.stateSpinner)
        districtSpinner = findViewById(R.id.districtSpinner)
        placeSpinner = findViewById(R.id.placeSpinner)
        showBtn = findViewById(R.id.showBtn)

        loadJSON()
        setupStateSpinner()

        showBtn.setOnClickListener {
            val state = stateSpinner.selectedItem.toString()
            val district = districtSpinner.selectedItem.toString()
            val place = placeSpinner.selectedItem.toString()

            val result = findData(state, district, place)

            if (result != null) {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("data", result.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Data Found ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔥 Load JSON
    private fun loadJSON() {
        val inputStream = resources.openRawResource(R.raw.eco_data)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        jsonData = JSONArray(jsonString)
    }

    // 🔥 Setup State Spinner
    private fun setupStateSpinner() {
        val states = mutableSetOf<String>()

        for (i in 0 until jsonData!!.length()) {
            val obj = jsonData!!.getJSONObject(i)
            states.add(obj.getString("state"))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = adapter

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                setupDistrictSpinner(stateSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 🔥 Setup District Spinner
    private fun setupDistrictSpinner(state: String) {
        val districts = mutableSetOf<String>()

        for (i in 0 until jsonData!!.length()) {
            val obj = jsonData!!.getJSONObject(i)
            if (obj.getString("state") == state) {
                districts.add(obj.getString("district"))
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts.toList())
        districtSpinner.adapter = adapter

        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                setupPlaceSpinner(state, districtSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 🔥 Setup Place Spinner
    private fun setupPlaceSpinner(state: String, district: String) {
        val places = mutableSetOf<String>()

        for (i in 0 until jsonData!!.length()) {
            val obj = jsonData!!.getJSONObject(i)
            if (obj.getString("state") == state && obj.getString("district") == district) {
                places.add(obj.getString("place"))
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places.toList())
        placeSpinner.adapter = adapter
    }

    // 🔥 Find Selected Data
    private fun findData(state: String, district: String, place: String): JSONObject? {
        for (i in 0 until jsonData!!.length()) {
            val obj = jsonData!!.getJSONObject(i)
            if (
                obj.getString("state") == state &&
                obj.getString("district") == district &&
                obj.getString("place") == place
            ) {
                return obj
            }
        }
        return null
    }
}