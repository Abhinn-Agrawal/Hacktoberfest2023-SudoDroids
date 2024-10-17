package com.example.sudokugame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner

class BoardSudoku(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private var squareSize = 3
    private var innerSqr = 9
    private var cellSize = 0F
    private var selectedRow = -1
    private var selectedColumn = -1
    private lateinit var viewModel: GamePlayViewModel
    private var board: Array<IntArray> = arrayOf()

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 6F
    }

    private val innerPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 3F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }

    fun setViewModel(viewModel: GamePlayViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        viewModel.selectedRow.observe(lifecycleOwner) { row ->
            selectedRow = row ?: -1
            invalidate()
        }
        viewModel.selectedColumn.observe(lifecycleOwner) { column ->
            selectedColumn = column ?: -1
            invalidate()
        }
    }

    fun setBoard(board: Array<IntArray>) {
        this.board = board
        invalidate()  // Redraw the view with the new board
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        cellSize = (width / innerSqr).toFloat()
        fillCells(canvas)
        showLines(canvas)
        drawBoard(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        if (selectedRow == -1 || selectedColumn == -1) return
        for (r in 0 until innerSqr) {
            for (c in 0 until innerSqr) {
                if (r == selectedRow && c == selectedColumn) {
                    fillCell(canvas, r, c, selectedCellPaint)
                } else if (r == selectedRow || c == selectedColumn) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                }
            }
        }
    }

    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(c * cellSize, r * cellSize, (c + 1) * cellSize, (r + 1) * cellSize, paint)
    }

    private fun showLines(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), borderPaint)
        for (i in 1 until innerSqr) {
            val lineToUse = when (i % squareSize) {
                0 -> borderPaint
                else -> innerPaint
            }
            canvas.drawLine(i * cellSize, 0F, i * cellSize, height.toFloat(), lineToUse)
            canvas.drawLine(0F, i * cellSize, width.toFloat(), i * cellSize, lineToUse)
        }
    }

    private fun drawBoard(canvas: Canvas) {
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = cellSize / 2
            textAlign = Paint.Align.CENTER
        }

        for (r in board.indices) {
            for (c in board[r].indices) {
                val value = board[r][c]
                if (value != 0) {
                    canvas.drawText(value.toString(), (c * cellSize) + cellSize / 2, (r * cellSize) + cellSize / 2 + textPaint.textSize / 2, textPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                performClick()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        selectedRow = (y / cellSize).toInt()
        selectedColumn = (x / cellSize).toInt()
        viewModel.selectCell(selectedRow, selectedColumn)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}

