package com.example.game_app
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
class GameLevelsActivity : AppCompatActivity() {
    private lateinit var level1 : ImageView
    private lateinit var level2 : ImageView
    private lateinit var level3 : ImageView
    private lateinit var level4 : ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_level)

        level1 = findViewById(R.id.level1)
        level1.setOnClickListener{
            startActivity(Intent(this@GameLevelsActivity,GameLevel1Activity::class.java))
        }

        level2 = findViewById(R.id.level2)
        level2.setOnClickListener{
            startActivity(Intent(this@GameLevelsActivity,GameLevel2Activity::class.java))
        }

        level3 = findViewById(R.id.level3)
        level3.setOnClickListener{
            startActivity(Intent(this@GameLevelsActivity,GameLevel3Activity::class.java))
        }

        level4 = findViewById(R.id.level4)
        level4.setOnClickListener{
            startActivity(Intent(this@GameLevelsActivity,GameLevel4Activity::class.java))
        }
    }
}