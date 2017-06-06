package org.tannakaken.yourtime

/**
 * Created by kensaku on 2017/06/01.
 */
object ClockList : MutableList<MyClock> by ArrayList<MyClock>()  {
    var currentClockIndex = 0

    init {
        add(MyClock("私の時間", MyClock.Ampm.AMPM, 12, 60, 60, true))
        add(MyClock("24時間時計", MyClock.Ampm.AM, 24, 60, 60, false))
        add(MyClock("仏革命暦十進化時間", MyClock.Ampm.AM, 10, 100, 100, true))
    }

    public fun swap(aFrom : Int, aTo : Int) {
        val tmp = get(aFrom)
        set(aFrom, get(aTo))
        set(aTo, tmp)
    }
}