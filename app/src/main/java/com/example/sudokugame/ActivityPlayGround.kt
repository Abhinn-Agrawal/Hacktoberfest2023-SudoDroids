package com.example.sudokugame

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth

class ActivityPlayGround : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val gamePlayViewModel: GamePlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_play_ground)

        // Initialize UI components
        val scoreTextView: TextView = findViewById(R.id.scoreTextView)
        val boardView: BoardSudoku = findViewById(R.id.boardSudoku)

        // Observe game data and update UI
        gamePlayViewModel.gameData.observe(this, Observer { game ->
            updateScore(scoreTextView, game.score)
            boardView.setBoard(game.board)
        })

        // Initialize game data if not already set
        if (gamePlayViewModel.gameData.value == null) {
            val initialGame = Game(
                board = arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 0, 0), intArrayOf(0, 0, 0)),
                score = 0,
                timeElapsed = 0L
            )
            gamePlayViewModel.updateGameData(initialGame)
        }
    }

    private fun updateScore(scoreTextView: TextView, score: Int) {
        scoreTextView.text = "Score: $score"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        auth.signOut()
                        auth.addAuthStateListener {
                            if (!isFinishing) {
                                if (auth.currentUser == null) {
                                    dialog.dismiss()
                                    val intent = Intent(this, ActivityLogin::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Something went wrong")
                                        .setPositiveButton("Ok") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .create()
                                        .show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}