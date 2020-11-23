package com.dylanc.slidingindicator

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dylanc.slidingindicator.adapter.CheckableAdapter
import com.dylanc.slidingindicator.adapter.DecoratedAdapter

/**
 * @author Dylan Cai
 */
fun SlidingIndicator.setupWithRecyclerView(
  recyclerView: RecyclerView,
  spanCount: Int
) {
  if (spanCount % 2 == 0) {
    throw IllegalArgumentException("Span count must be odd number.")
  }

  val linearLayoutManager =
    LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)

  val rawAdapter = recyclerView.adapter

  recyclerView.apply {
    adapter = rawAdapter?.let { DecoratedAdapter(it, spanCount) }
    layoutManager = linearLayoutManager
    LinearSnapHelper().attachToRecyclerView(this)
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        scrollTo(dx.toFloat() / recyclerView.measuredWidth * spanCount)
      }
    })
  }

  doOnScrolled { position, offsetValue ->
    linearLayoutManager.scrollToPositionWithOffset(
      position,
      (recyclerView.measuredWidth / spanCount * offsetValue).toInt()
    )
  }

  if (rawAdapter is CheckableAdapter<*, *>) {
    rawAdapter.onItemClick { position ->
      smoothScrollTo(((position - rawAdapter.checkedPosition).toFloat()))
    }
    doOnSelected { position ->
      rawAdapter.selectAt(position)
      recyclerView.adapter?.notifyDataSetChanged()
    }
  }
}