package org.tannakaken.yourtime

import android.app.Application

/**
 * アプリケーション開始時に[ClockList]を初期化する。
 * Created by kensaku on 2017/06/09.
 */
class MainApplication :Application() {

    /**
     * [ClockList]を初期化
     */
    override fun onCreate() {
        super.onCreate()
        ClockList.init(this)
    }

}