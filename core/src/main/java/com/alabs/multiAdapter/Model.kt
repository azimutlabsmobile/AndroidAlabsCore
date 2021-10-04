package com.alabs.multiAdapter

import com.google.gson.annotations.SerializedName

open class SearchMultitype(
    @SerializedName("UISearch.id") open val id: String,
    @SerializedName("UISearch.value") open val value: String
)