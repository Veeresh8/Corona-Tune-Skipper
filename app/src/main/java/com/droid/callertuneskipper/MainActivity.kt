package com.droid.callertuneskipper

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.droid.callertuneskipper.databinding.ActivityMainBinding
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.jaredrummler.android.device.DeviceName
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnableService.setOnClickListener {
            Utils.launchAccessibilityScreen(this)
        }

        printDeviceInfo()
    }

    override fun onResume() {
        super.onResume()
        checkPhoneStatePermission()
    }

    private fun printDeviceInfo() {
        DeviceName.with(this).request { info, error ->
            if (error != null) {
                error { "Error getting device info: $error" }
                return@request
            }

            info {
                """
                Manufacturer - ${info.manufacturer}
                Name - ${info.marketName}    
            """.trimIndent()
            }
        }
    }

    private fun checkPhoneStatePermission() {
        if (Utils.hasPhoneStatePermission(this)) {
            info { "Phone state permission granted!" }
            checkAccessibilitySettings()
            return
        }

        askPermission(Manifest.permission.READ_PHONE_STATE) {
            binding.btnEnableService.enableButton()
            checkAccessibilitySettings()
        }.onDeclined {
            when {
                it.hasDenied() -> {
                    toast("Reading phone state is necessary for the app to function")
                    binding.btnEnableService.disableButton()
                }
                it.hasForeverDenied() -> {
                    binding.btnEnableService.disableButton()
                }
            }
        }
    }

    private fun checkAccessibilitySettings() {
        if (Utils.isAccessibilityServiceEnabled(this, CallerService::class.java)) {
            info { "Acc service enabled" }
            binding.btnEnableService.disableButton()
        } else {
            info { "Acc service not enabled" }
            binding.btnEnableService.enableButton()
        }
    }
}