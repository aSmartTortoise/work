package com.voyah.window.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.voyah.voice.card.view.TemperatureView
import com.voyah.window.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        navigateWindowStudyPage("嘿 悬浮窗。")
        finish()

//        findViewById<TemperatureView>(R.id.temp_view).apply {
//            setTemp(38, 12, 38, 13)
//        }
    }
}