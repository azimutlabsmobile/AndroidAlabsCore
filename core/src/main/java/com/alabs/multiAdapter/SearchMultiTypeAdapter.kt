package com.alabs.multiAdapter

import com.alabs.core_application.utils.extensions.toArrayList
import com.alabs.multiAdapter.SearchStrategy.CONTAINS
import com.alabs.multiAdapter.SearchStrategy.STARTS_WITH
import java.util.*
import kotlin.collections.ArrayList

/**
 * Производит фильтрацию по тем элементам, которые implement-ят SearchMultiType
 * Остальные элементы не обрезаются
 */
open class SearchMultiTypeAdapter : MultiTypeAdapter() {

    /**
     * Хранание отфильтрованных данных
     */
    private var filteredList = mutableListOf<Any>()

    /**
     * Хранение оригинальных данных
     */
    private var tmpList = mutableListOf<Any>()

    /**
     * Фильтрация содержимого в адаптере
     */
    fun makeFilter(
        value: CharSequence?,
        searchStrategy: SearchStrategy? = STARTS_WITH
    ) {
        if (tmpList.isNullOrEmpty()) {
            tmpList = items.toArrayList()
        }
        val originalItems = tmpList
        val charSearch = value?.toString().orEmpty()
        val queryLowered = charSearch.toLowerCase(Locale.ROOT)
        filteredList = if (charSearch.isEmpty()) {
            originalItems
        } else {
            val resultList = ArrayList<Any>()
            for (row in originalItems) {
                if (row is SearchMultitype) {
                    val searchSourceItemLowered = row.value.toLowerCase(Locale.ROOT)
                    when (searchStrategy) {
                        STARTS_WITH -> {
                            if (searchSourceItemLowered.startsWith(queryLowered)) {
                                resultList.add(row)
                            }
                        }
                        CONTAINS -> {
                            if (searchSourceItemLowered.contains(queryLowered)) {
                                resultList.add(row)
                            }
                        }
                    }
                } else {
                    resultList.add(row)
                }
            }
            resultList
        }
        items = filteredList
        notifyDataSetChanged()
    }

    /**
     * Сброс фильтра
     */
    fun resetFilter() {
        items = tmpList
        notifyDataSetChanged()
    }
}