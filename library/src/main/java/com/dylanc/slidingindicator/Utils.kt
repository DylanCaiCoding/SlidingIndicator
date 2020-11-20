package com.dylanc.slidingindicator

import android.content.res.Resources
import android.util.TypedValue

/**
 * @author Dylan Cai
 */

val Int.dp get() = toFloat().dp

val Float.dp
  get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
  )

fun Int.convertAlpha(alpha: Float): Int {
  val a = 255.coerceAtMost(0.coerceAtLeast((alpha * 255).toInt())) shl 24
  val rgb = 0x00ffffff and this
  return a + rgb
}