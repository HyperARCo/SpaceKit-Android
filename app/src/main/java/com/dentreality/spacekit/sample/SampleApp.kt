package com.dentreality.spacekit.sample

import android.app.Application
import com.dentreality.spacekit.android.ext.SpaceKit

class SampleApp:Application() {
    override fun onCreate() {
        super.onCreate()
        SpaceKit.init(this){

            //enable press-to-locate
            isLocationOverrideEnabled = true
        }
    }
}