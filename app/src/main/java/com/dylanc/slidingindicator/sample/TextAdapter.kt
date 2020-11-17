package com.dylanc.slidingindicator.sample

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_item_text.view.*

class TextAdapter(
  private val list: List<TextItem>,
  private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<TextAdapter.ViewHolder>() {

  var checkedPosition = 0
    private set

  init {
    list[checkedPosition].isChecked = true
  }

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
          tv_content.setTextColor(Color.parseColor("#FF5722"))
        } else {
          tv_content.setTextColor(Color.WHITE)
        }
        setOnClickListener {
          onItemClick(position - 2)
        }
      } else {
        tv_content.text = ""
        setOnClickListener(null)
      }
    }
  }

  override fun getItemCount() = list.size + 4

  fun selectAt(position: Int) {
    list[checkedPosition].isChecked = false
    list[position].isChecked = true
    notifyDataSetChanged()
    checkedPosition = position
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}