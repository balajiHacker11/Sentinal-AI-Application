package com.example

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MainContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SafetyViewModel

class MainActivity : ComponentActivity() {

    private val safetyViewModel: SafetyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleSosIntent(intent)

        setContent {
            MyApplicationTheme {
                MainContainer(viewModel = safetyViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSosIntent(intent)
    }

    private fun handleSosIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("EXTRA_AUTO_TRIGGER_POWER_SOS", false) == true) {
            val reason = intent.getStringExtra("EXTRA_TRIGGER_REASON") ?: "POWER_TRIGGER"
            safetyViewModel.onPowerButtonDangerDetected(reason)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            safetyViewModel.powerButtonDetector.handlePowerButtonPress()
        }
        return super.onKeyDown(keyCode, event)
    }
}

