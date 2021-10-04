package com.alabs.multiAdapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


open class PaginatedAdapter : SearchMultiTypeAdapter() {


    private var pageSize = 10
    private var currentItems = mutableListOf<Any>()

    var isLoadNewItem = false
    var mListener: OnPaginationListener? = null
    var currentPage = 1


    override var items: List<Any> = emptyList()
        set(value) {
            currentItems = items.toMutableList()
            currentItems.addAll(value)
            field = currentItems
            notifyDataSetChanged()
            mListener?.onCurrentPage(currentPage)
            if (actualSize(value) == pageSize)
                isLoadNewItem = true
            else {
                mListener?.onFinish()
            }
        }

    var adapter: RecyclerView? = null
        set(value) {
            field = value
            initRecyclerViewPaging()
        }


    /**
     * При работе RecyclerView
     */
    private fun initRecyclerViewPaging() {
        adapter?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager =
                    LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager?.itemCount ?: -1
                val lastVisible = layoutManager?.findLastVisibleItemPosition() ?: -1
                val endHasBeenReached = lastVisible + 2 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached && isLoadNewItem) {
                    mListener?.onNextPage(++currentPage)
                    isLoadNewItem = false
                }
            }
        })
    }


    /**
     * В случае если в спике находяться лишние данные которые дабавлены хардно,
     * данная функция дает возможность отфильтровать данные
     */
    open fun actualSize(items : List<Any>) = items.size


    /**
     * Передаем слушатель
     */
    fun setOnPaginationListener(onPaginationListener: OnPaginationListener?) {
        mListener = onPaginationListener
    }


    /**
     * Размерность страницы
     */
    fun setPageSize(pageSize: Int) {
        this.pageSize = pageSize
    }

    /**
     * Удаление данных пагинации
     */
    fun clearPagingData() {
        currentItems.clear()
    }


    /**
     * Отчиска ресурсов
     */
    fun clear() {
        adapter = null
    }

    interface OnPaginationListener {
        fun onCurrentPage(page: Int) {
            // do nothing
        }

        fun onNextPage(page: Int)

        fun onFinish() {
            // do nothing
        }
    }
}