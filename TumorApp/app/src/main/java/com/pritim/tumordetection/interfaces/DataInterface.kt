package com.pritim.tumordetection.`interface`

import com.pritim.tumordetection.data.User
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface DataInterface {

    @FormUrlEncoded
    @POST("/login")
    fun getUserData(
        @Field("nama") nama : String,
        @Field("email")email:String,
        @Field("nomor_hp") nomor_hp : Int,
        @Field("password") password : String) : Call<User>


    @FormUrlEncoded
    @POST("/signup")
    fun signup(@Field("email") email: String,
    @Field("password") password: String) : Call<User>

}