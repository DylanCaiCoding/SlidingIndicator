package com.dylanc.slidingindicator.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private lateinit var textAdapter: TextAdapter
  private val list = listOf(
    TextItem("0"),
    TextItem("1"),
    TextItem("2"),
    TextItem("3"),
    TextItem("4"),
    TextItem("5"),
    TextItem("6"),
    TextItem("7"),
    TextItem("8")
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val linearLayoutManager =
      LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    textAdapter = TextAdapter(list, this::onItemClick)

    recycler_view.apply {
      adapter = textAdapter
      layoutManager = linearLayoutManager
      LinearSnapHelper().attachToRecyclerView(this)
      addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          indicator.scrollTo(dx.toFloat() / recyclerView.measuredWidth * 5)
        }
      })
    }

    indicator.apply {
      maxValue = list.size - 1
      doOnSelected {
        textAdapter.selectAt(it)
      }
      doOnScroll { position, offsetValue ->
        linearLayoutManager.scrollToPositionWithOffset(
          position,
          (recycler_view.measuredWidth / 5 * offsetValue).toInt()
        )
      }
      scrollToPosition(1)
    }
  }

  private fun onItemClick(position: Int) {
    indicator.smoothScrollTo(((position - textAdapter.checkedPosition).toFloat()))
  }
}