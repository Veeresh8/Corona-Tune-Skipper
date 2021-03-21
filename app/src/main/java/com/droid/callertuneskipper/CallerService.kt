package com.droid.callertuneskipper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.droid.callertuneskipper.readers.base.ActionCallback
import com.droid.callertuneskipper.readers.base.GooglePixel
import com.droid.callertuneskipper.readers.base.Reader
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.error
import org.jetbrains.anko.info


class CallerService : AccessibilityService(), AnkoLogger {

    private val readers = HashMap<String, Reader>()
    private val supportedPackages = listOf(GooglePixel.packageName)

    companion object {
        var canScrape = true
    }

    override fun onServiceConnected() {
        info("onServiceConnected")

        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.packageNames = supportedPackages.toTypedArray()
        info.feedbackType = AccessibilityServiceInfo.DEFAULT
        info.notificationTimeout = 100

        this.serviceInfo = info
    }

    override fun onCreate() {
        info { "onCreated called" }
        super.onCreate()
        initReaders()
    }

    private fun initReaders() {
        readers[GooglePixel.packageName] = GooglePixel()
    }

    override fun onInterrupt() {
        error { "onInterrupt called" }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        debug("Unbind called")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        debug("onDestroy called")
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        try {

            if (event == null) {
                info("AccessibilityEvent was NULL")
                return
            }

            if (event.source == null) {
                info("Event source was NULL")
                return
            }

            if (event.source.packageName == null) {
                info("PackageName was NULL")
                return
            }

            if (!Utils.canScrapeData(event.source.packageName.toString(), readers.keys)) {
                info { event.source.packageName.toString() + " not eligible for scraping" }
                return
            }

            val reader = readers[event.source.packageName.toString()]
            if (reader != null) {
                reader.payload(event.source, object : ActionCallback {
                    @SuppressLint("MissingPermission")
                    override fun performBackPress() {
                        val wasSuccessful = performGlobalAction(GLOBAL_ACTION_BACK)
                        Thread.sleep(1000)
                        if (Utils.hasPhoneStatePermission(this@CallerService)) {
                            if (wasSuccessful && CallManager.isCallActive) {
                                canScrape = false
                            } else {
                                info { "CallActive: ${CallManager.isCallActive} || Back: $wasSuccessful" }
                            }
                        } else {
                            info { "Phone state permission not granted!" }
                        }

                    }
                })
            } else {
                info { "Reader was NULL for " + event.source.packageName.toString() }
            }

        } catch (exception: Exception) {
            error { "Exception caught: $exception" }
        }
    }
}