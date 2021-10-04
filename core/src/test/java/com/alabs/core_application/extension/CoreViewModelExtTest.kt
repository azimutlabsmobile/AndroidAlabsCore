package com.alabs.core_application.extension

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alabs.core_application.data.network.networkPrinter.TestCustomErrorResponse
import com.alabs.core_application.data.network.networkPrinter.TestError
import com.alabs.core_application.presentation.viewModel.ViewModelTest
import com.alabs.core_application.utils.extensions.launch
import com.alabs.core_application.utils.extensions.launchWithError
import com.alabs.core_application.utils.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.*
import org.koin.core.KoinComponent
import retrofit2.HttpException
import retrofit2.Response


class CoreViewModelExtTest : KoinComponent {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testingDispatcher = Dispatchers.Unconfined

    val viewModel =
        ViewModelTest()




    @Before
    fun doBeforeEach() {
        Dispatchers.setMain(testingDispatcher)
    }

    @Test
    fun `error launch viewModel`(){
        viewModel.launch({
            safeApiCall {
                throw Exception()
            }
        },{
           // do nothing
        })


    }


    @Test
    fun `success launch viewModel`(){
        viewModel.launch({
            safeApiCall {
                "Hello world"
            }
        },{
            Assert.assertEquals("Hello world", it)
        })
    }


    @Test
    fun `custom type error laymbda launch viewModel`(){
        val response: Response<*> =
            Response.error<String>(
                403,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"customer_verification_failed\",\"hello\":\"Вы ввели неверный код-пароль\",\"helloErrorResponse\":352352}"
                )
            )
        viewModel.launchWithError<HttpException, TestError>({
            safeApiCall ({
                throw HttpException(response)
            }, TestCustomErrorResponse())
        },{
            Assert.assertEquals("Hello world", it)
        },{
            Assert.assertEquals("352352", it?.helloErrorResponse)
            Assert.assertEquals("Вы ввели неверный код-пароль", it?.helloErrorResponse)
        })
    }


    @Test
    fun `custom type error livedata launch viewModel`(){
        val response: Response<*> =
            Response.error<String>(
                403,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"customer_verification_failed\",\"hello\":\"Вы ввели неверный код-пароль\",\"helloErrorResponse\":352352}"
                )
            )
        viewModel.launchWithError<HttpException, TestError>({
            safeApiCall ({
                throw HttpException(response)
            }, TestCustomErrorResponse())
        },{
            Assert.assertEquals("Hello world", it)
        })

    }

    @Test
    fun `incorrect data launch viewModel`(){
        val msg = "Ошибка : ClassCastException java.lang.String cannot be cast to java.lang.Integer"
        viewModel.launch({
            safeApiCall {
                "Hello world" as Int
            }
        },{
            Assert.assertEquals("Hello world", it)
        },{
            Assert.assertEquals(msg, it)
        })

    }

    @After
    fun doAfterEach() {
        Dispatchers.resetMain()
    }


}