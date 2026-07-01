package com.sam.youwontgiveup.ui.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.sam.youwontgiveup.R

class OverlayManager(
    context: Context
) {

    private val windowManager =
        context.getSystemService(
            Context.WINDOW_SERVICE
        ) as WindowManager

    private var overlayView: View? = null

    fun show(
        context: Context,
        domain: String
    ) {

        Handler(Looper.getMainLooper()).post {

            if (overlayView != null)
                return@post

            overlayView =
                LayoutInflater.from(context)
                    .inflate(
                        R.layout.activity_overlay,
                        null
                    )

            overlayView!!
                .findViewById<TextView>(
                    R.id.txtDomain
                ).text = domain

            val params =
                WindowManager.LayoutParams(

                    WindowManager.LayoutParams.MATCH_PARENT,

                    WindowManager.LayoutParams.MATCH_PARENT,

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    else
                        WindowManager.LayoutParams.TYPE_PHONE,

                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,

                    PixelFormat.TRANSLUCENT
                )

            params.gravity = Gravity.CENTER

            windowManager.addView(
                overlayView,
                params
            )

            Handler(Looper.getMainLooper())
                .postDelayed({

                    remove()

                }, 30000)
        }
    }

    fun remove() {

        overlayView?.let {

            windowManager.removeView(it)

            overlayView = null
        }
    }
}