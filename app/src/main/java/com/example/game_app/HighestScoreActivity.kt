package com.example.game_app
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
class HighestScoreActivity : AppCompatActivity() {
    private lateinit var againBt : ImageView
    private lateinit var homeBt : ImageView
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highest_score)

        // Initialize TextView
        val highestScoreTextView = findViewById<TextView>(R.id.highestScoreTextView)

        // Retrieve the highest score from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val highestScore = sharedPreferences.getInt("highestScore", 0)

        // Set the highest score to the TextView
        highestScoreTextView.text = "Highest Score: $highestScore"

        againBt = findViewById(R.id.againBt)
        againBt.setOnClickListener{
            startActivity(Intent(this@HighestScoreActivity,GameLevelsActivity::class.java))
        }

        homeBt = findViewById(R.id.homeBt)
        homeBt.setOnClickListener{
            startActivity(Intent(this@HighestScoreActivity,PlayBtnActivity::class.java))
        }
    }
}