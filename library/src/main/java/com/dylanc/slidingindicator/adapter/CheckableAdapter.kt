package com.dylanc.slidingindicator.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * @author Dylan Cai
 */
abstract class CheckableAdapter<T, VH : RecyclerView.ViewHolder>(
  list: List<T> = emptyList(), checkedPosition: Int = 0
) : RecyclerView.Adapter<VH>() {

  private lateinit var list: CheckableList<T>
  private var onItemClickListeners: MutableList<(Int) -> Unit> = arrayListOf()
  var checkedPosition = checkedPosition
    private set

  init {
    submitList(list)
  }

  fun submitList(list: List<T>) {
    this.list = list.toCheckableList()
      .apply {
        if (list.isNotEmpty()) this[checkedPosition].isChecked = true
      }
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.itemView.setOnClickListener {
      onItemClickListeners.forEach { it(position) }
    }
    onBindViewHolder(holder, list[position].data, list[position].isChecked)
  }

  override fun getItemCount() = list.size

  abstract fun onBindViewHolder(holder: VH, item: T, isChecked: Boolean)

  fun addOnItemClickListener(block: (Int) -> Unit) {
    onItemClickListeners.add(block)
  }

  fun selectAt(position: Int) {
    list[checkedPosition].isChecked = false
    list[position].isChecked = true
    checkedPosition = position
    notifyDataSetChanged()
  }
}