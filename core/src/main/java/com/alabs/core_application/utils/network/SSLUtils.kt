package com.alabs.core_application.utils.network

import okhttp3.OkHttpClient
import java.security.cert.CertificateException
import javax.net.ssl.*


/**
 * Фейковы FakeX509TrustManager в дальшейшем можно добавить различные сертификаты
 */
class FakeX509TrustManager : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(
        arg0: Array<java.security.cert.X509Certificate?>?,
        arg1: String?
    ) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(
        arg0: Array<java.security.cert.X509Certificate?>?,
        arg1: String?
    ) {
        // do nothing
    }

    override fun getAcceptedIssuers() =
        _acceptedIssuers

    companion object {
        private val _acceptedIssuers: Array<java.security.cert.X509Certificate> = arrayOf()
    }
}




fun getUnsafeSocketFactoryForDevelopment(): Pair<SSLSocketFactory, Array<TrustManager>> {

    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            chain: Array<java.security.cert.X509Certificate>,
            authType: String
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            chain: Array<java.security.cert.X509Certificate>,
            authType: String
        ) {
        }

        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
            return arrayOf()
        }
    })

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())

    return (sslContext.socketFactory to trustAllCerts)
}


fun getUnsafeOkHttp() : OkHttpClient{
    val (factory, trust) = getUnsafeSocketFactoryForDevelopment()
    val builder = OkHttpClient.Builder()
    builder.sslSocketFactory(factory, (trust.get(0) as X509TrustManager))
    builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
    return builder.build()
}

fun getSsl() =
    Pair(
        FakeX509TrustManager(),
        getUnsafeSocketFactoryForDevelopment().first
    )