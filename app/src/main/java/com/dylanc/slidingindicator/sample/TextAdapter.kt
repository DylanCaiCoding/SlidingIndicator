package com.dylanc.slidingindicator.sample

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dylanc.slidingindicator.adapter.CheckableAdapter
import kotlinx.android.synthetic.main.recycler_item_text.view.*

class TextAdapter(list: List<String>) : CheckableAdapter<String, TextAdapter.ViewHolder>(list) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    ViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_text, parent, false)
    )

  override fun onBindViewHolder(holder: ViewHolder, position: Int, item: String, isChecked: Boolean) {
    holder.itemView.apply {
      tv_content.text = item
      if (isChecked) {
        tv_content.setTextColor(Color.parseColor("#FF5722"))
      } else {
        tv_content.setTextColor(Color.WHITE)
      }
    }
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}