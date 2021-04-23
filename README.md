# SlidingIndicator

## 简介

少啰嗦，看东西！

![gif图](./gif/sample.gif) 

`SlidingIndicator` 是一个自定义滑动指示器控件，可很方便地与 `RecyclerView` 实现联动。

## 用法


#### 在 xml 添加 SlidingIndicator

```xml
<com.dylanc.slidingindicator.SlidingIndicator
    android:id="@+id/sliding_indicator"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

可对控件进行自定义，以下是自定义属性：

| 自定义属性        |       类型       | 作用                             |
| ----------------- | :--------------: | -------------------------------- |
| scaleHeight       |    dimension     | 刻度高度                         |
| scaleWidth        |    dimension     | 刻度宽度                         |
| scaleSpan         |    dimension     | 相邻两刻度的间距大小             |
| pointerHeight     |    dimension     | 指针高度                         |
| waveLength        |    dimension     | 波浪长度                         |
| intervalSpanCount |     integer      | 两个索引之间的刻度数量           |
| selectedIndex     |     integer      | 选中的位置                       |
| maxValue          |     integer      | 刻度最大值                       |
| extraScaleCount   |     integer      | 最左端和最右端增加额外的刻度数量 |
| scaleColor        |      color       | 刻度颜色                         |
| selectedColor     |      color       | 选中的刻度颜色                   |
| scaleStyle        | roundCap, square | 刻度主题，矩形或圆帽子           |

#### 与 RecyclerView 联动

```kotlin
recycler_view.adapter = DecoratedAdapter(adapter, spanCount = 5)
slidingIndicator.maxValue = items.size - 1
slidingIndicator.setupWithRecyclerView(recyclerView, spanCount = 5, scrollSelect = true)
```

DecoratedAdapter 的作用是对普通的适配器进行装饰，比如这里均分成 5 列，并在前后补充空白项，使第一个能居中选中。
