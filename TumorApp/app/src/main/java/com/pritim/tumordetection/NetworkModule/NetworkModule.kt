package com.pritim.tumordetection.NetworkModule

import com.google.gson.GsonBuilder
import com.pritim.tumordetection.interfaces.DataInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val GSON  = GsonBuilder().setLenient().create()
    val BASE_URL : String = "http://10.0.2.2:9999"
    fun getRetrofit() : Retrofit {

        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GSON)).build()

    }
    fun service() : DataInterface = getRetrofit().create(DataInterface::class.java)
}

