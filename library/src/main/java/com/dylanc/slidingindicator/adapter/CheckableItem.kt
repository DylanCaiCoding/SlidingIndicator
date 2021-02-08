package com.dylanc.slidingindicator.adapter

/**
 * @author Dylan Cai
 */

typealias CheckableList<T> = List<CheckableItem<T>>

fun <T> List<T>.toCheckableList() = map { CheckableItem(it) }

class CheckableItem<T>(
  var data: T,
  var isChecked: Boolean = false
)