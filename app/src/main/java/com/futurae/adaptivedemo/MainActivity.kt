package com.futurae.adaptivedemo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.futurae.adaptivedemo.databinding.ActivityMainBinding
import com.futurae.sdk.adaptive.AdaptiveDbHelper
import com.futurae.sdk.adaptive.AdaptiveSDK
import com.futurae.sdk.adaptive.CompletionCallback
import com.futurae.sdk.adaptive.UpdateCallback
import com.futurae.sdk.adaptive.exception.AdaptiveException
import com.futurae.sdk.adaptive.model.AdaptiveCollection
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionResultMap ->
        if (permissionResultMap.values.contains(false)) {
            AlertDialog.Builder(this)
                .setTitle("Missing Adaptive Permissions")
                .setMessage("Make sure to grant all adaptive permissions to make the best out of the functionality. Press Settings to grant missing permissions")
                .setPositiveButton("Settings") { _, _ ->
                    startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                        }
                    )
                }
                .show()
        }
    }

    private val completionCallback = object : CompletionCallback {

        override fun onCollectionCompleted(data: AdaptiveCollection) {
            Toast.makeText(this@MainActivity, "Collection complete", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch(Dispatchers.IO) {
                AdaptiveDbHelper.insertCollection(data)
            }
        }
    }
    private val updateCallback = object:  UpdateCallback {
        override fun onCollectionDataUpdated(data: AdaptiveCollection) {
            //no-op
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionLauncher.launch(
            requestAdaptivePermissions()
        )

        binding.statusTextview.text = getString(R.string.status_formatter, getString(R.string.status_idle))
        binding.buttonEnable.text =
            if (AdaptiveSDK.isEnabled()) getString(R.string.button_disable_adaptive)
            else getString(R.string.button_enable_adaptive)
        binding.buttonEnable.setOnClickListener {
            if (!AdaptiveSDK.isEnabled()) {
                AdaptiveSDK.enable(applicationContext as AdaptiveApp, updateCallback, completionCallback)
                binding.buttonEnable.text = getString(R.string.button_disable_adaptive)
            } else {
                AdaptiveSDK.disable()
                binding.buttonEnable.text = getString(R.string.button_enable_adaptive)
            }
        }
        binding.buttonRequestCollection.setOnClickListener {
            try {
                AdaptiveSDK.requestAdaptiveCollection(
                    object : UpdateCallback {
                        override fun onCollectionDataUpdated(data: AdaptiveCollection) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                binding.statusTextview.text =
                                    getString(R.string.status_formatter, getString(R.string.status_collecting))
                            }
                        }

                    },
                    object : CompletionCallback {
                        override fun onCollectionCompleted(data: AdaptiveCollection) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                binding.statusTextview.text =
                                    getString(R.string.status_formatter, getString(R.string.status_idle))
                            }
                        }

                    },
                    true
                )
            } catch (e: AdaptiveException) {
                Timber.e(e)
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

        }
        binding.buttonViewCollections.setOnClickListener {
            startActivity(
                Intent(this, CollectionsActivity::class.java)
            )
        }
        binding.buttonConfigureThreshold.setOnClickListener {
            try {
                var sliderValue = AdaptiveSDK.getAdaptiveCollectionThreshold()
                val dialogView = layoutInflater.inflate(R.layout.dialog_adaptive_threshold, null)
                val textValue = dialogView.findViewById<TextView>(R.id.sliderValue).apply {
                    text = "$sliderValue sec"
                }
                dialogView.findViewById<Slider>(R.id.slider).apply {
                    value = sliderValue.toFloat()
                    addOnChangeListener { _, value, _ ->
                        sliderValue = value.toInt()
                        textValue.text = "${value.toInt()} sec"
                    }
                }
                val dialog = AlertDialog.Builder(this, com.google.android.material.R.style.Theme_Material3_Light_Dialog)
                    .setTitle("Adaptive time threshold").setView(dialogView)
                    .setPositiveButton("OK") { _, _ ->
                        try {
                            AdaptiveSDK.setAdaptiveCollectionThreshold(sliderValue)
                        } catch (e: AdaptiveException) {
                            Timber.e(e)
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }

                    }.create()
                dialog.show()
            } catch (e: AdaptiveException) {
                Timber.e(e)
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestAdaptivePermissions(): Array<String> {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(android.Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(android.Manifest.permission.BLUETOOTH_CONNECT)
        }
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        return permissions.toTypedArray()
    }
}