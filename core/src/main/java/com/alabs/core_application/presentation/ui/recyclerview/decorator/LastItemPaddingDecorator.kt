package com.alabs.core_application.presentation.ui.recyclerview.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Задаем padding для поледнего элемента
 */
class LastItemPaddingDecorator(
    private val left: Float = 0f,
    private val top: Float = 0f,
    private val right: Float = 0f,
    private val bottom: Float = 0f
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)

        if (itemPosition == parent.adapter?.itemCount?.minus(1)) {
            outRect.left += left.toInt()
            outRect.top += top.toInt()
            outRect.right += right.toInt()
            outRect.bottom += bottom.toInt()
        }
    }

}