package com.droid.callertuneskipper.readers.base

import android.view.accessibility.AccessibilityNodeInfo
import com.droid.callertuneskipper.CallerService
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class GooglePixel: Reader, AnkoLogger {

    private val KEYPAD = "com.google.android.dialer:id/incall_second_button"

    companion object {
        val packageName = "com.google.android.dialer"
    }

    private val keyCodeMap = HashMap<String, String>()

    init {
        keyCodeMap["1"] = "com.google.android.dialer:id/one"
        keyCodeMap["2"] = "com.google.android.dialer:id/two"
        keyCodeMap["3"] = "com.google.android.dialer:id/three"
        keyCodeMap["4"] = "com.google.android.dialer:id/four"
        keyCodeMap["5"] = "com.google.android.dialer:id/five"
        keyCodeMap["6"] = "com.google.android.dialer:id/six"
        keyCodeMap["7"] = "com.google.android.dialer:id/seven"
        keyCodeMap["8"] = "com.google.android.dialer:id/eight"
        keyCodeMap["9"] = "com.google.android.dialer:id/nine"
        keyCodeMap["*"] = "com.google.android.dialer:id/star"
        keyCodeMap["#"] = "com.google.android.dialer:id/pound"
        keyCodeMap["0"] = "com.google.android.dialer:id/zero"
    }

    override fun payload(nodeInfo: AccessibilityNodeInfo, callback: ActionCallback) {
        if (!canScrapeCall()) {
            info { "Cannot scrape call yet!" }
            return
        }

        val keypadNode = nodeInfo.findAccessibilityNodeInfosByViewId(KEYPAD)
        keypadNode.forEach { node ->
            Thread.sleep(1500)
            node?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }

        val hashNode = nodeInfo.findAccessibilityNodeInfosByViewId(keyCodeMap["#"])
        hashNode.forEach { node ->
            Thread.sleep(1500)
            node?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Thread.sleep(1500)
            callback.performBackPress()
        }
    }

    override fun canScrapeCall(): Boolean {
        return CallerService.canScrape
    }
}