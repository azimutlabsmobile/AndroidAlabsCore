package com.alabs.core_application.utils


import com.google.gson.JsonParseException
import com.alabs.core_application.data.network.ResultApi
import com.alabs.core_application.data.network.networkPrinter.TestCustomErrorResponse
import com.alabs.core_application.data.network.networkPrinter.TestError
import com.alabs.core_application.utils.network.safeApiCall
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException


class NetworkUtilsTest {


    @Test
    fun `safeApiCall SocketTimeoutException test`() {
        runBlocking {
            assertEquals(safeApiCall {
                throw SocketTimeoutException()
            }, ResultApi.HttpError("Сервер не отвечает"))
        }
    }

    @Test
    fun `safeApiCall SSLHandshakeException test`() {
        runBlocking {
            assertEquals(safeApiCall {
                throw SSLHandshakeException("")
            }, ResultApi.HttpError("Возникли проблемы с сертификатом"))
        }
    }

    @Test
    fun `safeApiCall JsonParseException test`() {
        runBlocking {
            assertEquals(safeApiCall {
                throw JsonParseException("")
            }, ResultApi.HttpError("Ошибка обработки запроса"))
        }
    }

    @Test
    fun `safeApiCall EOFException test`() {
        runBlocking {
            assertEquals(safeApiCall {
                throw EOFException("")
            }, ResultApi.HttpError("Ошибка загрузки, попробуйте еще раз"))
        }
    }

    @Test
    fun `safeApiCall ConnectException test`() {
        runBlocking {
            assertEquals(safeApiCall {
                throw ConnectException("")
            }, ResultApi.HttpError("Возникли проблемы с интернетом"))
        }
    }

    @Test
    fun `safeApiCall UnknownHostException test`() {
        runBlocking {
            assertEquals(safeApiCall {
                throw UnknownHostException("")
            }, ResultApi.HttpError("Возникли проблемы с интернетом"))
        }
    }

    @Test
    fun `safeApiCall IOException test`() {
        runBlocking {
            assertEquals(safeApiCall {
                throw IOException("IOException print")
            }, ResultApi.HttpError("IOException print", HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }

    @Test
    fun `safeApiCall Another test`() {
        runBlocking {
            assertEquals(
                safeApiCall {
                    "3r352352gdsgs" as Int
                },
                ResultApi.HttpError("Ошибка : ClassCastException java.lang.String cannot be cast to java.lang.Integer")
            )
        }
    }


    @Test
    fun `safeApiCall get field from error body test`() = runBlocking {
        val response: Response<*> =
            Response.error<String>(
                403,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"customer_verification_failed\",\"hello\":\"Вы ввели неверный код-пароль\",\"helloErrorResponse\":352352}"
                )
            )
        val responseSafe = safeApiCall({
            throw HttpException(response)
        }, TestCustomErrorResponse())

        assertEquals((responseSafe as ResultApi.HttpError<TestError>).error?.hello,"Вы ввели неверный код-пароль")
        assertEquals((responseSafe as ResultApi.HttpError<TestError>).error?.helloErrorResponse,"352352")
    }

}

suspend fun  a () = safeApiCall ({

},TestCustomErrorResponse())

