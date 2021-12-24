package com.pritim.tumordetection.NetworkModule

import com.google.gson.GsonBuilder
import com.pritim.tumordetection.interfaces.CovidService
import com.pritim.tumordetection.interfaces.DataInterface
import com.pritim.tumordetection.responses.CovidData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val GSON  = GsonBuilder().create()
    val GSONDate = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
    val BASE_URL : String = "http://10.0.2.2:9999"
    val BASE_URLCOVID : String = "https://api.covidtracking.com/v1/"
    fun getRetrofit() : Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GSON)).build()
    }
    fun getCovidData() : Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URLCOVID).addConverterFactory(GsonConverterFactory.create(GSONDate)).build()
    }
    fun getCovidService() : CovidService = getCovidData().create(CovidService::class.java)
    fun service() : DataInterface = getRetrofit().create(DataInterface::class.java)
}

