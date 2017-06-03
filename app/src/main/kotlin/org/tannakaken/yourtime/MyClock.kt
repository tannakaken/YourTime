package org.tannakaken.yourtime

/**
 * Created by kensaku on 2017/05/31.
 * カスタマイズされた時計システムを表すクラス。
 */
data class MyClock(val name: String, val ampm: Ampm, val hours: Int, val minutes: Int, val seconds: Int, val dialFromOne: Boolean) {

    val oneDay : Long = 1000L * 60 * 60 * 24
    val mySecond = (oneDay / (seconds * minutes * hours * ampm.rowValue)).toInt()
    val myMinute = mySecond * seconds
    val myHour = myMinute * minutes

    fun calcNow(millisecond: Long): Now {

        val hour = (millisecond / myHour % myHour).toInt()
        val restOfHour = (millisecond % myHour).toInt()
        val minute = restOfHour / myMinute
        val restOfMinute = restOfHour % myMinute
        val second = restOfMinute / mySecond
        return Now(hour, minute, second)
    }

    /**
     *
     * Created by kensaku on 2017/06/01.
     * 現在時刻をカスタマイズされた時間システムで表現したデータクラス
     */
    data class Now(val hour: Int, val minute: Int, val second: Int)

    /**
     * Created by kensaku on 2017/06/01.
     * 午前午後システムを表現する列挙クラス。
     */
    enum class Ampm(val rowValue: Int) {
        AM(1),
        AMPM(2),
        AMMMPM(3)
    }
}