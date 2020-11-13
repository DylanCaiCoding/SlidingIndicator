package com.dylanc.indicatorview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import kotlin.math.absoluteValue
import kotlin.math.cos

class IndicatorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var minSpan = 16.dp
  private var intervalSpanCount = 10
  private var selectIndex = 1
  private var maxValue = 4
  private var selectValue = 0f
    set(value) {
      field = value
      invalidate()
    }

  private val paddingBottom = 8.dp
  private val scaleHeight = 30.dp
  private val scaleWidth = 6.dp
  private var waveLength = 120.dp
  private var waveAmplitude = 8.dp
  private var lastX = 0f
  private var offsetValue = 0f
    set(value) {
      field = value
      invalidate()
    }

  private var autoCenter = true

  init {
    paint.color = Color.WHITE
    paint.strokeCap = Paint.Cap.ROUND
    if (autoCenter) {
      selectValue = selectIndex.toFloat()
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    val drawCount: Int
    val startX: Float
    val currentValue = selectValue - offsetValue
    when {
      currentValue == 0f -> {
        startX = measuredWidth / 2f
        drawCount =
          if ((maxValue - currentValue) * intervalSpanCount * minSpan > measuredWidth / 2) {
            ((measuredWidth / 2 - (1 - currentValue * intervalSpanCount % 1 * minSpan)) / minSpan).toInt()
          } else {
            ((maxValue - currentValue) * intervalSpanCount).toInt()
          }
      }
      currentValue * intervalSpanCount * minSpan > measuredWidth / 2 -> {
        startX = (measuredWidth / 2 - currentValue * intervalSpanCount % 1 * minSpan) % minSpan
        drawCount =
          if ((maxValue - currentValue) * intervalSpanCount * minSpan > measuredWidth / 2) {
            ((measuredWidth - startX) / minSpan).toInt()
          } else {
            (((maxValue - currentValue) * intervalSpanCount * minSpan + measuredWidth / 2 - startX) / minSpan).toInt()
          }
      }
      else -> {
        startX = measuredWidth / 2 - currentValue * intervalSpanCount * minSpan
        drawCount =
          if ((maxValue - currentValue) * intervalSpanCount * minSpan > measuredWidth / 2) {
            ((measuredWidth - startX) / minSpan).toInt()
          } else {
            (((maxValue - currentValue) * intervalSpanCount * minSpan + measuredWidth / 2 - startX) / minSpan).toInt()
          }
      }
    }

    for (i in 0..drawCount) {
      if ((startX + i * minSpan - measuredWidth / 2f).absoluteValue < waveLength / 2) {
        paint.color = Color.RED
        paint.strokeWidth = scaleWidth
        canvas.drawLine(
          startX + i * minSpan,
          measuredHeight - paddingBottom,
          startX + i * minSpan,
          measuredHeight - paddingBottom - scaleHeight - waveHeight(startX + i * minSpan),
          paint
        )
      } else {
        paint.color = Color.WHITE
        paint.strokeWidth = scaleWidth
        canvas.drawLine(
          startX + i * minSpan,
          measuredHeight - paddingBottom,
          startX + i * minSpan,
          measuredHeight - paddingBottom - scaleHeight,
          paint
        )
      }
    }

    // test
    paint.strokeWidth = 1f
    paint.color = Color.WHITE
    canvas.drawLine(measuredWidth / 2f, 0f, measuredWidth / 2f, measuredHeight.toFloat(), paint)
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        lastX = x
      }
      MotionEvent.ACTION_MOVE -> {
        offsetValue = (x - lastX) / minSpan / intervalSpanCount
      }
      MotionEvent.ACTION_UP -> {
        selectValue -= offsetValue
        offsetValue = 0f
        val targetValue = if (selectValue - selectValue.toInt() > 0.5) {
          selectValue.toInt() + 1
        } else {
          selectValue.toInt()
        }
        ObjectAnimator.ofFloat(this, "selectValue", selectValue, targetValue.toFloat())
          .apply {
            doOnEnd {
              Log.d("selectValue", selectValue.toString())
            }
          }
          .start()
      }
    }
    return true
  }

  private fun waveHeight(x: Float): Float {
    return (waveAmplitude * cos(2 * Math.PI / waveLength * (x - measuredWidth / 2f)) + waveAmplitude).toFloat()
  }


}