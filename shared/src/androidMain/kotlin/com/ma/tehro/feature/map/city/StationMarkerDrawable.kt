package com.ma.tehro.feature.map.city

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import com.ma.tehro.common.COLOR_LINE_1
import com.ma.tehro.common.COLOR_LINE_2
import com.ma.tehro.common.COLOR_LINE_3
import com.ma.tehro.common.COLOR_LINE_4
import com.ma.tehro.common.COLOR_LINE_5
import com.ma.tehro.common.COLOR_LINE_6
import com.ma.tehro.common.COLOR_LINE_7
import kotlin.math.min

class StationMarkerDrawable(
    private val lineNumbers: List<Int>,
) : Drawable() {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = android.graphics.Color.BLACK
        strokeWidth = 4f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val outerPath = Path()

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()

        if (width <= 0f || height <= 0f) return

        val cx = bounds.exactCenterX()
        val radius = min(width / 2f, height * 0.40f)
        val cy = bounds.top + radius + 2f

        val bodyBottom = cy + radius
        val needleStartY = bodyBottom - radius * 0.10f
        val tipY = bounds.bottom - 2f
        val neckWidth = radius * 0.12f

        outerPath.reset()
        outerPath.moveTo(cx - radius, cy)

        outerPath.cubicTo(
            cx - radius,
            cy - radius * 0.55f,
            cx - radius * 0.55f,
            cy - radius,
            cx,
            cy - radius
        )

        outerPath.cubicTo(
            cx + radius * 0.55f,
            cy - radius,
            cx + radius,
            cy - radius * 0.55f,
            cx + radius,
            cy
        )

        outerPath.cubicTo(
            cx + radius,
            cy + radius * 0.55f,
            cx + radius * 0.55f,
            cy + radius * 0.90f,
            cx + radius * 0.45f,
            needleStartY
        )

        outerPath.cubicTo(
            cx + neckWidth * 0.8f,
            needleStartY + radius * 0.15f,
            cx + neckWidth * 0.3f,
            tipY - radius * 0.3f,
            cx,
            tipY
        )

        outerPath.cubicTo(
            cx - neckWidth * 0.3f,
            tipY - radius * 0.3f,
            cx - neckWidth * 0.8f,
            needleStartY + radius * 0.15f,
            cx - radius * 0.45f,
            needleStartY
        )

        outerPath.cubicTo(
            cx - radius * 0.55f,
            cy + radius * 0.90f,
            cx - radius,
            cy + radius * 0.55f,
            cx - radius,
            cy
        )

        outerPath.close()

        val colors = getColors(lineNumbers)

        when (colors.size) {
            0 -> {
                fillPaint.color = android.graphics.Color.GRAY
                canvas.drawPath(outerPath, fillPaint)
            }
            1 -> {
                fillPaint.color = colors.first()
                canvas.drawPath(outerPath, fillPaint)
            }
            2 -> {
                fillPaint.color = colors.last()
                canvas.drawPath(outerPath, fillPaint)

                val clipRect = Rect(
                    bounds.left,
                    bounds.top,
                    cx.toInt(),
                    bounds.bottom
                )
                canvas.save()
                canvas.clipRect(clipRect)
                fillPaint.color = colors.first()
                canvas.drawPath(outerPath, fillPaint)
                canvas.restore()
            }
            else -> {
                val gradient = LinearGradient(
                    cx - radius, 0f,
                    cx + radius, 0f,
                    colors.first(),
                    colors.last(),
                    Shader.TileMode.CLAMP
                )
                fillPaint.shader = gradient
                canvas.drawPath(outerPath, fillPaint)
                fillPaint.shader = null
            }
        }

        canvas.drawPath(outerPath, strokePaint)
    }

    private fun getColors(lineNumbers: List<Int>): List<Int> {
        return lineNumbers
            .distinct()
            .mapNotNull { lineNumber ->
                getLineColor(lineNumber)
            }
    }

    private fun getLineColor(lineNumber: Int): Int? {
        return when (lineNumber) {
            1 -> COLOR_LINE_1.toInt()
            2 -> COLOR_LINE_2.toInt()
            3 -> COLOR_LINE_3.toInt()
            4 -> COLOR_LINE_4.toInt()
            5 -> COLOR_LINE_5.toInt()
            6 -> COLOR_LINE_6.toInt()
            7 -> COLOR_LINE_7.toInt()
            else -> null
        }
    }

    override fun setAlpha(alpha: Int) {
        fillPaint.alpha = alpha
        strokePaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
        fillPaint.colorFilter = colorFilter
        strokePaint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = 62
    override fun getIntrinsicHeight(): Int = 78
}