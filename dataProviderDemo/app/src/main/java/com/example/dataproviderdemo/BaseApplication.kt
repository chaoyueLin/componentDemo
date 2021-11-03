package com.example.dataproviderdemo

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ARouter.openDebug()
        ARouter.openLog()
        ARouter.init(this)
    }
}