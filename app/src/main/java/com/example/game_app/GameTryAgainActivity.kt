package com.example.game_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class GameTryAgainActivity : AppCompatActivity() {
    private lateinit var againBt : ImageView
    private lateinit var homeBt : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_try_again)

        againBt = findViewById(R.id.againBt)
        againBt.setOnClickListener{
            startActivity(Intent(this@GameTryAgainActivity,GameLevelsActivity::class.java))
        }

        homeBt = findViewById(R.id.homeBt)
        homeBt.setOnClickListener{
            startActivity(Intent(this@GameTryAgainActivity,PlayBtnActivity::class.java))
        }
    }
}