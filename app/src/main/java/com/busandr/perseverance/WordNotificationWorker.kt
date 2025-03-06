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
import java.net.SocketTimeoutException


class WordNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val TAG = "WordNotificationWorker"
        var randomWordsLink = "https://random-word-api.herokuapp.com/"
        var translationLink = "https://api.mymemory.translated.net/"
        Log.i(TAG, "Worker has started")
        var retrofitTranslateInstance = Retrofit.Builder()
            .baseUrl(translationLink)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var translateService = retrofitTranslateInstance.create(TranslateService::class.java)

        var retrofitRandomWordsInstance = Retrofit.Builder()
                .baseUrl(randomWordsLink)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val notificationChannel = NotificationChannel("channel_words", "Words Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

            if (ActivityCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return Result.failure()

            var randomWordService = retrofitRandomWordsInstance.create(RandomWordService::class.java)
            val notificationBuilder = NotificationCompat.Builder(applicationContext, "channel_words")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Your word, messier")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            GlobalScope.launch {
                Log.i(TAG, "coroutine has started")
                var randomWord = ""
                var translatedWord = ""
                try {

                    randomWord = randomWordService
                        .getGermanWord()
                        .body()
                        .toString()
                        .replace("[\"", "")
                        .replace("\"]", "")
                    Log.i(TAG, "random $randomWord")
                    translatedWord = translateService
                        .getGermanTranslation(randomWord)
                        .body()
                        ?.get("responseData")
                        ?.asJsonObject
                        ?.get("translatedText")
                        .toString()
                        .replace("[\"", "")
                        .replace("\"]", "")
                    Log.i(TAG, "translate $translatedWord")

                }
                catch (e: SocketTimeoutException) {
                    println("Socket timeout occurred: ${e.message}")
                }
                val itogueString = "$randomWord == $translatedWord"
                notificationManager.notify(1, notificationBuilder.setContentText(itogueString).build())
            }
        return Result.success()
    }
}