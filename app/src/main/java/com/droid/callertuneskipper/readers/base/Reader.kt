package com.droid.callertuneskipper.readers.base

import android.view.accessibility.AccessibilityNodeInfo

interface Reader {
    fun payload(nodeInfo: AccessibilityNodeInfo, callback: ActionCallback)
    fun canScrapeCall(): Boolean
}

interface ActionCallback {
    fun performBackPress()
}
