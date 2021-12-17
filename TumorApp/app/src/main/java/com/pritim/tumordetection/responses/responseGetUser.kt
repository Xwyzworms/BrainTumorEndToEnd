package com.pritim.tumordetection.responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable
 class responseGetUser (
         @field:SerializedName("message")
          var messsage : String ?= null,
        @field:SerializedName("code")
         var code : String ?= null ,
         @field:SerializedName("data")
        var data: HashMap<String, Any> ?= HashMap()
        )