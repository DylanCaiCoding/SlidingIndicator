package com.dylanc.slidingindicator.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * @author Dylan Cai
 */
internal class DecoratedAdapter(
  private val adapter: Adapter<*>,
  private val spanCount: Int
) : Adapter<ViewHolder>() {

  private val extraCount: Int = spanCount / 2

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    if (viewType == VIEW_TYPE_EXTRA) {
      SimpleViewHolder(View(parent.context)).apply {
        val layoutParams = ViewGroup.LayoutParams(parent.measuredWidth / spanCount, 0)
        itemView.layoutParams = layoutParams
      }
    } else {
      adapter.onCreateViewHolder(parent, viewType).apply {
        val layoutParams: ViewGroup.LayoutParams = itemView.layoutParams
        layoutParams.width = parent.measuredWidth / spanCount
        itemView.layoutParams = layoutParams
      }
    }

  @Suppress("UNCHECKED_CAST")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (getItemViewType(position) != VIEW_TYPE_EXTRA) {
      val adapter = adapter as Adapter<ViewHolder>
      adapter.onBindViewHolder(holder, position - extraCount)
    }
  }

  override fun getItemCount() = adapter.itemCount + extraCount * 2

  override fun getItemViewType(position: Int) =
    if (position < extraCount || position >= itemCount - extraCount) {
      VIEW_TYPE_EXTRA
    } else {
      adapter.getItemViewType(position - extraCount)
    }

  companion object {
    private const val VIEW_TYPE_EXTRA = -9999
  }

  class SimpleViewHolder(itemView: View) : ViewHolder(itemView)
}