package com.alabs.core_application.data.network

enum class Status {
    SHOW_LOADING,
    HIDE_LOADING,
    SHOW_PULL_TO_REFRESH_LOADING,
    HIDE_PULL_TO_REFRESH_LOADING,
    SHOW_PAGGING_LOADING,
    HIDE_PAGGING_LOADING,
    ERROR,
    SUCCESS,
    REDIRECT_LOGIN
}