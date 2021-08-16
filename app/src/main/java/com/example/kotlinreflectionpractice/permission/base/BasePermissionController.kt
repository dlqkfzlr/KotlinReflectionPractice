package com.example.kotlinreflectionpractice.permission.base

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.kotlinreflectionpractice.PermissionResult

abstract class BasePermissionController: Fragment() {

    private val rationaleRequest = mutableMapOf<Int, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true   // Instance를 계속 유지되어서 한번 onCreate가 불리면 다시 불리지않고, onDestroy도 마찬가지로 불리지 않음
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            onPermissionResult(PermissionResult.PermissionGranted(requestCode))
        } else if (permissions.any { shouldShowRequestPermissionRationale(it) }) {
            onPermissionResult(
                PermissionResult.PermissionDenied(requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                )
            )
        } else {
            onPermissionResult(
                PermissionResult.PermissionDeniedPermanently(requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                ))
        }
    }

    protected fun requestPermissions(requestId: Int, vararg permissions: String) {
        rationaleRequest[requestId]?.let {
            requestPermissions(permissions, requestId)
            rationaleRequest.remove(requestId)
            return
        }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        when {
            notGranted.isEmpty() -> {
                onPermissionResult(PermissionResult.PermissionGranted(requestId))
            }
            notGranted.any { shouldShowRequestPermissionRationale(it) } -> {
                rationaleRequest[requestId] = true
                onPermissionResult(PermissionResult.ShowRationale(requestId))
            }
            else -> {
                requestPermissions(notGranted, requestId)
            }
        }
    }

    protected abstract fun onPermissionResult(permissionResult: PermissionResult)

}