package com.example.sudokugame

data class Game(
    val board: Array<IntArray>, // Example for a Sudoku board
    val score: Int,
    val timeElapsed: Long
)