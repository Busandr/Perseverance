package com.busandr.perseverance

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.security.Provider.Service

class MainActiivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, VisitService::class.java)
        startService(intent)
        
    }
}