package com.ma.tehro.feature.map.city

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.ma.tehro.common.toFarsiNumber
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import kotlin.math.min

class StationInfoWindow(
    mapView: MapView,
    private val stationNameFa: String,
    lines: List<Int>,
) : InfoWindow(createContentView(mapView.context, lines), mapView) {

    override fun onOpen(item: Any) {
        val container = mView as? LinearLayout ?: return

        val nameFaView = container.findViewWithTag<TextView>("name_fa")

        nameFaView.text = stationNameFa

        val lp = container.layoutParams
        if (lp is ViewGroup.MarginLayoutParams) {
            lp.bottomMargin = 20
            container.layoutParams = lp
        }

        container.setOnClickListener {
            close()
        }

        container.scaleX = 0.72f
        container.scaleY = 0.72f
        container.alpha = 0f
        container.translationY = 18f

        val scaleX = ObjectAnimator.ofFloat(container, View.SCALE_X, 0.72f, 1f)
        val scaleY = ObjectAnimator.ofFloat(container, View.SCALE_Y, 0.72f, 1f)
        val alpha = ObjectAnimator.ofFloat(container, View.ALPHA, 0f, 1f)
        val translation = ObjectAnimator.ofFloat(container, View.TRANSLATION_Y, 18f, 0f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha, translation)
            duration = 220L
            interpolator = DecelerateInterpolator(1.6f)
            start()
        }
    }

    override fun onClose() {}

    companion object {
        fun createContentView(context: Context, lines: List<Int>): View {
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(20, 10, 20, 14)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                background = object : Drawable() {
                    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.FILL
                        color = Color.WHITE
                    }
                    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        color = Color.BLACK
                        strokeWidth = 3.5f
                        strokeJoin = Paint.Join.ROUND
                        strokeCap = Paint.Cap.ROUND
                    }
                    private val path = Path()
                    private val rect = RectF()

                    override fun draw(canvas: Canvas) {
                        val left = bounds.left.toFloat()
                        val top = bounds.top.toFloat()
                        val right = bounds.right.toFloat()
                        val bottom = bounds.bottom.toFloat()
                        val width = bounds.width().toFloat()
                        val height = bounds.height().toFloat()
                        if (width <= 0f || height <= 0f) return
                        val cx = bounds.exactCenterX()
                        val cornerRadius = min(width, height) * 0.5f
                        val padding = 6f

                        rect.set(
                            left + padding,
                            top + padding,
                            right - padding,
                            bottom - padding - 12f
                        )

                        path.reset()
                        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
                        canvas.drawPath(path, fillPaint)
                        canvas.drawPath(path, strokePaint)

                        val dotRadius = 8f
                        val dotX = cx
                        val dotY = bottom - 4f

                        path.reset()
                        path.addCircle(dotX, dotY, dotRadius, Path.Direction.CW)
                        canvas.drawPath(path, fillPaint)
                        canvas.drawPath(path, strokePaint)

                        path.reset()
                        path.moveTo(cx - 6f, bottom - 6f)
                        path.lineTo(dotX, dotY - dotRadius * 0.5f)
                        canvas.drawPath(path, strokePaint)
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
                }
                pivotX = 0.5f
                pivotY = 1f
            }

            val nameFaView = TextView(context).apply {
                tag = "name_fa"
                textSize = 15f
                setTextColor(Color.BLACK)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                gravity = Gravity.CENTER
                includeFontPadding = false
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val lineNumbers = lines.map { it.toFarsiNumber() }
            val linesText = when (lineNumbers.size) {
                1 -> "خط ${lineNumbers[0]}"
                2 -> "خط ${lineNumbers[0]} و ${lineNumbers[1]}"
                else -> {
                    val last = lineNumbers.last()
                    val rest = lineNumbers.dropLast(1).joinToString("، ")
                    "خط $rest و $last"
                }
            }

            val linesView = TextView(context).apply {
                text = linesText
                textSize = 10f
                setTextColor(Color.rgb(120, 120, 120))
                gravity = Gravity.CENTER
                includeFontPadding = false
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            container.addView(nameFaView)
            container.addView(linesView)

            return container
        }
    }
}