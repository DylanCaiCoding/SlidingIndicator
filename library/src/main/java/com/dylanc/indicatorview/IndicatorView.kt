package com.dylanc.indicatorview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import kotlin.math.absoluteValue
import kotlin.math.cos

class IndicatorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var minSpan = 16.dp
  private var intervalSpanCount = 10
  var maxValue = 8
    set(value) {
      field = value
      invalidate()
    }
  private val padding = 8.dp
  private val scaleHeight = 16.dp
  private val scaleWidth = 2.dp
  private var waveLength = 100.dp
  private var waveAmplitude = 4.dp
  private var lastX = 0f
  private var selectValue = 0f
    set(value) {
      field = value
      invalidate()
    }
  var selectIndex = 0
    private set(value) {
      field = value
      doOnSelected?.invoke(value)
    }

  private var autoCenter = true
  private var doOnSelected: ((Int) -> Unit)? = null
  private var doOnScroll: ((Int, Float) -> Unit)? = null

  init {
    paint.color = Color.WHITE
    paint.strokeCap = Paint.Cap.ROUND
    if (autoCenter) {
      selectValue = selectIndex.toFloat()
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
      layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    }
    if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
      setMeasuredDimension(
        MeasureSpec.getSize(widthMeasureSpec),
        (padding * 2 + scaleHeight + waveAmplitude * 2).toInt()
      )
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    doOnScroll?.invoke(selectValue.toInt(), -selectValue % 1)

    val drawCount: Int
    val startX: Float
    when {
      selectValue == 0f -> {
        startX = measuredWidth / 2f
        drawCount =
          if ((maxValue - selectValue) * intervalSpanCount * minSpan > measuredWidth / 2) {
            ((measuredWidth / 2 - (1 - selectValue * intervalSpanCount % 1 * minSpan)) / minSpan).toInt()
          } else {
            ((maxValue - selectValue) * intervalSpanCount).toInt()
          }
      }
      selectValue * intervalSpanCount * minSpan > measuredWidth / 2 -> {
        startX = (measuredWidth / 2 - selectValue * intervalSpanCount % 1 * minSpan) % minSpan
        drawCount =
          if ((maxValue - selectValue) * intervalSpanCount * minSpan > measuredWidth / 2) {
            ((measuredWidth - startX) / minSpan).toInt()
          } else {
            (((maxValue - selectValue) * intervalSpanCount * minSpan + measuredWidth / 2 - startX) / minSpan).toInt()
          }
      }
      else -> {
        startX = measuredWidth / 2 - selectValue * intervalSpanCount * minSpan
        drawCount =
          if ((maxValue - selectValue) * intervalSpanCount * minSpan > measuredWidth / 2) {
            ((measuredWidth - startX) / minSpan).toInt()
          } else {
            (((maxValue - selectValue) * intervalSpanCount * minSpan + measuredWidth / 2 - startX) / minSpan).toInt()
          }
      }
    }

    for (i in 0..drawCount) {
      if ((startX + i * minSpan - measuredWidth / 2f).absoluteValue < waveLength / 2) {
        paint.color = Color.RED
        paint.strokeWidth = scaleWidth
        canvas.drawLine(
          startX + i * minSpan,
          measuredHeight - padding,
          startX + i * minSpan,
          measuredHeight - padding - scaleHeight - waveHeight(startX + i * minSpan),
          paint
        )
      } else {
        paint.color = Color.WHITE
        paint.strokeWidth = scaleWidth
        canvas.drawLine(
          startX + i * minSpan,
          measuredHeight - padding,
          startX + i * minSpan,
          measuredHeight - padding - scaleHeight,
          paint
        )
      }
    }

//    // test
//    paint.strokeWidth = 1f
//    paint.color = Color.WHITE
//    canvas.drawLine(measuredWidth / 2f, 0f, measuredWidth / 2f, measuredHeight.toFloat(), paint)
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        lastX = x
      }
      MotionEvent.ACTION_MOVE -> {
        val offsetValue = (x - lastX) / minSpan / intervalSpanCount
        lastX = x
        scrollTo(offsetValue)
      }
      MotionEvent.ACTION_UP -> {
        val targetValue = if (selectValue - selectValue.toInt() > 0.5) {
          selectValue.toInt() + 1
        } else {
          selectValue.toInt()
        }
        ObjectAnimator.ofFloat(this, "selectValue", selectValue, targetValue.toFloat())
          .apply {
            doOnEnd {
              selectIndex = selectValue.toInt()
            }
          }
          .start()
      }
    }
    return true
  }

  fun doOnSelected(block: (Int) -> Unit) {
    doOnSelected = block
  }

  fun doOnScroll(block: (Int, Float) -> Unit) {
    doOnScroll = block
  }

  fun scrollTo(offsetValue: Float) {
    selectValue = when {
      selectValue + offsetValue <= 0f -> 0f
      selectValue + offsetValue >= maxValue -> maxValue.toFloat()
      else -> selectValue + offsetValue
    }

    if (offsetValue < 0 && selectValue.toInt() != selectIndex) {
      selectIndex = selectValue.toInt()
    } else if (offsetValue > 0) {
      if (selectValue == 0f && selectIndex != 0) {
        selectIndex = 0
      } else if (selectValue != 0f && selectValue.toInt() + 1 != selectIndex) {
        selectIndex = selectValue.toInt() + 1
      }
    }
  }

  private fun waveHeight(x: Float): Float {
    return (waveAmplitude * cos(2 * Math.PI / waveLength * (x - measuredWidth / 2f)) + waveAmplitude).toFloat()
  }

}