package com.example.game_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class GameActivity : AppCompatActivity() {
    private lateinit var welcomePic : ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        welcomePic = findViewById(R.id.welcomePic)

        welcomePic.setOnClickListener{
            val intent = Intent(this@GameActivity,PlayBtnActivity::class.java )
            startActivity(intent)
        }
    }
} 