package com.pritim.tumordetection.responses

import com.google.gson.annotations.SerializedName

class responseRequestOnly {
    @field:SerializedName("message")
    val message : String ?= null
    @field:SerializedName("code")
    val code : String ?= null
}