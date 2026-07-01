package com.sam.youwontgiveup.ui.overlay

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.sam.youwontgiveup.databinding.ActivityOverlayBinding

class OverlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val domain =
            intent.getStringExtra("DOMAIN") ?: ""

        binding.txtDomain.text = domain

        Handler(Looper.getMainLooper()).postDelayed({

            finish()

        }, 30000)
    }

    override fun onBackPressed() {
        // Disabled
    }
}