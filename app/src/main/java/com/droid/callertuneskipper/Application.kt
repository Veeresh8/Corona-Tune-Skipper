package com.droid.callertuneskipper

import android.app.Application
import com.jaredrummler.android.device.DeviceName

class Application: Application() {

    override fun onCreate() {
        super.onCreate()
        DeviceName.init(this);
    }
}