package com.busandr.perseverance

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.provider.Settings.Global
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
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

        val channel = NotificationChannel("channel_words", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

//            .build()

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return//{
//        }

        var randomWordService = retrofit.create(RandomWordService::class.java)
        val builder = NotificationCompat.Builder(this, "channel_words")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Your word, messieur")
//            .setContentText("Notification Message")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        GlobalScope.launch {
            var randomWord = randomWordService
                .getGermanWord()
                .body()
                .toString()
                .replace("[\"","")
                .replace("\"]", "")
            Log.i(TAG, randomWord)//.body().toString())
            notificationManager.notify(1, builder.setContentText(randomWord).build())
        }


    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}