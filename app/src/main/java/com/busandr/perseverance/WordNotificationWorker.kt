package com.busandr.perseverance

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WordNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val TAG = "WordNotificationWorker"
        var wordsSource = "https://random-word-api.herokuapp.com/"
        var translateService = "https://api.mymemory.translated.net/"
        Log.i(TAG, "Worker has started")
        var retrofitTranslateService = Retrofit.Builder()
            .baseUrl(translateService)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var api = retrofitTranslateService.create(TranslateService::class.java)

        var retrofit = Retrofit.Builder()
                .baseUrl(wordsSource)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofit.newBuilder()

            val channel = NotificationChannel("channel_words", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            if (ActivityCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return Result.failure()//{

            var randomWordService = retrofit.create(RandomWordService::class.java)
            val builder = NotificationCompat.Builder(applicationContext, "channel_words")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Your word, messier")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            GlobalScope.launch {
                Log.i(TAG, "coroutine has started")
                var randomWord = randomWordService
                    .getGermanWord()
                    .body()
                    .toString()
                    .replace("[\"","")
                    .replace("\"]", "")
                Log.i(TAG, "random $randomWord")
                var translatedWord = api
                    .getGermanTranslation(randomWord)
                    .body()
                    ?.get("responseData")
                    ?.asJsonObject
                    ?.get("translatedText")
                Log.i(TAG, "translate $translatedWord")

                val itogueString = "$randomWord == $translatedWord"
                notificationManager.notify(1, builder.setContentText(itogueString).build())
            }
        return Result.success()
    }
}