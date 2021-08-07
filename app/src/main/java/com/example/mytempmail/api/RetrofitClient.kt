package com.example.mytempmail.api

import com.example.mytempmail.util.LiveDataCallAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "https://www.1secmail.com/api/v1/"

    private val gson: Gson = GsonBuilder().setLenient().create()

    private val retrofitBuilder: Retrofit.Builder by lazy {  //the benefit of using lazy is that the object will be created only when it is called otherwise it will not be created.
        // The other benefit of using lazy is that once the object is initialized, you will use the same object again when called.
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))  //Gson converter factory converts json pbjects to java objects
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
    }

    val apiService: ApiService by lazy {
        retrofitBuilder.build()
            .create(ApiService::class.java)
    }
}