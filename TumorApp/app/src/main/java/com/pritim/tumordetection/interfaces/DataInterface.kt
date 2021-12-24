package com.pritim.tumordetection.interfaces

import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.responses.responseGetUser
import com.pritim.tumordetection.responses.responseRequestOnly
import retrofit2.Call
import retrofit2.http.*

interface DataInterface {

    @POST("/api/loginEmail")
    fun getUserData(
    @Body hashMap: HashMap<String, Any>) : Call<responseGetUser>


    @POST("/api/daftar")
    fun signup(
        @Body hashMap: HashMap<String,String>
    ) : Call<responseRequestOnly>

    @DELETE("/api/users/{id}")
    fun deleteGans(@Path("id") id : String) : Call<responseRequestOnly>


    @PUT("/api/users/{id}")
    fun update(@Path("id") userID : String,
               @Body hashMap: HashMap<String, Any>
    ) : Call<responseGetUser>

}