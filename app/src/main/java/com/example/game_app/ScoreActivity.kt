package com.example.game_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ScoreActivity : AppCompatActivity() {
    private lateinit var againBt : ImageView
    private lateinit var homeBt : ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        // Retrieve the current score from the intent
        val currentScore = intent.getIntExtra("currentScore", 0)

        // Display the current score
        val scoreTextView: TextView = findViewById(R.id.userScoreTextView)
        scoreTextView.text = "Your Score: $currentScore"

        againBt = findViewById(R.id.againBt)
        againBt.setOnClickListener{
            startActivity(Intent(this@ScoreActivity,GameLevelsActivity::class.java))
        }

        homeBt = findViewById(R.id.homeBt)
        homeBt.setOnClickListener{
            startActivity(Intent(this@ScoreActivity,PlayBtnActivity::class.java))
        }
    }
}
