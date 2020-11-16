package com.dylanc.indicatorview.sample

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_item_text.view.*

class TextAdapter(
  private val list: List<TextItem>
) : RecyclerView.Adapter<TextAdapter.ViewHolder>() {

  private var checkedPosition = 2

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    ViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_text, parent, false)
    ).apply {
      val parentWidth = parent.measuredWidth
      val layoutParams: ViewGroup.LayoutParams = itemView.layoutParams
      layoutParams.width = parentWidth / 5
      itemView.layoutParams = layoutParams
    }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.itemView.apply {
      if (position >= 2 && position < itemCount - 2) {
        val item = list[position - 2]
        tv_content.text = item.content
        if (item.isChecked) {
          tv_content.setTextColor(Color.RED)
        } else {
          tv_content.setTextColor(Color.WHITE)
        }
      } else {
        tv_content.text = ""
        tv_content.setTextColor(Color.WHITE)
      }
    }
  }

  override fun getItemCount() = list.size + 4

  fun check(position: Int) {
    if (position < itemCount - 4) {
      list[checkedPosition - 2].isChecked = false
      list[position].isChecked = true
      notifyDataSetChanged()
      checkedPosition = position + 2
    }
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}