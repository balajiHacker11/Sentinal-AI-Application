package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.telephony.SmsManager
import android.util.Log
import com.example.data.db.GuardianEntity

class SosManager(private val context: Context) {

    fun triggerDirectCall(phoneNumber: String = "1091") {
        try {
            vibrateAlert()
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to dialer if direct CALL permission isn't granted at runtime
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(dialIntent)
            } catch (ex: Exception) {
                Log.e("SosManager", "Failed to open phone dialer: ${ex.message}")
            }
        }
    }

    fun sendEmergencySmsToGuardians(
        guardians: List<GuardianEntity>,
        locationText: String = "Live Emergency Location",
        customMessage: String? = null
    ): String {
        return sendOfflineEvidenceSmsToGuardians(guardians, null, null)
    }

    fun sendOfflineEvidenceSmsToGuardians(
        guardians: List<GuardianEntity>,
        latestPhotoName: String? = null,
        latestAudioName: String? = null
    ): String {
        if (guardians.isEmpty()) {
            return "No guardians registered. Please add emergency contacts first!"
        }

        vibrateAlert()
        val photoPart = if (!latestPhotoName.isNullOrEmpty()) "\n📷 Incident Photo: $latestPhotoName" else ""
        val audioPart = if (!latestAudioName.isNullOrEmpty()) "\n🎙️ Audio Evidence: $latestAudioName" else ""
        val fullMsg = "🚨 EMERGENCY ALERT!\nI am in danger! Urgent help needed immediately.$photoPart$audioPart\n- Sent via Sentinel AI Guard"

        var successCount = 0
        val smsManager: SmsManager = try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
        } catch (e: Exception) {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        for (guardian in guardians) {
            val phone = guardian.phone.trim()
            if (phone.isNotEmpty()) {
                try {
                    val parts = smsManager.divideMessage(fullMsg)
                    smsManager.sendMultipartTextMessage(phone, null, parts, null, null)
                    successCount++
                } catch (e: Exception) {
                    Log.e("SosManager", "SMS to $phone failed: ${e.message}")
                }
            }
        }

        try {
            val primaryPhone = guardians.firstOrNull { it.isPrimary }?.phone ?: guardians.first().phone
            val uri = android.net.Uri.parse("smsto:${primaryPhone.trim()}")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", fullMsg)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (ex: Exception) {
            Log.e("SosManager", "Failed to launch system SMS intent: ${ex.message}")
        }

        return if (successCount > 0) {
            "Offline SMS evidence alert sent to $successCount guardian(s)!"
        } else {
            "Offline SMS ready with Photo/Audio evidence & location."
        }
    }

    fun openWhatsAppAlert(phoneNumber: String, message: String) {
        try {
            val cleanPhone = phoneNumber.replace("+", "").replace(" ", "")
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SosManager", "WhatsApp failed: ${e.message}")
        }
    }

    fun vibrateAlert() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        } catch (e: Exception) {
            Log.e("SosManager", "Vibration failed: ${e.message}")
        }
    }
}
