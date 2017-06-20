package org.tannakaken.yourtime

/**
 * Created by kensaku on 2017/05/31.
 * カスタマイズされた時計システムを表すクラス
 * @property name 時計の名前
 * @property ampm 一日を何等分するか
 * @property hours 一区切り何時間か
 * @property minutes 一時間何分か
 * @property seconds 一分何秒か
 * @property dialFromOne 文字盤は１から始めるか０から始めるか
 * @property clockwise 針の動きは右回りか左回りか
 */
data class MyClock(val name: String, val ampm: Ampm, val hours: Int, val minutes: Int, val seconds: Int, val dialFromOne: Boolean, val clockwise: Boolean) {

    /**
     * 通常の時計システムで一日何秒か
     */
    private val ONEDAY: Long = 1000L * 60 * 60 * 24
    /**
     * 我々の一秒が通常の時計システムで何秒か
     */
    private val mySecond = (ONEDAY / (seconds * minutes * hours * ampm.division)).toInt()
    /**
     * 我々の一分が通常の時計システムで何秒か
     */
    private val myMinute = mySecond * seconds
    /**
     * 我々の一時間が通常の時計システムで何秒か
     */
    private val myHour = myMinute * minutes

    /**
     * 時計回りすなわち右回りなら角度はプラス。反時計回りすなわち左回りなら角度はマイナス
     */
    val sig : Double
        get() = if (clockwise) 1.0 else -1.0

    /**
     * @param aMillisecond 一日の中の現在の時刻をミリセカンドで表した数
     * @return この時計システムでの現在の秒・分・時刻
     */
    fun calcNow(aMillisecond: Long): Now {

        val hour = (aMillisecond / myHour % myHour).toInt()
        val restOfHour = (aMillisecond % myHour).toInt()
        val minute = restOfHour / myMinute
        val restOfMinute = restOfHour % myMinute
        val second = restOfMinute / mySecond
        return Now(hour, minute, second)
    }

    /**
     *
     * Created by kensaku on 2017/06/01.
     * 現在時刻をカスタマイズされた時間システムで表現したデータクラス
     * @property hour 現在時刻
     * @property minute 現在の分
     * @property second 現在の秒
     */
    data class Now(val hour: Int, val minute: Int, val second: Int)

    /**
     * Created by kensaku on 2017/06/01.
     * 午前午後システムを表現する列挙クラス。
     * @property division いくつに一日を分割するか
     */
    enum class Ampm(val division: Int) {
        AM(1),
        AMPM(2),
        AMMMPM(3)
    }
}