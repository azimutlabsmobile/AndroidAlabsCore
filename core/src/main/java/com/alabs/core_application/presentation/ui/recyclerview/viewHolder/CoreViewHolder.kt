package com.alabs.core_application.presentation.ui.recyclerview.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class CoreViewHolder<V>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: V)
}