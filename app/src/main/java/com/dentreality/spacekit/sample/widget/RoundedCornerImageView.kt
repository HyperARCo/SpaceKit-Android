package com.dentreality.spacekit.sample.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.dentreality.spacekit.R

class RoundedCornerImageView(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {
    private val edgeColor: Int
    private val edgeWidth: Float
    private val radius: Float

    private val path: Path = Path()
    private val rect: RectF by lazy { RectF(0f, 0f, width.toFloat(), height.toFloat()) }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundedCornerImageView)
        edgeColor = attributes.getColor(R.styleable.RoundedCornerImageView_edgeColor, 0)
        edgeWidth = attributes.getDimension(R.styleable.RoundedCornerImageView_edgeWidth, 0f)
        radius = attributes.getDimension(R.styleable.RoundedCornerImageView_imageCornerRadius, 0f)
        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        path.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.drawRoundRect(rect, radius, radius, borderPaint)//draw border
    }

    val borderPaint: Paint by lazy {
        Paint().apply {
            color = edgeColor
            strokeWidth = edgeWidth
            style = Paint.Style.STROKE
        }
    }
}