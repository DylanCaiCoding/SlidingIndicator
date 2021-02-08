package com.dylanc.slidingindicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt

/**
 * @author Dylan Cai
 */
class SlidingIndicator(context: Context, attrs: AttributeSet) : View(context, attrs) {

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val padding = 8.dp
  private var autoCenter = true
  private var transparentEdge = false
  private var scaleHeight = 0f
  private var scaleWidth = 0f
  private var pointerHeight = 0f
  private var waveLength = 0f
  private var scaleSpan = 0f
  private var intervalSpanCount = 0
  private var lastX = 0f
  private var extraScaleCount = 0
  private var scaleColor = 0
  private var selectedColor = 0
  private var selectedValue = 0f
    set(value) {
      field = value
      invalidate()
    }
  var maxValue = 0
    set(value) {
      field = value
      invalidate()
    }
  var selectedIndex = 0
    internal set(value) {
      if (field == value) return
      field = value
      doOnSelected.forEach { it(value) }
    }
  private var doOnSelected: MutableList<(Int) -> Unit> = arrayListOf()
  private var doOnScroll: ((Int, Float) -> Unit)? = null
  private var animator: ObjectAnimator? = null
  var scrollSelect = true

  init {
    val array = context.obtainStyledAttributes(attrs, R.styleable.SlidingIndicator)
    scaleHeight = array.getDimension(R.styleable.SlidingIndicator_scaleHeight, 12.dp)
    scaleWidth = array.getDimension(R.styleable.SlidingIndicator_scaleWidth, 2.dp)
    scaleSpan = array.getDimension(R.styleable.SlidingIndicator_scaleSpan, 10.dp)
    pointerHeight = array.getDimension(R.styleable.SlidingIndicator_pointerHeight, 20.dp)
    waveLength = array.getDimension(R.styleable.SlidingIndicator_waveLength, 72.dp)
    intervalSpanCount = array.getInteger(R.styleable.SlidingIndicator_intervalSpanCount, 10)
    selectedIndex = array.getInteger(R.styleable.SlidingIndicator_selectedIndex, 0)
    maxValue = array.getInteger(R.styleable.SlidingIndicator_maxValue, 5)
    extraScaleCount = array.getInteger(R.styleable.SlidingIndicator_extraScaleCount, 0)
    scaleColor = array.getColor(R.styleable.SlidingIndicator_scaleColor, Color.WHITE)
    selectedColor =
      array.getColor(R.styleable.SlidingIndicator_selectedColor, Color.parseColor("#FF5722"))
    transparentEdge =
      array.getBoolean(R.styleable.SlidingIndicator_transparentEdge, false)

    paint.strokeCap = if (array.getInt(R.styleable.SlidingIndicator_scaleStyle, -1) == -1) {
      Paint.Cap.ROUND
    } else {
      Paint.Cap.SQUARE
    }

    if (autoCenter) {
      selectedValue = selectedIndex.toFloat()
    }
    array.recycle()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
      layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    }
    if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
      setMeasuredDimension(
        MeasureSpec.getSize(widthMeasureSpec),
        (padding * 2 + pointerHeight).toInt()
      )
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    doOnScroll?.invoke(selectedValue.toInt(), -selectedValue % 1)

    //TODO 优化起始点和绘制数量的算法
    var startX: Float
    var drawCount: Int
    when {
      selectedValue == 0f -> {
        startX = measuredWidth / 2f
        drawCount =
          if ((maxValue - selectedValue) * intervalSpanCount * scaleSpan > measuredWidth / 2) {
            ((measuredWidth / 2 - (1 - selectedValue * intervalSpanCount % 1 * scaleSpan)) / scaleSpan).toInt()
          } else {
            ((maxValue - selectedValue) * intervalSpanCount).toInt()
          }
      }
      selectedValue * intervalSpanCount * scaleSpan > measuredWidth / 2 -> {
        startX = (measuredWidth / 2 - selectedValue * intervalSpanCount % 1 * scaleSpan) % scaleSpan
        drawCount =
          if ((maxValue - selectedValue) * intervalSpanCount * scaleSpan > measuredWidth / 2) {
            ((measuredWidth - startX) / scaleSpan).toInt()
          } else {
            (((maxValue - selectedValue) * intervalSpanCount * scaleSpan + measuredWidth / 2 - startX) / scaleSpan).roundToInt()
          }
      }
      else -> {
        startX = measuredWidth / 2 - selectedValue * intervalSpanCount * scaleSpan
        drawCount =
          if ((maxValue - selectedValue) * intervalSpanCount * scaleSpan > measuredWidth / 2) {
            ((measuredWidth - startX) / scaleSpan).toInt()
          } else {
            (((maxValue - selectedValue) * intervalSpanCount * scaleSpan + measuredWidth / 2 - startX) / scaleSpan).roundToInt()
          }
      }
    }
    startX -= extraScaleCount * scaleSpan
    drawCount += extraScaleCount * 2

    for (i in 0..drawCount) {
      if ((startX + i * scaleSpan - measuredWidth / 2f).absoluteValue < waveLength / 2) {
        paint.color = selectedColor
        paint.strokeWidth = scaleWidth
        canvas.drawLine(
          startX + i * scaleSpan,
          measuredHeight - padding,
          startX + i * scaleSpan,
          measuredHeight - padding - scaleHeight - waveHeight(startX + i * scaleSpan),
          paint
        )
      } else {
        paint.color = if (transparentEdge) {
          scaleColor.convertAlpha(1 - (measuredWidth / 2 - startX - i * scaleSpan).absoluteValue / measuredWidth * 2)
        } else {
          scaleColor
        }
        paint.strokeWidth = scaleWidth
        canvas.drawLine(
          startX + i * scaleSpan,
          measuredHeight - padding,
          startX + i * scaleSpan,
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

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        animator?.cancel()
        animator = null
        lastX = x
      }
      MotionEvent.ACTION_MOVE -> {
        val offsetValue = (lastX - x) / scaleSpan / intervalSpanCount
        lastX = x
        scrollTo(offsetValue)
      }
      MotionEvent.ACTION_UP -> {
        val targetValue = if (selectedValue - selectedValue.toInt() > 0.5) {
          selectedValue.toInt() + 1
        } else {
          selectedValue.toInt()
        }
        smoothScrollTo(targetValue - selectedValue)
      }
    }
    return true
  }

  fun doOnSelected(block: (Int) -> Unit) {
    doOnSelected.add(block)
  }

  fun doOnScrolled(block: (Int, Float) -> Unit) {
    doOnScroll = block
  }

  fun scrollToPosition(position: Int) {
    selectedValue = position.toFloat()
    selectedIndex = position
  }

  fun scrollTo(offsetValue: Float) {
    selectedValue = when {
      selectedValue + offsetValue <= 0f -> 0f
      selectedValue + offsetValue >= maxValue -> maxValue.toFloat()
      else -> selectedValue + offsetValue
    }
    if (!scrollSelect) {
      return
    }

    if (offsetValue > 0 && selectedValue.roundToInt() != selectedIndex) {
      selectedIndex = selectedValue.roundToInt()
    } else if (offsetValue < 0) {
      if (selectedValue == 0f && selectedIndex != 0) {
        selectedIndex = 0
      } else if (selectedValue > 0f && selectedIndex - selectedValue > 1) {
        selectedIndex = selectedValue.toInt() + (selectedIndex - selectedValue).toInt()
      }
    }
  }

  fun smoothScrollTo(offsetValue: Float) {
    //todo 支持在动画未执行完时再次滑动
    if (animator != null && animator!!.isRunning) {
      return
    }
    animator =
      ObjectAnimator.ofFloat(this, "selectedValue", selectedValue, selectedValue + offsetValue)
        .apply {
          addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
              selectedIndex = selectedValue.toInt()
              animator = null
            }
          })
        }
    animator?.start()
  }

  private fun waveHeight(x: Float): Float {
    val waveAmplitude = (pointerHeight - scaleHeight) / 2
    return (waveAmplitude * cos(2 * Math.PI / waveLength * (x - measuredWidth / 2f)) + waveAmplitude).toFloat()
  }

  private val Int.dp get() = toFloat().dp

  private val Float.dp
    get() = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

  private fun Int.convertAlpha(alpha: Float): Int {
    val a = 255.coerceAtMost(0.coerceAtLeast((alpha * 255).toInt())) shl 24
    val rgb = 0x00ffffff and this
    return a + rgb
  }
}
