package com.pritim.tumordetection.utils

enum class Metrics{
    NEGATIVE, POSITIVE, DEATH
}
enum class TimeScale( val numDays : Int)  {

    WEEK(7),
    MONTH(30),
    MAX(-1)
};
