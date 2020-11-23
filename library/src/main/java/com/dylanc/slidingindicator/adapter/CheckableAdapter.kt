package com.dylanc.slidingindicator.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * @author Dylan Cai
 */
abstract class CheckableAdapter<T, VH : RecyclerView.ViewHolder>(
  list: List<T>
) : RecyclerView.Adapter<VH>() {

  var checkedPosition = 0
    private set

  private var onItemClick: ((Int) -> Unit)? = null

  private val list = mutableListOf<CheckableItem<T>>()
    .apply {
      list.forEach {
        add(CheckableItem(it))
      }
      this[checkedPosition].isChecked = true
    }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.itemView.setOnClickListener {
      onItemClick?.invoke(position)
    }
    onBindViewHolder(holder, position, list[position].data, list[position].isChecked)
  }

  override fun getItemCount() = list.size

  abstract fun onBindViewHolder(
    holder: VH,
    position: Int,
    item: T,
    isChecked: Boolean
  )

  fun selectAt(position: Int) {
    list[checkedPosition].isChecked = false
    list[position].isChecked = true
    checkedPosition = position
  }

  fun onItemClick(block: (Int) -> Unit) {
    onItemClick = block
  }
}