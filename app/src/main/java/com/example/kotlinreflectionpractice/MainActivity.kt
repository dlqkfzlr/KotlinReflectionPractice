package com.example.kotlinreflectionpractice

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kotlinreflectionpractice.PermissionResult.*
import com.example.kotlinreflectionpractice.databinding.ActivityMainBinding
import com.example.kotlinreflectionpractice.permission.PermissionController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {
            private const val REQUEST_ID = 0x0001
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
           Log.d("PERMISSION", "start requestPermissions")
            val permissionResult = PermissionController.requestPermissions(
                this@MainActivity,
                REQUEST_ID,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA,
            )
            Log.d("PERMISSION_RESULT", "$permissionResult")
            when(permissionResult) {
                is PermissionGranted -> {

                }
                is PermissionDenied -> {

                }
                is PermissionDeniedPermanently -> {
                    // 실제론 Dialog 띄운 후, 설정화면으로 이동시켜줘야함
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "권한없어서 종료", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                is ShowRationale -> {

                }
            }
        }
    }

}