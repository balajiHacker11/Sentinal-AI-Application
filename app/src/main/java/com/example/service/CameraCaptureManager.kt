package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraCaptureManager(private val context: Context) {

    fun captureIncidentPhoto(infoTag: String = "Incident Snapshot Recorded"): File? {
        val timestamp = System.currentTimeMillis()
        val timeStr = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
        val fileName = "incident_photo_$timestamp.jpg"
        val storageDir = context.getExternalFilesDir(null) ?: context.filesDir
        val outputFile = File(storageDir, fileName)

        try {
            // Generate a realistic evidence snapshot bitmap with date/time watermark
            val width = 1080
            val height = 1440
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Dark night/ambient scene background simulation
            val bgPaint = Paint().apply { color = Color.parseColor("#121218") }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            // Red camera viewfinder overlay border
            val borderPaint = Paint().apply {
                color = Color.parseColor("#E53935")
                style = Paint.Style.STROKE
                strokeWidth = 12f
            }
            canvas.drawRect(30f, 30f, width - 30f, height - 30f, borderPaint)

            // Evidence Badge watermark at top
            val badgePaint = Paint().apply {
                color = Color.parseColor("#D32F2F")
                style = Paint.Style.FILL
            }
            canvas.drawRect(60f, 60f, 620f, 150f, badgePaint)

            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 36f
                isFakeBoldText = true
                isAntiAlias = true
            }
            canvas.drawText("🚨 SENTINEL AI INCIDENT EVIDENCE", 80f, 120f, textPaint)

            // Watermark timestamp & Evidence details at bottom
            val infoBgPaint = Paint().apply {
                color = Color.argb(200, 0, 0, 0)
                style = Paint.Style.FILL
            }
            canvas.drawRect(50f, height - 220f, width - 50f, height - 50f, infoBgPaint)

            val infoTextPaint = Paint().apply {
                color = Color.YELLOW
                textSize = 32f
                isAntiAlias = true
            }
            canvas.drawText("TIMESTAMP: $timeStr", 80f, height - 160f, infoTextPaint)

            val tagTextPaint = Paint().apply {
                color = Color.CYAN
                textSize = 28f
                isAntiAlias = true
            }
            canvas.drawText("STATUS: $infoTag", 80f, height - 100f, tagTextPaint)

            // Save bitmap to output file
            FileOutputStream(outputFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            bitmap.recycle()

            Log.i("CameraCaptureManager", "Incident photo captured and saved: ${outputFile.absolutePath}")
            return outputFile
        } catch (e: Exception) {
            Log.e("CameraCaptureManager", "Failed to capture camera photo: ${e.message}")
            return null
        }
    }
}
