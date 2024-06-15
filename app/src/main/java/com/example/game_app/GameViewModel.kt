package com.example.game_app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    val swapCountLiveData = MutableLiveData<Int>()

    init {
        // Initialize swap count to 10
        swapCountLiveData.value = 10
    }

    fun decrementSwapCount() {
        val currentCount = swapCountLiveData.value ?: return
        swapCountLiveData.value = currentCount - 1
    }
}
