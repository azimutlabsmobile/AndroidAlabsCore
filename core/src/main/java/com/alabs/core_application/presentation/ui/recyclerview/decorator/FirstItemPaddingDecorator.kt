package com.alabs.core_application.presentation.ui.recyclerview.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FirstItemPaddingDecorator(
    private val left: Float,
    private val top: Float,
    private val right: Float,
    private val bottom: Float
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)


        if (itemPosition == 0) {
            outRect.left += left.toInt()
            outRect.top += top.toInt()
            outRect.right += right.toInt()
            outRect.bottom += bottom.toInt()
        }
    }

}