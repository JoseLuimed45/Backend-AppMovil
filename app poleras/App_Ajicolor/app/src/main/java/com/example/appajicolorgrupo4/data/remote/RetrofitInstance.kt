package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton para crear y gestionar la instancia de Retrofit.
object RetrofitInstance {

    private val loggingInterceptor = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) okhttp3.logging.HttpLoggingInterceptor.Level.BODY else okhttp3.logging.HttpLoggingInterceptor.Level.NONE
    }

    // Interceptor para reintentar peticiones fallidas (Cold Starts / 503)
    private val retryInterceptor = okhttp3.Interceptor { chain ->
        var request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0
        val maxLimit = 3

        while (!response.isSuccessful && tryCount < maxLimit && (response.code == 503 || response.code == 504)) {
            tryCount++
            response.close()
            // Exponential backoff could be added here, but simple retry for now
            response = chain.proceed(request)
        }
        response
    }

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Aumentado para Cold Starts
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(retryInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
