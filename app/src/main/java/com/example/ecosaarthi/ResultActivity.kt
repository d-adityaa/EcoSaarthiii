package com.example.ecosaarthi

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val dataString = intent.getStringExtra("data")

        if (dataString == null) return

        val json = JSONObject(dataString)

        val summary = findViewById<TextView>(R.id.summaryText)
        val terrain = findViewById<TextView>(R.id.terrainText)
        val wildlife = findViewById<TextView>(R.id.wildlifeText)
        val dos = findViewById<TextView>(R.id.dosText)
        val donts = findViewById<TextView>(R.id.dontsText)
        val culture = findViewById<TextView>(R.id.cultureText)
        val helpline = findViewById<TextView>(R.id.helplineText)
        val connectivity = findViewById<TextView>(R.id.connectivityText)
        val medical = findViewById<TextView>(R.id.medicalText)
        val survival = findViewById<TextView>(R.id.survivalText)
        val map = findViewById<TextView>(R.id.mapText)
        val safety = findViewById<TextView>(R.id.safetyText)

        summary.text = "🌄 Summary:\n" + json.getString("summary")
        terrain.text = "🌿 Terrain:\n" + json.getString("terrain")
        wildlife.text = "🐾 Wildlife:\n" + json.getString("wildlife")

        val dosArray = json.getJSONArray("dos")
        val dosTextBuilder = StringBuilder()
        for (i in 0 until dosArray.length()) {
            dosTextBuilder.append("✔ ").append(dosArray.getString(i)).append("\n")
        }
        dos.text = "✅ DO's:\n$dosTextBuilder"

        val dontsArray = json.getJSONArray("donts")
        val dontsTextBuilder = StringBuilder()
        for (i in 0 until dontsArray.length()) {
            dontsTextBuilder.append("❌ ").append(dontsArray.getString(i)).append("\n")
        }
        donts.text = "❌ DON'Ts:\n$dontsTextBuilder"

        culture.text = "🏕 Culture:\n" + json.getString("culture")
        helpline.text = "📞 Helpline:\n" + json.getString("helpline")
        connectivity.text = "🛣 Connectivity:\n" + json.getString("connectivity")
        medical.text = "🏥 Medical:\n" + json.getString("medical")
        survival.text = "🧠 Survival Tips:\n" + json.getString("survival")
        map.text = "🗺 Map:\n" + json.getString("map")
        safety.text = "⭐ Safety Score:\n" + json.getString("safety")
    }
}