package com.busandr.perseverance

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.Settings.Global
import android.util.Log
import com.google.gson.JsonArray
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface RandomWordService {
    @GET("word")
    suspend fun getWord(): Response<JsonArray>

    @GET("word?lang=de")
    suspend fun getGermanWord(): Response<JsonArray>



//        @GET("users/{user}/repos")
//        suspend fun getUserRepositories(@Path("user") username: String): Response<List<Repository>>
}

class VisitService : Service() {

    val TAG = "VisitService"
    var wordsSource = "https://random-word-api.herokuapp.com/"
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service has started")
        var retrofit = Retrofit.Builder()
            .baseUrl(wordsSource)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
//        retrofit.newBuilder()

        var randomWordService = retrofit.create(RandomWordService::class.java)
        GlobalScope.launch {
            var randomWord = randomWordService.getGermanWord().body().toString()
            Log.i(TAG, randomWord)//.body().toString())
        }


    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}