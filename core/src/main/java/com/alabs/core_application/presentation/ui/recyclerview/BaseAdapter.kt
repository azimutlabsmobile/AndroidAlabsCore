package com.alabs.core_application.presentation.ui.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.alabs.core_application.presentation.ui.recyclerview.viewHolder.BaseViewHolder

//
@Deprecated("Не поддерживается будут удален")
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewHolder<T>>() {

    internal var items = mutableListOf<T>()

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(list: List<T>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun getItems(): List<T> = items

}