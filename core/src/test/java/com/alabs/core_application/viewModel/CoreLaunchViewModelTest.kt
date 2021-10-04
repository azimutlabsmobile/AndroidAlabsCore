package com.alabs.core_application.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alabs.core_application.data.network.LoadingType
import com.alabs.core_application.data.network.networkPrinter.TestCustomErrorResponse
import com.alabs.core_application.data.network.networkPrinter.TestError
import com.alabs.core_application.presentation.viewModel.ViewModelLaunchTest
import com.alabs.core_application.utils.exception.ApolloSuccessException
import com.alabs.core_application.utils.network.safeApiCall
import com.alabs.core_application.utils.rule.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.*
import retrofit2.HttpException
import retrofit2.Response


class CoreLaunchViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testingDispatcher = Dispatchers.Unconfined
    private val viewModel = ViewModelLaunchTest()


    @Before
    fun doBeforeEach() {
        Dispatchers.setMain(testingDispatcher)
    }

    @Test
    fun `error launch IO viewModel`() {
        viewModel.launch(dispatcher = Dispatchers.IO, block = {
            safeApiCall {
                throw Exception()
            }
        }, result = {
            // do nothing
        })
    }

    @Test
    fun `error launch loading viewModel`() {
        viewModel.launch(dispatcher = Dispatchers.Main, block = {
            safeApiCall {
                throw Exception()
            }
        }, result = {
            // do nothing
        }, loading = {
            if (it) {
                Assert.assertEquals(it, true)
            } else {
                Assert.assertEquals(it, false)
            }
        })
    }

    @Test
    fun `error launchZip viewModel`() {
        viewModel.launch(dispatcher = Dispatchers.IO, block = {
            safeApiCall {
                Pair(throw Exception("Ошибка launchZip"), "")
            }

        }, result = {
            // do nothing
        }, errorBlock = {
            Assert.assertEquals(it, "launchZip")
        }, loadingType = LoadingType.PULL_TO_REFRESH)

    }


    @Test
    fun `error launch viewModel`() {
        viewModel.launch(dispatcher = Dispatchers.Main, block = {
            safeApiCall {
                throw Exception()
            }
        }, result = {
            // do nothing
        })
    }


    @Test
    fun `success launch viewModel`() {
        viewModel.launch({
            safeApiCall {
                "Hello world"
            }
        }, {
            Assert.assertEquals("Hello world", it)
        })

    }


    @Test
    fun `success launchZip viewModel`() {
        viewModel.launch({
            safeApiCall {
                Pair("Hello world", "Hello world")
            }
        }, {
            Assert.assertEquals(it, Pair("Hello world", "Hello world"))
        })
    }


    @Test
    fun `custom type error laymbda launch viewModel`() = runBlocking {
        val response: Response<*> =
            Response.error<String>(
                403,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"customer_verification_failed\",\"hello\":\"Вы ввели неверный код-пароль\",\"helloErrorResponse\":352352}"
                )
            )
        viewModel.launchWithError<HttpException, TestError>({
            safeApiCall({
                throw HttpException(response)
            }, TestCustomErrorResponse())
        }, {
            Assert.assertEquals("Hello world", it)
        }, {
            Assert.assertEquals("352352", it?.helloErrorResponse)
            Assert.assertEquals("Вы ввели неверный код-пароль", it?.hello)
        })
    }

    @Test
    fun `custom type error laymbda launchFlow viewModel`() = runBlocking {
        val response: Response<*> =
            Response.error<String>(
                403,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"customer_verification_failed\",\"hello\":\"Вы ввели неверный код-пароль\",\"helloErrorResponse\":352352}"
                )
            )
        viewModel.launchFlowWithError<HttpException, TestError>(
            errorPrinter = TestCustomErrorResponse(),
            requestFlow = {
                flow {
                    emit(throw HttpException(response))
                }
            },
            result = {
                Assert.assertEquals("Hello world", it)
            },
            errorBlock = {
                Assert.assertEquals("352352", it?.helloErrorResponse)
                Assert.assertEquals("Вы ввели неверный код-пароль", it?.hello)
            })
    }


    @Test
    fun `custom type error livedata launch viewModel`() = runBlocking {
        val response: Response<*> =
            Response.error<String>(
                403,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"customer_verification_failed\",\"hello\":\"Вы ввели неверный код-пароль\",\"helloErrorResponse\":352352}"
                )
            )
        viewModel.launchWithError<HttpException, TestError>({
            safeApiCall({
                throw HttpException(response)
            }, TestCustomErrorResponse())
        }, {
            Assert.assertEquals("Вы ввели неверный код-пароль", it)
        })
    }

    @Test
    fun `incorrect data launch viewModel`() {
        val msg = "Ошибка : ClassCastException java.lang.String cannot be cast to java.lang.Integer"
        viewModel.launch({
            safeApiCall {
                "Hello world" as Int
            }
        }, {
            Assert.assertEquals("Hello world", it)
        }, {
            Assert.assertEquals(msg, it)
        })
    }


    @Test
    fun `404 launchFlow`() {
        val response: Response<*> =
            Response.error<String>(
                404,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"Вы поймали ошибку 404\"}"
                )
            )


        viewModel.launchFlow({
            flow {
                emit(throw HttpException(response))
            }
        }, {

        }, {
            Assert.assertEquals(it, "Вы поймали ошибку 404")
        })
    }


    @Test
    fun `401 launchFlow`() {
        val response: Response<*> =
            Response.error<String>(
                401,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"Вы поймали ошибку 401\"}"
                )
            )


        viewModel.launchFlow(dispatcher = Dispatchers.Main, requestFlow = {
            flow {
                emit(throw HttpException(response))
            }
        }, result = {

        }, errorBlock = {
            Assert.assertEquals(it, "Вы поймали ошибку 401")
        })

    }


    @Test
    fun `result success launchFlow`() {
        viewModel.launchFlow({
            flow {
                emit("result success launchFlow")
            }
        }, {
            Assert.assertEquals(it, "result success launchFlow")
        }, {

        })
    }

    @Test
    fun `result ApolloSuccessException launchFlow`() {
        viewModel.launchFlow({
            flow {
                emit(throw ApolloSuccessException("ApolloSuccessException"))
            }
        }, {
        }, {
            Assert.assertEquals(it, "ApolloSuccessException")
        })
    }


    @After
    fun doAfterEach() {
        Dispatchers.resetMain()
    }

}