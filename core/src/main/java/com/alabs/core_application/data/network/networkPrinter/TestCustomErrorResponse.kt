package com.alabs.core_application.data.network.networkPrinter

import com.alabs.core_application.utils.parseJson

class TestCustomErrorResponse : NetworkErrorHttpPrinter<TestError> {
    override fun print(response: String?, default: String?) =
        parseJson(response) ?: TestError(helloErrorResponse = default)
}


data class TestError(
    var hello: String? = null,
    var helloErrorResponse: String? = null
)