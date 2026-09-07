package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val prefs = context.getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
            val isGuardEnabled = prefs.getBoolean("power_button_guard_enabled", true)
            if (isGuardEnabled) {
                Log.d("BootCompletedReceiver", "Boot completed: restarting PowerButtonSosService")
                PowerButtonSosService.start(context)
            }
        }
    }
}
