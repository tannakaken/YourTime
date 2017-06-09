package org.tannakaken.yourtime

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by kensaku on 2017/06/01.
 * 時計のリストを保持するシングルトン
 */
object ClockList : MutableList<MyClock> by ArrayList<MyClock>()  {
    var currentClockIndex = 0

    fun init(aContext: Context) {
        if (dataExists(aContext)) {
            load(aContext)
        } else {
            add(MyClock("私の時間", MyClock.Ampm.AMPM, 12, 60, 60, true))
            add(MyClock("24時間時計", MyClock.Ampm.AM, 24, 60, 60, false))
            add(MyClock("仏革命暦十進化時間", MyClock.Ampm.AM, 10, 100, 100, true))
        }
    }

    fun swap(aFrom : Int, aTo : Int) {
        val tmp = get(aFrom)
        set(aFrom, get(aTo))
        set(aTo, tmp)
    }

    private val storage_file = "clock.csv"

    private fun dataExists(aContext : Context) : Boolean = aContext.getFileStreamPath(storage_file).exists()

    private fun load(aContext: Context) {
        val reader = BufferedReader(InputStreamReader(aContext.openFileInput(storage_file)))
        do {
            val line = reader.readLine()
            if (line == null) {
                break
            }
            val vals  = line.split("\t")
            add(MyClock(vals[0],MyClock.Ampm.values()[vals[1].toInt() - 1], vals[2].toInt(), vals[3].toInt(), vals[4].toInt(), vals[5].toBoolean()))
        } while (true)
    }

    fun save(aContext: Context) {
        val stream = aContext.openFileOutput(storage_file, Context.MODE_PRIVATE)
        for (clock in this) {
            stream.write((clock.name + "\t" + clock.ampm.rowValue + "\t" + clock.hours + "\t" + clock.minutes + "\t" + clock.seconds + "\t" + clock.dialFromOne + "\n").toByteArray())
        }
    }
}