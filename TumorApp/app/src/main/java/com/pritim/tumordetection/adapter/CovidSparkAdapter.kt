package com.pritim.tumordetection.adapter

import android.graphics.RectF
import com.pritim.tumordetection.responses.CovidData
import com.pritim.tumordetection.utils.Metrics
import com.pritim.tumordetection.utils.TimeScale
import com.robinhood.spark.SparkAdapter

class CovidSparkAdapter(private val dailyData: List<CovidData>) : SparkAdapter() {

    var metrics : Metrics  = Metrics.POSITIVE
    var daysAgo : TimeScale = TimeScale.MAX

    override fun getCount(): Int {
        return dailyData.size
    }

    override fun getItem(index: Int): Any {
        return dailyData[index]
    }

    override fun getY(index: Int): Float {
        val chosenDay = dailyData[index]
        return when(metrics) {
            Metrics.POSITIVE -> chosenDay.positiveIncrease.toFloat()
            Metrics.DEATH -> chosenDay.deathIncrease.toFloat()
            Metrics.NEGATIVE -> chosenDay.negativeIncrease.toFloat()
        }
    }

    override fun getDataBounds(): RectF {
        val bounts : RectF = super.getDataBounds()
        if (daysAgo != TimeScale.MAX) {
            bounts.left = count - daysAgo.numDays.toFloat()
            return bounts
        }
        return bounts
    }

}