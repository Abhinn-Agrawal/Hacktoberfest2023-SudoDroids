package com.example.sudokugame

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GamePlayViewModel : ViewModel() {
    val gameData = MutableLiveData<Game>()
    val selectedRow = MutableLiveData<Int>()
    val selectedColumn = MutableLiveData<Int>()

    fun updateGameData(newData: Game) {
        gameData.value = newData
    }

    fun selectCell(row: Int, column: Int) {
        selectedRow.value = row
        selectedColumn.value = column
    }
}