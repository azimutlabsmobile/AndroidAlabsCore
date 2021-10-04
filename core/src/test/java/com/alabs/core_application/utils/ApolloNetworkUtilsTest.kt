package com.alabs.core_application.utils

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.alabs.core_application.data.network.ResultApi
import com.alabs.core_application.di.createApolloOkHttpClient
import com.alabs.core_application.utils.mock.MockApollo
import com.alabs.core_application.utils.network.apolloSafeApiCall
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class ApolloNetworkUtilsTest : KoinTest {


    private var mock = MockApollo(UnsupportedOperationException())
    private val server = MockWebServer()
    private val context = mock<Context>()
    private val apolloService = ApolloClient.builder().serverUrl(server.url("/")).okHttpClient(
        createApolloOkHttpClient()
    ).build()


    @Test
    fun `test 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        runBlocking {
            Assert.assertEquals(
                apolloService.apolloSafeApiCall { mock },
                ResultApi.HttpError("Срок действия сессии истек", 401)
            )
        }
    }


    @Test
    fun `test 404`() {
        server.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            Assert.assertEquals(
                apolloService.apolloSafeApiCall { mock },
                ResultApi.HttpError("Данного запроса не существует", 404)
            )
        }
    }

    @Test
    fun `test 200`() {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                "{\n" +
                        "  \"hero\": {\n" +
                        "    \"name\": \"Luke Skywalker\"\n" +
                        "  }\n" +
                        "}"
            )
        )
        runBlocking {
            val data = apolloService.apolloSafeApiCall { mock }
            Assert.assertEquals(
                data,
                ResultApi.Success((data as? ResultApi.Success)?.data)
            )
        }
    }

    @Test
    fun `test error parse data`() {
        server.enqueue(MockResponse().setResponseCode(200))

        runBlocking {
            Assert.assertEquals(
                apolloService.apolloSafeApiCall { mock },
                ResultApi.HttpError("Невозможно распарсить данные", 0)
            )
        }
    }


    @After
    fun tearDown() {
        stopKoin()
    }
}