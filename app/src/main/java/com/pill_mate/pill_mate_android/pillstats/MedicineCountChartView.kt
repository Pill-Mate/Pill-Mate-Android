package com.pill_mate.pill_mate_android.pillstats

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.pill_mate.pill_mate_android.R
import kotlin.math.min

class MedicineCountChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Point(val label: String, val count: Int)

    private var points: List<Point> = emptyList()
    private var showValues = true

    private val density = resources.displayMetrics.density
    private fun dp(value: Float) = value * density
    private fun sp(value: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics
    )

    private val gray1 = ContextCompat.getColor(context, R.color.gray_1)
    private val gray2 = ContextCompat.getColor(context, R.color.gray_2)
    private val gray3 = ContextCompat.getColor(context, R.color.gray_3)
    private val black = ContextCompat.getColor(context, R.color.black)
    private val blue = ContextCompat.getColor(context, R.color.main_blue_1)

    private val regular = font(R.font.pretendard_regular)
    private val semiBold = font(R.font.pretendard_semibold)
    private val bold = font(R.font.pretendard_bold)

    private val borderPaint = strokePaint(dp(1f), gray3)
    private val dashPaint = strokePaint(dp(1f), gray3).apply {
        pathEffect = DashPathEffect(floatArrayOf(dp(2f), dp(2f)), 0f)
    }
    private val linePaint = strokePaint(dp(2f), gray3).apply {
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = sp(14f)
    }

    private val linePath = Path()

    // showValues=false: 기록이 없을 때 점·선·개수는 그리지 않고 테두리, 점선, 월 라벨만 그린다.
    fun setPoints(newPoints: List<Point>, showValues: Boolean = true) {
        points = newPoints
        this.showValues = showValues
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val chartWidth = width.toFloat()
        val chartHeight = dp(CHART_HEIGHT_DP)
        val half = borderPaint.strokeWidth / 2
        canvas.drawRoundRect(half, half, chartWidth - half, chartHeight - half, dp(2f), dp(2f), borderPaint)

        if (points.isEmpty()) return

        val cellWidth = chartWidth / points.size
        for (i in 1 until points.size) {
            val x = cellWidth * i
            canvas.drawLine(x, 0f, x, chartHeight, dashPaint)
        }

        val xs = FloatArray(points.size) { cellWidth * (it + 0.5f) }
        val lastIndex = points.lastIndex

        if (showValues) {
            val ys = computeYs()

            linePath.reset()
            points.indices.forEach { i ->
                if (i == 0) linePath.moveTo(xs[i], ys[i]) else linePath.lineTo(xs[i], ys[i])
            }
            canvas.drawPath(linePath, linePaint)

            val dotRadius = dp(DOT_RADIUS_DP)
            points.indices.forEach { i ->
                val isCurrent = i == lastIndex

                dotPaint.color = if (isCurrent) blue else gray2
                canvas.drawCircle(xs[i], ys[i], dotRadius, dotPaint)

                textPaint.typeface = if (isCurrent) bold else semiBold
                textPaint.color = if (isCurrent) blue else gray2
                drawCenteredText(canvas, "${points[i].count}개", xs[i], ys[i] - dotRadius - dp(10f))
            }
        }

        val labelCenterY = chartHeight + dp(LABEL_GAP_DP) + dp(10f)
        points.indices.forEach { i ->
            val isCurrent = i == lastIndex
            textPaint.typeface = if (isCurrent) semiBold else regular
            textPaint.color = if (isCurrent) black else gray1
            drawCenteredText(canvas, points[i].label, xs[i], labelCenterY)
        }
    }

    private fun computeYs(): FloatArray {
        val counts = points.map { it.count }
        val minCount = counts.min()
        val maxCount = counts.max()
        val span = maxCount - minCount
        val middle = (minCount + maxCount) / 2f
        val unit = if (span == 0) 0f else min(dp(MAX_UNIT_DP), dp(MAX_SPAN_HEIGHT_DP) / span)
        return FloatArray(points.size) { dp(PLOT_CENTER_DP) - (counts[it] - middle) * unit }
    }

    private fun drawCenteredText(canvas: Canvas, text: String, centerX: Float, centerY: Float) {
        val metrics = textPaint.fontMetrics
        canvas.drawText(text, centerX, centerY - (metrics.ascent + metrics.descent) / 2, textPaint)
    }

    private fun strokePaint(width: Float, color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = width
        this.color = color
    }

    private fun font(resId: Int): Typeface = ResourcesCompat.getFont(context, resId) ?: Typeface.DEFAULT

    private companion object {
        const val CHART_HEIGHT_DP = 200f
        const val LABEL_GAP_DP = 3f
        const val DOT_RADIUS_DP = 3.75f
        const val PLOT_CENTER_DP = 115f
        const val MAX_UNIT_DP = 16f
        const val MAX_SPAN_HEIGHT_DP = 120f
    }
}
