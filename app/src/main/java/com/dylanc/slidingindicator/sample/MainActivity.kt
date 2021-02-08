package com.dylanc.slidingindicator.sample

import android.os.Bundle
import android.util.Log
import android.widget.ListAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dylanc.slidingindicator.setupWithRecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private val list = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val textAdapter = TextAdapter()
    recycler_view.adapter = textAdapter
    sliding_indicator.maxValue = list.size - 1
    sliding_indicator.setupWithRecyclerView(recycler_view, 5, false)

    sliding_indicator.doOnSelected { position ->
      Log.d("doOnSelected", position.toString())
    }
    sliding_indicator.scrollToPosition(2)

    textAdapter.submitList(list)
    textAdapter.notifyDataSetChanged()
  }
}