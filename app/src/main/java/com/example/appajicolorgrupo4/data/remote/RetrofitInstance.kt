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
            android.util.Log.d("RetrofitInstance", "Retry attempt $tryCount for ${request.url} (code: ${response.code})")
            response.close()
            
            // Exponential backoff: espera 1s, 2s, 4s
            val delayMs = (1000 * Math.pow(2.0, (tryCount - 1).toDouble())).toLong()
            Thread.sleep(delayMs)
            
            response = chain.proceed(request)
        }
        
        if (!response.isSuccessful && (response.code == 503 || response.code == 504)) {
            android.util.Log.e("RetrofitInstance", "Failed after $tryCount retries for ${request.url}")
        }
        
        response
    }

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(90, java.util.concurrent.TimeUnit.SECONDS) // Aumentado para Cold Starts de Vercel
        .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
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
