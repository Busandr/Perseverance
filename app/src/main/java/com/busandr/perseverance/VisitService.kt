package com.busandr.perseverance

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import retrofit2.Retrofit


class VisitService : Service() {

    val TAG = "VisitService"
    var wordsSource = "https://www.woerter.net/"
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service has started")
        var retrofit = Retrofit.Builder()
            .baseUrl(wordsSource)
            .build()
        retrofit.newBuilder()


    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}