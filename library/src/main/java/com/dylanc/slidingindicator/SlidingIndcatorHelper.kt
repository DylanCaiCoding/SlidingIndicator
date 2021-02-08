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
  spanCount: Int,
  scrollSelect: Boolean = false
) {
  if (spanCount % 2 == 0) {
    throw IllegalArgumentException("Span count must be odd number.")
  }

  val layoutManager =
    LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
  val rawAdapter = recyclerView.adapter
  this.scrollSelect = scrollSelect

  recyclerView.apply {
    this.layoutManager = layoutManager
    adapter = rawAdapter?.let {
      if (it is DecoratedAdapter) it else DecoratedAdapter(it, spanCount)
    }
    LinearSnapHelper().attachToRecyclerView(this)
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE && !scrollSelect) {
          layoutManager.apply {
            val firstVisibleItemPosition = findFirstVisibleItemPosition()
            val firstCompletelyVisibleItemPosition = findFirstCompletelyVisibleItemPosition()
            if (firstVisibleItemPosition == firstCompletelyVisibleItemPosition) {
              selectedIndex = firstVisibleItemPosition
            }
          }
        }
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        scrollTo(dx.toFloat() / recyclerView.measuredWidth * spanCount)
      }
    })
  }

  doOnScrolled { position, offsetValue ->
    layoutManager.scrollToPositionWithOffset(
      position,
      (recyclerView.measuredWidth / spanCount * offsetValue).toInt()
    )
  }

  if (rawAdapter is CheckableAdapter<*, *>) {
    rawAdapter.addOnItemClickListener { position ->
      smoothScrollTo((position - rawAdapter.checkedPosition).toFloat())
    }
    doOnSelected { position ->
      recyclerView.post {
        rawAdapter.selectAt(position)
        recyclerView.adapter?.notifyDataSetChanged()
      }
    }
  }
}