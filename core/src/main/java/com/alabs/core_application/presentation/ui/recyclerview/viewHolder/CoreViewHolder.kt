package com.alabs.core_application.presentation.ui.recyclerview.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class CoreViewHolder<V>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: V)
}

@Deprecated("Не поддерживается будут удален")
abstract class BaseViewHolder<T>(
    @LayoutRes layoutId: Int,
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(
        parent.context
    ).inflate(layoutId, parent, false)) {

    open fun onBind(item: T) {}
}