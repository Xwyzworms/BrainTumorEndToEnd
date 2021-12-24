package com.pritim.tumordetection

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pritim.tumordetection.NetworkModule.NetworkModule
import com.pritim.tumordetection.R
import com.pritim.tumordetection.adapter.CovidSparkAdapter
import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.responses.CovidData
import com.pritim.tumordetection.utils.Metrics
import com.pritim.tumordetection.utils.TimeScale
import com.robinhood.spark.SparkView
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import okhttp3.internal.Util
import org.angmarch.views.NiceSpinner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class CovidViz : AppCompatActivity(){
    private val TAG : String = "CovidViz"
    private val ALL_STATES : String = "All (NationWide)"
    private lateinit var adapter : CovidSparkAdapter
    private lateinit var perStateDailyData: Map<String, List<CovidData>>
    private lateinit var nationalDailyData : List<CovidData>
    private lateinit var currentlyShownData : List<CovidData>

    private lateinit var  tvMetricsLabel :  TickerView
    private lateinit var  tvDateLabel : TextView

    private lateinit var bottomNavigation : BottomNavigationView

    private lateinit var radiobuttonWeek : RadioButton
    private lateinit var radioButtonMonth: RadioButton
    private lateinit var radioButtonMax : RadioButton

    private lateinit var radioButtonNegative: RadioButton
    private lateinit var radioButtonPositive: RadioButton
    private lateinit var radioButtonDeath : RadioButton

    private lateinit var sparkView : SparkView

    private lateinit var radioGroupTimeSelection : RadioGroup
    private  lateinit var  radioGroupDataCollection : RadioGroup
    private lateinit var spinner : NiceSpinner
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.covid_viz)
        init()
        settingUpTheBottomNavigation()
        user = intent.getSerializableExtra("DATA_USER") as User
        // Fetch the National Data

            NetworkModule.getCovidService().getNationalData().enqueue(object : Callback<List<CovidData>> {
                override fun onResponse(call: Call<List<CovidData>>, response: Response<List<CovidData>>) {
                    Log.i(TAG, "onResponse $response")
                    val nationalData = response.body()
                    if (nationalData == null) {
                        Log.w(TAG, "Did Not receive a Valid Response Body")
                        return
                    }
                    setupEventListener()
                    nationalDailyData = nationalData.reversed()
                    Log.i(TAG,"Update Graph With NationlData")
                    updateDisplayWithData(nationalDailyData)

                }

                override fun onFailure(call: Call<List<CovidData>>, t: Throwable) {
                    Log.d(TAG,"onFailure ${t.message}")
                }

            })
        // fetch the state Data
        NetworkModule.getCovidService().getStateData().enqueue(object : Callback<List<CovidData>> {
            override fun onResponse(call: Call<List<CovidData>>, response: Response<List<CovidData>>) {
                Log.i(TAG, "onResponse $response")
                val statesData = response.body()
                if (statesData == null) {
                    Log.w(TAG, "Did Not Recevice A Valid Response Body")
                    return
                }
                perStateDailyData = statesData.reversed().groupBy { it.state }
                Log.i(TAG,perStateDailyData.toString())
                updateStateData(perStateDailyData.keys)
            }

            override fun onFailure(call: Call<List<CovidData>>, t: Throwable) {
                Log.d(TAG, "onFailure ${t.message}")
            }
        })
    }


    private fun updateStateData(StateData : Set<String>) {
        val listOfStateData : MutableList<String> = StateData.toMutableList()
        listOfStateData.sort()
        listOfStateData.add(0, ALL_STATES)

        spinner.attachDataSource(listOfStateData)
        spinner.setOnSpinnerItemSelectedListener { parent, _, position, _->
            val selectedState : String = spinner.getItemAtPosition(position) as String

            val dataForThatState : List<CovidData> = perStateDailyData[selectedState] ?: nationalDailyData
            updateDisplayWithData(dataForThatState)
        }

    }
    private fun setupEventListener() {
        tvMetricsLabel.setCharacterLists(TickerUtils.provideNumberList())
        sparkView.isScrubEnabled = true
        sparkView.setScrubListener {  item_selected ->
            if (item_selected is CovidData) {
                updateInfoForDate(item_selected)
            }
        }

        radioGroupTimeSelection.setOnCheckedChangeListener { _, checkedId ->
            adapter.daysAgo = when (checkedId) {
                R.id.cv_radioButtonWeek -> TimeScale.WEEK
                R.id.cv_radioButtonMonth -> TimeScale.MONTH
                else -> TimeScale.MAX
            }
            adapter.notifyDataSetChanged()
        }
        radioGroupDataCollection.setOnCheckedChangeListener{_,checkedId ->
            when(checkedId) {
                R.id.cv_radioButtonPositive -> updateDisplayedMetric(Metrics.POSITIVE)
                R.id.cv_radioButtonNegative -> updateDisplayedMetric(Metrics.NEGATIVE)
                else -> updateDisplayedMetric(Metrics.DEATH)
            }
        }
    }
    private fun updateDisplayedMetric(metrics: Metrics) {
        val colorRes : Int = when (metrics) {
            Metrics.DEATH -> {
               R.color.numberDeaths
            }
            Metrics.POSITIVE -> {
                R.color.positiveCases
            }
            else -> {
                R.color.negativeCases
            }
        }
        @ColorInt val colorInt = ContextCompat.getColor(this@CovidViz,colorRes)
        sparkView.lineColor =colorInt
        tvMetricsLabel.setTextColor(colorInt)

        adapter.metrics = metrics
        adapter.notifyDataSetChanged()

        //Resetting Bottom TextViews
        updateInfoForDate(currentlyShownData.last())

    }
    private fun init() {
        tvDateLabel = findViewById(R.id.cv_tvDateLabel)
        tvMetricsLabel = findViewById(R.id.cv_tvMetricLabel)
        radiobuttonWeek =findViewById(R.id.cv_radioButtonWeek)
        radioButtonMax = findViewById(R.id.cv_radioButtonMax)
        radioButtonMonth = findViewById(R.id.cv_radioButtonMonth)
        radioButtonNegative = findViewById(R.id.cv_radioButtonNegative)
        radioButtonDeath = findViewById(R.id.cv_radioButtonDeaths)
        radioButtonPositive  = findViewById(R.id.cv_radioButtonPositive)
        radioGroupTimeSelection = findViewById(R.id.cv_radioGroupTimeSelection)
        radioGroupDataCollection = findViewById(R.id.cv_radioGroupMetricSelection)
        spinner = findViewById(R.id.cv_spinnerSelect)
        sparkView = findViewById(R.id.cv_sparkView)
        bottomNavigation = findViewById(R.id.cv_beranda)
    }


    private fun updateDisplayWithData(dailyData : List<CovidData>) {
        currentlyShownData = dailyData
        adapter = CovidSparkAdapter(dailyData)
        sparkView.adapter = adapter
        radioButtonPositive.isChecked = true
        radioButtonMax.isChecked = true
        updateDisplayedMetric(Metrics.POSITIVE)
    }

    private fun updateInfoForDate(covidData: CovidData) {
        // Updating Info Per Date Selected
        val totalCases : Int = when( adapter.metrics) {
            Metrics.POSITIVE -> covidData.positiveIncrease
            Metrics.NEGATIVE -> covidData.negativeIncrease
            Metrics.DEATH -> covidData.deathIncrease
        }

        tvMetricsLabel.text = totalCases.toString()
        val simpleDateFormatter : SimpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        var date_selected = simpleDateFormatter.format(covidData.dateChecked)
        tvDateLabel.text = date_selected

    }

    private fun settingUpTheBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_beranda
        bottomNavigation.itemBackground = ColorDrawable(resources.getColor(R.color.white,null))

        bottomNavigation.setOnItemSelectedListener { it->
            when(it.itemId) {
                R.id.nav_predict -> {
                    val nav_settings : Intent = Intent(this@CovidViz, InferenceModel::class.java)
                    nav_settings.putExtra("DATA_USER",user)
                    nav_settings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(nav_settings)
                    finish()
                }
                else -> {
                    val nav_settings : Intent = Intent(this@CovidViz, update_profil::class.java)
                    nav_settings.putExtra("DATA_USER",user)
                    nav_settings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(nav_settings)
                    finish()
                }

            }

             true
        }
    }

}