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
class GameLevel1Activity : AppCompatActivity() {
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
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var swapCountViewModel: GameViewModel

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_level1)

        // Initialize ViewModel
        swapCountViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        // Override swap count to 10 for level 1
        swapCountViewModel.swapCountLiveData.value = 10

        // Observe swap count LiveData
        swapCountViewModel.swapCountLiveData.observe(this) { count ->
            // Update UI with new swap count
            swapCountTextView.text = "Count : $count"
            if (count == 0) {
                swapCountTextView.text = "Count : 0"
                // Delay for 1000 milliseconds (1 second)
                Handler().postDelayed({
                    navigateToScoreActivity() // Function to navigate after the swap count is zero
                }, 1000)
            }
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

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
        startReport() //reporting matches in the game
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
        }
    }

    private fun checkHorizontalMatches(): Boolean {
        var matched = false // Flag to track if any matches are found
        for (row in 0 until noOfBlock) {
            var count = 1
            var previousSymbol = symbol[row * noOfBlock].tag as Int
            for (col in 1 until noOfBlock) {
                val currentSymbol = symbol[row * noOfBlock + col].tag as Int
                if (previousSymbol == currentSymbol) {
                    count++
                } else {
                    if (count >= 3) {
                        score += calculateScore(count)
                        clearSymbols(row, col - count, row, col - 1)
                        matched = true // Set the flag since a match is found
                    }
                    count = 1
                    previousSymbol = currentSymbol
                }
            }
            //to end the loop and check match at the End of the Row
            if (count >= 3) {
                score += calculateScore(count)
                clearSymbols(row, noOfBlock - count, row, noOfBlock - 1)
                matched = true // Set the flag since a match is found
            }
        }
        return matched
    }

    private fun checkVerticalMatches(): Boolean {
        var matched = false // Flag to track if any matches are found
        for (col in 0 until noOfBlock) {
            var count = 1
            var previousSymbol = symbol[col].tag as Int
            for (row in 1 until noOfBlock) {
                val currentSymbol = symbol[row * noOfBlock + col].tag as Int
                if (previousSymbol == currentSymbol) {
                    count++
                } else {
                    if (count >= 3) {
                        score += calculateScore(count)
                        clearSymbols(row - count, col, row - 1, col)
                        matched = true // Set the flag since a match is found
                    }
                    count = 1
                    previousSymbol = currentSymbol
                }
            }
            //to end the loop and check matches at the end of the column
            if (count >= 3) {
                score += calculateScore(count)
                clearSymbols(noOfBlock - count, col, noOfBlock - 1, col)
                matched = true // Set the flag since a match is found
            }
        }
        return matched
    }

    private fun calculateScore(count: Int): Int {
        return when (count) {
            in 3..4 -> 10
            in 5..7 -> 20
            8 -> 25
            else -> 0
        }
    }

    private fun clearSymbols(startRow: Int, startCol: Int, endRow: Int, endCol: Int) {
        for (row in startRow..endRow) {
            for (col in startCol..endCol) {
                symbol[row * noOfBlock + col].setImageResource(notSymbol)
                symbol[row * noOfBlock + col].tag = notSymbol
            }
        }
        moveDownSymbols()
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
            // If so, update the highest score in SharedPreferences
            editor.putInt("highestScore", score)
            editor.apply()

            // Navigate to HighestScoreActivity
            startActivity(Intent(this, HighestScoreActivity::class.java))
        } else {
            // Pass the current score to ScoreActivity
            val intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("currentScore", score)
            startActivity(intent)
        }
    }
}