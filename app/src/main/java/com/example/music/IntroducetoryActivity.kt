package com.example.music

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.music.databinding.ActivityIntroducetoryBinding

class IntroducetoryActivity : AppCompatActivity() {
    lateinit var binding:ActivityIntroducetoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroducetoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.logo.animate().translationY(3000f).setDuration(1200).setStartDelay(2000)
        binding.introname.animate().translationY(3000f).setDuration(1200).setStartDelay(2000)
        binding.splashCD.animate().translationY(3000f).setDuration(1200).setStartDelay(2000)
        binding.splashNote.animate().translationY(3000f).setDuration(1200).setStartDelay(2000)
        binding.img.animate().translationY(-3000f).setDuration(1200).setStartDelay(2000)

        val delayHandler = Handler()
        delayHandler.postDelayed({
            // Start MainActivity after a delay
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3000)

    }
}