package com.droid.callertuneskipper

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.droid.callertuneskipper.CallerService.Companion.canScrape
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class CallManager : BroadcastReceiver(), AnkoLogger {

    private var lastState = TelephonyManager.EXTRA_STATE_IDLE

    companion object {
        var isCallActive = false
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.extras?.getString(TelephonyManager.EXTRA_STATE)

        when {
            state.equals(TelephonyManager.EXTRA_STATE_IDLE) -> {
                info { "Call in IDLE state" }
                isCallActive = false
                canScrape = true
                lastState = TelephonyManager.EXTRA_STATE_IDLE
            }
            state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) -> {
                info { "Call initiated" }
                isCallActive = true
                lastState = TelephonyManager.EXTRA_STATE_OFFHOOK
            }
            state.equals(TelephonyManager.EXTRA_STATE_RINGING) -> {
                info { "Incoming call" }
                isCallActive = true
                lastState = TelephonyManager.EXTRA_STATE_RINGING
                if (lastState == TelephonyManager.EXTRA_STATE_RINGING) {
                    info { "Setting canScrape flag to FALSE" }
                    canScrape = false
                }
            }
        }
    }
}