package com.example.campuskit.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.campuskit.R

/**
 * Custom view for rendering habit progress grid (GitHub contribution graph style).
 * Displays a 7-column grid showing daily completion status.
 */
class HabitProgressGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cellRect = RectF()
    
    private var cellSize = 0f
    private var cellSpacing = 4f
    private val columns = 7
    private val rows = 5
    
    // Progress data: 0 = no completion, 1-4 = completion intensity
    private var progressData = IntArray(columns * rows) { 0 }
    
    private val emptyColor = ContextCompat.getColor(context, R.color.surface_variant)
    private val accentColor = ContextCompat.getColor(context, R.color.accent_sage)

    init {
        // Generate sample data for demonstration
        generateSampleData()
    }

    private fun generateSampleData() {
        // Fill with some sample progress (for demo purposes)
        for (i in progressData.indices) {
            progressData[i] = when {
                i < 14 -> (0..2).random() // Past 2 weeks with some completion
                i < 21 -> (1..3).random() // More recent with better completion
                else -> 0 // Future days empty
            }
        }
    }

    fun setProgressData(data: IntArray) {
        if (data.size == progressData.size) {
            progressData = data
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        
        // Calculate cell size based on available width
        val availableWidth = width - paddingLeft - paddingRight
        cellSize = (availableWidth - (cellSpacing * (columns - 1))) / columns
        
        val height = (cellSize * rows + cellSpacing * (rows - 1) + paddingTop + paddingBottom).toInt()
        
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        var index = 0
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val x = paddingLeft + col * (cellSize + cellSpacing)
                val y = paddingTop + row * (cellSize + cellSpacing)
                
                cellRect.set(x, y, x + cellSize, y + cellSize)
                
                // Set color based on progress intensity
                val progress = if (index < progressData.size) progressData[index] else 0
                paint.color = when (progress) {
                    0 -> emptyColor
                    1 -> blendColors(emptyColor, accentColor, 0.3f)
                    2 -> blendColors(emptyColor, accentColor, 0.6f)
                    else -> accentColor
                }
                
                // Draw rounded rectangle cell
                val cornerRadius = cellSize * 0.2f
                canvas.drawRoundRect(cellRect, cornerRadius, cornerRadius, paint)
                
                index++
            }
        }
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio
        val r = (android.graphics.Color.red(color1) * inverseRatio + android.graphics.Color.red(color2) * ratio).toInt()
        val g = (android.graphics.Color.green(color1) * inverseRatio + android.graphics.Color.green(color2) * ratio).toInt()
        val b = (android.graphics.Color.blue(color1) * inverseRatio + android.graphics.Color.blue(color2) * ratio).toInt()
        return android.graphics.Color.rgb(r, g, b)
    }
}
