package com.dylanc.indicatorview.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val linearLayoutManager =
      LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    val list = listOf(
      TextItem("0", true),
      TextItem("1"),
      TextItem("2"),
      TextItem("3"),
      TextItem("4"),
      TextItem("5"),
      TextItem("6"),
      TextItem("7"),
      TextItem("8")
    )
    val textAdapter = TextAdapter(list)

    recycler_view.apply {
      adapter = textAdapter
      layoutManager = linearLayoutManager
      addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          recyclerView.post { indicator_view.scrollTo(dx.toFloat() / recyclerView.measuredWidth * 5) }
        }
      })
      LinearSnapHelper().attachToRecyclerView(this)
    }
    indicator_view.apply {
      doOnSelected {
        textAdapter.check(it)
        Log.d("test", it.toString())
      }
      doOnScroll { position, offsetValue ->
        linearLayoutManager.scrollToPositionWithOffset(
          position,
          (recycler_view.measuredWidth / 5 * offsetValue).toInt()
        )
      }
    }
  }
}