package com.alabs.multiAdapter


inline fun PaginatedAdapter.addListener(
    crossinline onCurrentPage: (Int) -> Unit = {},
    crossinline onFinishPage: () -> Unit = {},
    crossinline onNextPage: (Int) -> Unit = {}
) {

    val listener = object : PaginatedAdapter.OnPaginationListener {
        override fun onCurrentPage(page: Int) {
            onCurrentPage(page)
        }

        override fun onNextPage(page: Int) {
            onNextPage(page)
        }

        override fun onFinish() {
            onFinishPage()
        }

    }

    setOnPaginationListener(listener)
}