package com.pritim.tumordetection.data

class Recognize (var id : Int, var result : String, var confidence : Float){

    override fun toString(): String {
        return "Result ${result}, confidence : ${confidence}"
    }
}