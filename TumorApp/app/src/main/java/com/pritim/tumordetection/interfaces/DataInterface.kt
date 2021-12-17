package com.pritim.tumordetection.interfaces

import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.responses.responseGetUser
import com.pritim.tumordetection.responses.responseRequestOnly
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface DataInterface {

    @POST("/login")
    fun getUserData(
    @Body hashMap: HashMap<String, Any>) : Call<responseGetUser>


    @POST("/signup")
    fun signup(
        @Body hashMap: HashMap<String,String>
    ) : Call<responseRequestOnly>

}