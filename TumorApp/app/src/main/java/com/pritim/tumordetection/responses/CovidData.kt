package com.pritim.tumordetection.responses

import com.google.gson.annotations.SerializedName
import java.util.*

data class CovidData (
        @SerializedName("dateChecked") val dateChecked: Date,
        @SerializedName("positiveIncrease") val positiveIncrease : Int,
        @SerializedName("negaviteIncrease") val negativeIncrease : Int,
        @SerializedName("deathIncrease") val deathIncrease :Int,
        @SerializedName("state") val state: String,
        )