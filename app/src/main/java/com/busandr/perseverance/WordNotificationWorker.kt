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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.DelicateCoroutinesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WordNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @OptIn(DelicateCoroutinesApi::class)
    override fun doWork(): Result {

        val TAG = "WordNotificationWorker"
        var wordsSource = "https://random-word-api.herokuapp.com/"
        var translateService = "https://api.mymemory.translated.net/"
        Log.i(TAG, "Worker has started")
        var retrofitTranslateService = Retrofit.Builder()
            .baseUrl(translateService)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var translateApi = retrofitTranslateService.create(TranslateService::class.java)

        var retrofit = Retrofit.Builder()
                .baseUrl(wordsSource)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

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

        fun getWord(): String{
    val randomWordCall = randomWordService.getGermanWord()
    var randomWord = """Permanenz"""
    randomWordCall.enqueue(object : Callback<List<JsonPrimitive>> {
        override fun onResponse(
            call: Call<List<JsonPrimitive>>,
            response: retrofit2.Response<List<JsonPrimitive>>
        ) {
            if (response.isSuccessful) {
                randomWord = response
                    .body()
                    .toString()
                    .replace("[\"","")
                    .replace("\"]", "")
                randomWord.let { println(it) }
            } else {
                println("Failed to retrieve")
            }
        }

        override fun onFailure(call: Call<List<JsonPrimitive>>, t: Throwable) {
            t.printStackTrace()
        }
    })
    return randomWord
        }

        fun getTranslate(word: String): String{
            var translateCall = translateApi.getGermanTranslation(word)
            var translatedWord = word
            translateCall.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: retrofit2.Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        translatedWord = response
                            .body()//.toString()

                            ?.get("responseData")
                            ?.asJsonObject
                            ?.get("translatedText")
                            .toString()
                        translatedWord.let { println(it) }
                    } else {
                        println("Failed to retrieve")
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    t.printStackTrace()
                }
            })
            return translatedWord
        }

        val randomWord = getWord()
        Log.i(TAG, "random $randomWord")

        val translatedWord = getTranslate(randomWord)
        Log.i(TAG, "translate $translatedWord")

        val itogueString = "$randomWord == $translatedWord"
                notificationManager.notify(1, builder.setContentText(itogueString).build())
        return Result.success()
    }
}