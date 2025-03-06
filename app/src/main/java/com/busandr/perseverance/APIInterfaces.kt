package com.busandr.perseverance

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*


interface RandomWordService {
    @GET("word")
    suspend fun getWord(): Response<JsonArray>

    @GET("word?lang=de")
    suspend fun getGermanWord(): Response<JsonArray>
}


interface TranslateService {
    @GET("get?langpair=de|ru")
    suspend fun getGermanTranslation(@Query("q") word: String): Response<JsonObject>
}
