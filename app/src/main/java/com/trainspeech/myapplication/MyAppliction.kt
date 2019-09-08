package com.trainspeech.myapplication

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.utils.Environment
import java.security.SecureRandom
import javax.net.ssl.*
import javax.security.cert.X509Certificate

class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        WavesSdk.init(this, Environment.TEST_NET)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}