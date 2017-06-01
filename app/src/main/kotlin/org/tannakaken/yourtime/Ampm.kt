package org.tannakaken.yourtime

/**
 * Created by kensaku on 2017/06/01.
 * 午前午後システムを表現する列挙クラス。
 */
enum class Ampm(val rowValue: Int) {
    AM(1),
    AMPM(2),
    AMMMPM(3)
}