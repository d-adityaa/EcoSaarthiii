package com.example.ecosaarthi

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class OfflineActivity : AppCompatActivity() {

    lateinit var questionText: TextView
    lateinit var yesBtn: Button
    lateinit var noBtn: Button
    lateinit var resultText: TextView

    var currentQuestionIndex = 0

    val regionFiles = listOf(
        "central",
        "bastar",
        "dhamtari",
        "gariaband",
        "kawardha",
        "korba_raigarh",
        "northernhills_cg",
        "northern_extension"
    )

    val questionsMap = mutableMapOf<String, MutableList<String>>()
    val scores = mutableMapOf<String, Int>()

    val allQuestions = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)

        questionText = findViewById(R.id.questionText)
        yesBtn = findViewById(R.id.option1)
        noBtn = findViewById(R.id.option2)
        resultText = findViewById(R.id.resultText)

        loadAllJSON()
        showQuestion()

        yesBtn.setOnClickListener {
            updateScores(true)
            checkResultOrNext()
        }

        noBtn.setOnClickListener {
            checkResultOrNext()
        }
    }

    fun loadAllJSON() {
        for (region in regionFiles) {

            val resId = resources.getIdentifier(region, "raw", packageName)
            if (resId == 0) continue

            val jsonString = resources.openRawResource(resId)
                .bufferedReader().use { it.readText() }

            val json = JSONObject(jsonString)
            val arr = json.getJSONArray("questions")

            val list = mutableListOf<String>()

            for (i in 0 until arr.length()) {
                val q = arr.getJSONObject(i).getString("question")
                list.add(q)

                if (!allQuestions.contains(q)) {
                    allQuestions.add(q)
                }
            }

            questionsMap[region] = list
            scores[region] = 0
        }

        allQuestions.shuffle() // 🔥 random questions
    }

    fun showQuestion() {
        if (currentQuestionIndex < allQuestions.size) {
            questionText.text = allQuestions[currentQuestionIndex]
        } else {
            showFinalResult()
        }
    }

    fun updateScores(answerYes: Boolean) {
        if (!answerYes) return

        val currentQ = allQuestions[currentQuestionIndex]

        for ((region, questions) in questionsMap) {
            if (questions.contains(currentQ)) {
                scores[region] = scores[region]!! + 1
            }
        }
    }

    fun checkResultOrNext() {

        val sorted = scores.entries.sortedByDescending { it.value }

        if (sorted.size > 1) {
            val top = sorted[0]
            val second = sorted[1]

            // 🔥 SMART DECISION
            if (top.value >= 3 && top.value - second.value >= 2) {
                showFinalResult()
                return
            }
        }

        currentQuestionIndex++
        showQuestion()
    }

    fun showFinalResult() {

        val bestRegion = scores.maxByOrNull { it.value }?.key ?: "central"

        val result = when (bestRegion) {

            "bastar" -> """
📍 Location: Bastar, Chhattisgarh (Approximation)

🌿 Speciality:
Dense sal forests, tribal culture

🏛 History:
Ancient tribal heritage (Gond, Maria)

🐅 Wildlife:
Tiger, bison, wild boar

🏥 Medical:
Limited access

⚠ Do:
• Travel in groups
• Carry supplies

❌ Don’t:
• Avoid deep forest alone
"""

            "kawardha" -> """
📍 Location: Maikal Hills, Kawardha (Approximation)

🌿 Speciality:
Hill forests, bamboo

🏛 History:
Baiga tribe region

🐅 Wildlife:
Deer, bison

🏥 Medical:
Basic facilities

⚠ Do:
• Respect tribal zones

❌ Don’t:
• Avoid disturbing nature
"""

            "gariaband" -> """
📍 Location: Gariaband Region (Approximation)

🌿 Speciality:
Forest + agriculture mix

🏛 History:
Tribal-farming culture

🐒 Wildlife:
Monkeys, peafowl

🏥 Medical:
Moderate

⚠ Do:
• Stay alert near forests

❌ Don’t:
• Avoid isolated zones
"""

            "dhamtari" -> """
📍 Location: Dhamtari–Kanker Belt (Approximation)

🌿 Speciality:
Transition zone

🏛 History:
Mixed tribal culture

🐅 Wildlife:
Deer, birds

🏥 Medical:
Moderate

⚠ Do:
• Follow local routes

❌ Don’t:
• Avoid night travel
"""

            else -> """
📍 Location: Central Plains (Raipur, Durg) (Approximation)

🌿 Speciality:
Agriculture

🏛 History:
Cultural center

🐄 Wildlife:
Cattle

🏥 Medical:
Good hospitals

⚠ Do:
• Stay hydrated

❌ Don’t:
• Avoid unknown areas
"""
        }

        questionText.text = "Analysis Complete ✅"
        yesBtn.visibility = Button.GONE
        noBtn.visibility = Button.GONE

        resultText.text = result
    }
}