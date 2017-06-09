package org.tannakaken.yourtime

import android.app.Application

/**
 * Created by kensaku on 2017/06/09.
 */
class MainApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        ClockList.init(this)
    }

}