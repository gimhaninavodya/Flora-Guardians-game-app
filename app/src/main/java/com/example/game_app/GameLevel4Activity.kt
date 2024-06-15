package com.example.game_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlin.math.abs

@Suppress("DEPRECATION")
class GameLevel4Activity : AppCompatActivity() {
    private var symbols = intArrayOf(
        R.drawable.sm1,
        R.drawable.sm2,
        R.drawable.sm3,
        R.drawable.sm4,
        R.drawable.sm5,
        R.drawable.sm6,
    )

    private var widthOfBlock: Int = 0
    private var noOfBlock: Int = 8
    private var widthOfScreen: Int = 0
    private lateinit var symbol: ArrayList<ImageView>
    private var symbolToBeDragged: Int = 0
    private var symbolToBeReplaced: Int = 0
    private var notSymbol: Int = R.drawable.transparent
    private lateinit var mHandler: Handler
    private lateinit var scoreResult: TextView
    private lateinit var swapCountTextView: TextView
    private var score = 0
    var interval = 100L
    private var userSwap = false // Flag to track user swap
    private var symCount1 = 0
    private var symCount2 = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var swapCountViewModel: GameViewModel
    private var remainingSwapCount: Int = 0 // Variable to store remaining swap count

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_level4)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Set initial remaining swap count
        val initialSwapCount = 0
        remainingSwapCount = sharedPreferences.getInt("remainingSwapCount", initialSwapCount)

        // Initialize ViewModel
        swapCountViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        // Override swap count to 20 for level 4
        swapCountViewModel.swapCountLiveData.value = 20

        // Observe swap count LiveData
        swapCountViewModel.swapCountLiveData.observe(this) { count ->
            // Update UI with new swap count
            swapCountTextView.text = "Count : $count"
            remainingSwapCount = count // Update remaining swap count
            if (count == 0) {
                swapCountTextView.text = "Count : 0"
                // Delay for 1000 milliseconds (1 second)
                Handler().postDelayed({
                    navigateToScoreActivity() // Function to navigate after the swap count is zero
                }, 1000)
            }
        }

        // Load the highest score from SharedPreferences
        sharedPreferences.getInt("highestScore", 0)

        // Initialize the scoreResult TextView
        scoreResult = findViewById(R.id.score)
        score = 0  // Initialize score to 0

        // Initialize the swapCountTextView
        swapCountTextView = findViewById(R.id.swapCount)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        widthOfScreen = displayMetrics.widthPixels
        widthOfBlock = widthOfScreen / noOfBlock

        symbol = ArrayList()
        createBoard()

        // Set up touch listeners for symbols after initializing the score to 0
        for (i in symbol.indices) {
            symbol[i].setOnTouchListener(SwipeListener(i))
        }

        mHandler = Handler()
        startReport()
    }

    private fun symbolInterChange() {
        if (symbolToBeReplaced in symbol.indices && symbolToBeDragged in symbol.indices) {
            val background: Int = symbol[symbolToBeReplaced].tag as Int
            val background1: Int = symbol[symbolToBeDragged].tag as Int

            symbol[symbolToBeDragged].setImageResource(background)
            symbol[symbolToBeReplaced].setImageResource(background1)

            symbol[symbolToBeDragged].tag = background
            symbol[symbolToBeReplaced].tag = background1

            userSwap = true // Set userSwap flag to true when symbols are swapped
            // Decrement swap count in ViewModel
            swapCountViewModel.decrementSwapCount()
        }
    }

    private fun checkForMatches() {
        if (userSwap) { // Check if the match was due to user swap
            var matched: Boolean  // Flag to track if any matches are found
            do {
                matched = checkHorizontalMatches() || checkVerticalMatches()
            } while (matched)
            userSwap = false // Reset the flag after checking matches

            // Check game over condition
            if (symCount1 >= 15 && symCount2 >= 20) {
                navigateToScoreActivity()
            }
        }
    }

    private fun checkHorizontalMatches(): Boolean {
        var matched = false
        for (row in 0 until noOfBlock) {
            var count = 1
            var previousSymbol = symbol[row * noOfBlock].tag as Int
            for (col in 1 until noOfBlock) {
                val currentSymbol = symbol[row * noOfBlock + col].tag as Int
                if (previousSymbol == currentSymbol) {
                    count++
                } else {
                    if (count >= 3) {
                        score += calculateScore(count, previousSymbol)
                        clearSymbols(row, col - count, row, col - 1)
                        matched = true
                    }
                    count = 1
                    previousSymbol = currentSymbol
                }
            }
            if (count >= 3) {
                score += calculateScore(count, previousSymbol)
                clearSymbols(row, noOfBlock - count, row, noOfBlock - 1)
                matched = true
            }
        }
        return matched
    }

    private fun checkVerticalMatches(): Boolean {
        var matched = false
        for (col in 0 until noOfBlock) {
            var count = 1
            var previousSymbol = symbol[col].tag as Int
            for (row in 1 until noOfBlock) {
                val currentSymbol = symbol[row * noOfBlock + col].tag as Int
                if (previousSymbol == currentSymbol) {
                    count++
                } else {
                    if (count >= 3) {
                        score += calculateScore(count, previousSymbol)
                        clearSymbols(row - count, col, row - 1, col)
                        matched = true
                    }
                    count = 1
                    previousSymbol = currentSymbol
                }
            }
            if (count >= 3) {
                score += calculateScore(count, previousSymbol)
                clearSymbols(noOfBlock - count, col, noOfBlock - 1, col)
                matched = true
            }
        }
        return matched
    }


    private fun calculateScore(count: Int, symbol: Int): Int {
        // Adjust score calculation based on symbol count
        val baseScore = when (symbol) {
            R.drawable.sm2 -> 1
            R.drawable.sm5 -> 1
            R.drawable.sm1 -> 1
            R.drawable.sm3 -> 1
            R.drawable.sm4 -> 1
            R.drawable.sm6 -> 1
            else -> 0
        }
        return when (count) {
            in 3..4 -> baseScore * 10
            in 5..7 -> baseScore * 20
            8 -> baseScore * 25
            else -> 0
        }
    }


    private fun clearSymbols(startRow: Int, startCol: Int, endRow: Int, endCol: Int) {
        for (row in startRow..endRow) {
            for (col in startCol..endCol) {
                val currentSymbol = symbol[row * noOfBlock + col].tag as Int
                if (currentSymbol == R.drawable.sm4) {
                    symCount1++
                    updateSymCountTextView(symCount1, R.id.symCount1)
                } else if (currentSymbol == R.drawable.sm6) {
                    symCount2++
                    updateSymCountTextView(symCount2, R.id.symCount2)
                }
                symbol[row * noOfBlock + col].setImageResource(notSymbol)
                symbol[row * noOfBlock + col].tag = notSymbol
            }
        }
        moveDownSymbols()
    }

    @SuppressLint("SetTextI18n")
    private fun updateSymCountTextView(count: Int, textViewId: Int) {
        val textView = findViewById<TextView>(textViewId)
        textView.text = "$count"
    }



    @SuppressLint("SetTextI18n")
    private fun moveDownSymbols() {
        for (col in 0 until noOfBlock) {
            var emptySpaces = 0
            for (row in noOfBlock - 1 downTo 0) {
                val currentIndex = row * noOfBlock + col
                if (symbol[currentIndex].tag == notSymbol) {
                    emptySpaces++
                } else if (emptySpaces > 0) {
                    val newIndex = currentIndex + emptySpaces * noOfBlock
                    symbol[newIndex].setImageResource(symbol[currentIndex].tag as Int)
                    symbol[newIndex].tag = symbol[currentIndex].tag
                    symbol[currentIndex].setImageResource(notSymbol)
                    symbol[currentIndex].tag = notSymbol
                }
            }
            for (row in 0 until emptySpaces) {
                val randomSymbol = symbols.random()
                symbol[row * noOfBlock + col].setImageResource(randomSymbol)
                symbol[row * noOfBlock + col].tag = randomSymbol
            }
        }
        scoreResult.text = "Score : $score"
    }

    private val reportRunnable = object : Runnable {
        override fun run() {
            checkForMatches()
            mHandler.postDelayed(this, interval)
        }
    }

    private fun startReport() {
        mHandler.postDelayed(reportRunnable, interval)
    }

    private fun createBoard() {
        val gridLayout = findViewById<GridLayout>(R.id.board)
        gridLayout.rowCount = noOfBlock
        gridLayout.columnCount = noOfBlock

        // Initialize a temporary list to hold the symbols for board creation
        val tempSymbols = mutableListOf<Int>()

        // Fill the temporary list with symbols ensuring no initial matches
        for (i in 0 until noOfBlock * noOfBlock) {
            var symbolToAdd: Int
            do {
                symbolToAdd = symbols.random()
            } while (isMatchedSymbol(tempSymbols, symbolToAdd))
            tempSymbols.add(symbolToAdd)
        }

        // Create the board using symbols from the temporary list
        for (i in 0 until noOfBlock * noOfBlock) {
            val imageView = ImageView(this)
            imageView.layoutParams = GridLayout.LayoutParams().apply {
                width = widthOfBlock
                height = widthOfBlock
            }

            imageView.setImageResource(tempSymbols[i])
            imageView.tag = tempSymbols[i]
            symbol.add(imageView)
            gridLayout.addView(imageView)
        }
    }

    private fun isMatchedSymbol(symbolList: List<Int>, symbolToAdd: Int): Boolean {
        // Check if adding the new symbol will result in a match
        val size = symbolList.size
        if (size >= 2) {
            if (size % noOfBlock >= 2 && symbolList[size - 1] == symbolToAdd &&
                symbolList[size - 2] == symbolToAdd
            ) {
                return true // Horizontal match
            }
            if (size >= 2 * noOfBlock && symbolList[size - noOfBlock] == symbolToAdd &&
                symbolList[size - 2 * noOfBlock] == symbolToAdd
            ) {
                return true // Vertical match
            }
        }
        return false
    }

    private inner class SwipeListener(val index: Int) : View.OnTouchListener {
        private var startX = 0f
        private var startY = 0f

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val endY = event.y
                    val dx = endX - startX
                    val dy = endY - startY
                    if (abs(dx) > abs(dy)) {
                        if (dx > 0) {
                            // Swipe right
                            symbolToBeDragged = index
                            symbolToBeReplaced = index + 1
                        } else {
                            // Swipe left
                            symbolToBeDragged = index
                            symbolToBeReplaced = index - 1
                        }
                    } else {
                        if (dy > 0) {
                            // Swipe down
                            symbolToBeDragged = index
                            symbolToBeReplaced = index + noOfBlock
                        } else {
                            // Swipe up
                            symbolToBeDragged = index
                            symbolToBeReplaced = index - noOfBlock
                        }
                    }
                    symbolInterChange()
                    return true
                }
            }
            return false
        }
    }

    private fun navigateToScoreActivity() {
        val additionalScore = remainingSwapCount
        score += additionalScore

        // Save the current score to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putInt("currentScore", score)

        // Total score calculation
        val totalScore = sharedPreferences.getInt("totalScore", 0)
        val updatedTotalScore = totalScore + score
        editor.putInt("totalScore", updatedTotalScore)

        editor.apply()

        // Check if the current score is the highest score
        val highestScore = sharedPreferences.getInt("highestScore", 0)

        if (score > highestScore) {
            // Check if both symCount1 and symCount2 are less than 15 and 20
            if (symCount1 < 15 || symCount2 < 20) {
                // If so, navigate to GameTryAgainActivity
                startActivity(Intent(this, GameTryAgainActivity::class.java))
            } else {
                // If so, update the highest score in SharedPreferences
                editor.putInt("highestScore", score)
                editor.apply()
                // Navigate to HighestScoreActivity
                startActivity(Intent(this, HighestScoreActivity::class.java))
            }
        } else {
            // Check if both symCount1 and symCount2 are less than 15 and 20
            if (symCount1 < 15 || symCount2 < 20) {
                // If so, navigate to GameTryAgainActivity
                startActivity(Intent(this, GameTryAgainActivity::class.java))
            } else {
                // Pass the current score to ScoreActivity
                val intent = Intent(this, ScoreActivity::class.java)
                intent.putExtra("currentScore", score)
                startActivity(intent)
            }
        }
    }
}