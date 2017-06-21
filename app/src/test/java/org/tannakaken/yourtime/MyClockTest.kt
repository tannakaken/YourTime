package org.tannakaken.yourtime

import org.junit.Test

import org.junit.Assert.*

/**
 * [MyClock]のテストクラス
 * Created by kensaku on 2017/06/20.
 */
class MyClockTest {
    @Test
    fun 通常の時間をテストする() {
        val tMyClock = MyClock("私の時間", MyClock.Ampm.AMPM, 12, 60, 60, true, true)
        val tMillisecond : Long = ((13 * 60 + 2) * 60 + 3 ) * 1000
        assertEquals("私の時間", tMyClock.name)
        val tNow = tMyClock.calcNow(tMillisecond)
        assertEquals(1, tNow.hour)
        assertEquals(2, tNow.minute)
        assertEquals(3, tNow.second)
    }

    @Test
    fun 二十四時間時計をテストする() {
        val tMyClock = MyClock("二十四時間制時計", MyClock.Ampm.AM, 24, 60, 60, true, true)
        val tMillisecond : Long = ((13 * 60 + 2) * 60 + 3 ) * 1000
        val tNow = tMyClock.calcNow(tMillisecond)
        assertEquals(13, tNow.hour)
        assertEquals(2, tNow.minute)
        assertEquals(3, tNow.second)
    }

    @Test
    fun 一日を三つに区分してみよう() {
        val tMyClock = MyClock("午前午後ともう一つある時計", MyClock.Ampm.AMMMPM, 8, 60, 60, true, true)
        val tMillisecond : Long = ((13 * 60 + 2) * 60 + 3 ) * 1000
        val tNow = tMyClock.calcNow(tMillisecond)
        assertEquals(5, tNow.hour)
        assertEquals(2, tNow.minute)
        assertEquals(3, tNow.second)
    }

    @Test
    fun 十進時間をテストする() {
        val tMyClock = MyClock("十進時間", MyClock.Ampm.AM, 10, 100, 100, true, true)
        val tMillisecond : Long = ((1 * 100 + 2) * 100 + 3) * 864
        val tNow = tMyClock.calcNow(tMillisecond)
        assertEquals(1, tNow.hour)
        assertEquals(2, tNow.minute)
        assertEquals(3, tNow.second)
    }

    @Test
    fun 時計の向きをテストする() {
        val tMyClock = MyClock("私の時間", MyClock.Ampm.AMPM, 12, 60, 60, true, true)
        assertEquals(tMyClock.sig, 1.0, 0.0)
        val tMyClock2 = MyClock("私の時間", MyClock.Ampm.AMPM, 12, 60, 60, true, false)
        assertEquals(tMyClock2.sig, -1.0, 0.0)
    }
}