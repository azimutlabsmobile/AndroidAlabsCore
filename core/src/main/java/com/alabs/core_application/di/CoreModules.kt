package com.alabs.core_application.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.http.ApolloHttpCache
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.constants.CoreConstant.APOLLO_CACHE_DIRECTORY_NAME
import com.alabs.core_application.data.constants.CoreConstant.CACHE_SIZE_IN_BYTES
import com.alabs.core_application.data.constants.CoreVariables
import com.alabs.core_application.data.constants.CoreVariables.APOLLO_CACHE_ENABLED
import com.alabs.core_application.data.constants.CoreVariables.APOLLO_INTERCEPTORS
import com.alabs.core_application.data.constants.CoreVariables.BASE_APOLLO_URL
import com.alabs.core_application.data.constants.CoreVariables.IS_PRODUCTION
import com.alabs.core_application.data.constants.CoreVariables.NETWORK_APOLLO_INTERCEPTORS
import com.alabs.core_application.data.constants.CoreVariables.PREF_SOURCES_LOCAL
import com.alabs.core_application.data.constants.CoreVariables.REST_INTERCEPTORS
import com.alabs.core_application.data.constants.CoreVariables.REST_NETWORK_INTERCEPTORS
import com.alabs.core_application.data.prefs.LanguageDataSource
import com.alabs.core_application.data.prefs.SecurityDataSource
import com.alabs.core_application.data.prefs.SourcesLocalDataSource
import com.alabs.core_application.data.repository.CoreAuthUserDateRepository
import com.alabs.core_application.domain.auth.useCase.*
import com.alabs.core_application.presentation.viewModel.CoreAuthViewModel
import com.alabs.core_application.utils.network.getSsl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

val coreModule = module {
    factory { createRetrofitOkHttpClient() }

    single {
        SecurityDataSource(
            androidContext().getSharedPreferences(
                PREF_SOURCES_LOCAL,
                Context.MODE_PRIVATE
            )
        )
    }

    single {
        SourcesLocalDataSource(
            androidContext().getSharedPreferences(
                PREF_SOURCES_LOCAL,
                Context.MODE_PRIVATE
            )
        )
    }

    single { createApollo(createApolloOkHttpClient(), androidContext()) }
    single {
        LanguageDataSource(
            androidContext().getSharedPreferences(
                PREF_SOURCES_LOCAL,
                Context.MODE_PRIVATE
            )
        )
    }
}


val coreRepositoryModule = module {
    factory {
        CoreAuthUserDateRepository(get())
    }
}

val coreUseCaseModule = module {
    factory {
        CoreCheckAuthUserUseCase(get())
    }
    factory {
        CoreGetPhoneNumberUseCase(get())
    }
    factory {
        CoreSavePhoneNumberUseCase(get())
    }
    factory {
        CoreGetAccessTokenUseCase(get())
    }
    factory {
        CoreGetRefreshTokenUseCase(get())
    }
    factory {
        CoreGetNameUserUseCase(get())
    }
    factory {
        CoreSetNameUserUseCase(get())
    }
    factory {
        CoreGetUserAvatarUseCase(get())
    }
    factory {
        CoreSetUserAvatarUseCase(get())
    }
    factory {
        CoreRemoveUserAvatarUseCase(get())
    }
    factory {
        CoreIsPendingAuthorizationPassedUseCase(get())
    }
    factory {
        CoreSetPendingAuthorizationPassedUseCase(get())
    }
    factory {
        CoreResetPendingAuthorizationPassedUseCase(get())
    }
}

val coreViewModel = module {
    viewModel {
        CoreAuthViewModel(get(), get())
    }
}

/*
* Логирование запросов отключается либо для production среды, либо при включенном кэшировании
*/
private val httpLoggingInterceptorLevel =
    if (IS_PRODUCTION || APOLLO_CACHE_ENABLED) {
        NONE
    } else {
        BODY
    }

fun getCoreDIModule() =
    arrayListOf(coreModule, coreRepositoryModule, coreUseCaseModule, coreViewModel)

/**
 * OkHttpClient использовать только для retrofit
 */
fun createRetrofitOkHttpClient(): OkHttpClient {
    val (manager, factory) = getSsl()
    val httpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(httpLoggingInterceptorLevel)
    val okHttpBuilder = OkHttpClient.Builder()
        .connectTimeout(CoreConstant.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(CoreConstant.READ_TIMEOUT, TimeUnit.MILLISECONDS)

    REST_INTERCEPTORS?.let { interceptors ->
        interceptors.forEach {
            okHttpBuilder.addInterceptor(it)
        }
    }
    REST_NETWORK_INTERCEPTORS?.let { interceptors ->
        interceptors.forEach {
            okHttpBuilder.addNetworkInterceptor(it)
        }
    }
    okHttpBuilder.addInterceptor(httpLoggingInterceptor)
    okHttpBuilder.sslSocketFactory(factory, manager)
    okHttpBuilder.hostnameVerifier(HostnameVerifier { _, _ -> true })
    return okHttpBuilder.build()
}

/**
 * OkHttpClient использовать только для Apollo
 */
fun createApolloOkHttpClient(): OkHttpClient {
    val (manager, factory) = getSsl()
    val httpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(httpLoggingInterceptorLevel)
    val okHttpBuilder = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(CoreConstant.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(CoreConstant.READ_TIMEOUT, TimeUnit.MILLISECONDS)

    APOLLO_INTERCEPTORS?.let { interceptors ->
        interceptors.forEach {
            okHttpBuilder.addInterceptor(it)
        }
    }

    NETWORK_APOLLO_INTERCEPTORS?.let { interceptors ->
        interceptors.forEach {
            okHttpBuilder.addNetworkInterceptor(it)
        }
    }
    okHttpBuilder.sslSocketFactory(factory, manager)
    okHttpBuilder.hostnameVerifier(HostnameVerifier { _, _ -> true })
    return okHttpBuilder.build()
}

/**
 * Создание api сервисов для Retrofit
 */
inline fun <reified T> createWebService(
    okHttpClient: OkHttpClient,
    baseUrl: String = CoreVariables.BASE_URL
): T = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create(createGson()))
    .build()
    .create(T::class.java)

fun createGson(): Gson = GsonBuilder().setLenient().create()

/**
 * Создание сервиса для Apollo
 */
fun createApollo(okHttp: OkHttpClient, context: Context): ApolloClient {
    val apolloClientBuilder = ApolloClient
        .builder()
        .serverUrl(BASE_APOLLO_URL)

    if (APOLLO_CACHE_ENABLED) {
        val cacheFile = File(context.applicationContext.cacheDir, APOLLO_CACHE_DIRECTORY_NAME)
        val cacheStore = DiskLruHttpCacheStore(cacheFile, CACHE_SIZE_IN_BYTES)
        apolloClientBuilder.httpCache(ApolloHttpCache(cacheStore))
    }

    return apolloClientBuilder
        .okHttpClient(okHttp)
        .build()
}