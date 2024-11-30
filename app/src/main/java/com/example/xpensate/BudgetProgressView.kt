package com.example.xpensate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat

class BudgetProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var maxBudget = 10000f
    private var spentAmount = 0f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 30f
        color = Color.parseColor("#2A2D3E")
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 30f
        color = Color.parseColor("#4CAF50")
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private val walletDrawable = ContextCompat.getDrawable(context, R.drawable.wallet)
    private val rect = RectF()

    fun setProgress(spent: Float, total: Float) {
        spentAmount = spent
        maxBudget = total
        progress = ((spent / total).coerceIn(0f, 1f) * 180f)
        Log.d("BudgetProgressView", "Progress set to: $progress")
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val strokePadding = backgroundPaint.strokeWidth / 2
        rect.set(
            paddingLeft + strokePadding,
            paddingTop + strokePadding,
            w - paddingRight - strokePadding,
            h - paddingBottom - strokePadding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width.coerceAtMost(height) / 2f) - backgroundPaint.strokeWidth

        canvas.drawArc(
            rect,
            180f,
            180f,
            false,
            backgroundPaint
        )

        if (progress > 0f) {
            canvas.drawArc(
                rect,
                180f,
                progress,
                false,
                progressPaint
            )
        }

        walletDrawable?.let {
            val iconSize = radius * 0.2f
            val left = centerX - iconSize
            val top = centerY - iconSize
            it.setBounds(
                left.toInt(),
                top.toInt(),
                (left + iconSize * 2).toInt(),
                (top + iconSize * 2).toInt()
            )
            it.draw(canvas)
        }

        val spentText = "Spent ₹${spentAmount.toInt()} of ₹${maxBudget.toInt()}"
        canvas.drawText(
            spentText,
            centerX,
            centerY + radius * 0.4f,
            textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 400
        val desiredHeight = 250

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidth.coerceAtMost(widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeight.coerceAtMost(heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }
}