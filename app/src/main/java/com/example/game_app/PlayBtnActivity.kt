package com.example.game_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayBtnActivity : AppCompatActivity() {
    private lateinit var playBt : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_btn)

        supportActionBar?.hide()
        playBt = findViewById(R.id.playBt)
        playBt.setOnClickListener{
            startActivity(Intent(this@PlayBtnActivity,GameLevelsActivity::class.java))
        }

        // Initialize TextView
        val totalScoreTextView = findViewById<TextView>(R.id.totalScore)

        // Retrieve the total score from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val totalScore = sharedPreferences.getInt("totalScore", 0)

        // Set the total score to the TextView
        totalScoreTextView.text = "$totalScore"
    }
}