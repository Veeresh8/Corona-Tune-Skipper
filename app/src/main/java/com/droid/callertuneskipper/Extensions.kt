package com.droid.callertuneskipper

import android.view.View

fun View.disableButton() {
    this.isEnabled = false
    this.alpha = 0.5F
}

fun View.enableButton() {
    this.isEnabled = true
    this.alpha = 1F
}