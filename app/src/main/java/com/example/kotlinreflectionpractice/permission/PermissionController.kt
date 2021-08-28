package com.example.kotlinreflectionpractice.permission

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kotlinreflectionpractice.PermissionResult
import com.example.kotlinreflectionpractice.permission.base.BasePermissionController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PermissionController: BasePermissionController() {

    companion object {

        private const val TAG = "PermissionManager"

        suspend fun requestPermissions(
            activity: AppCompatActivity,
            requestId: Int,
            vararg permissions: String
        ): PermissionResult {
            return withContext(Dispatchers.Main) {
                return@withContext _requestPermissions(
                    activity,
                    requestId,
                    *permissions    // spread operator
                )
            }
        }

        private suspend fun _requestPermissions(
            activityOrFragment: Any,
            requestId: Int,
            vararg permissions: String
        ): PermissionResult {
            val fragmentManager = if (activityOrFragment is AppCompatActivity) {
                activityOrFragment.supportFragmentManager
            } else {
                (activityOrFragment as Fragment).childFragmentManager
            }
            return if (fragmentManager.findFragmentByTag(TAG) != null) {
                val permissionController = fragmentManager.findFragmentByTag(TAG) as PermissionController
                permissionController.completableDeferred = CompletableDeferred()
                permissionController.requestPermissions(
                    requestId,
                    *permissions
                )
                permissionController.completableDeferred.await()
            } else {
                val permissionController = PermissionController().apply {
                    completableDeferred = CompletableDeferred()
                }
                fragmentManager.beginTransaction().add(
                    permissionController,
                    TAG
                ).commitNow()
                permissionController.requestPermissions(requestId, *permissions)
                permissionController.completableDeferred.await()
            }
        }

    }

    /*
    * All functions on this interface [and all interfaces derived from it] are thread-safe
    * and can be safely invoked from concurrent coroutines without external synchronization.” */
    private lateinit var completableDeferred: CompletableDeferred<PermissionResult>

    override fun onPermissionResult(permissionResult: PermissionResult) {
        Log.d("PermissionController", "onPermissionResult(permissionResult=${permissionResult})")
        if (::completableDeferred.isInitialized) {
            completableDeferred.complete(permissionResult)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::completableDeferred.isInitialized && completableDeferred.isActive) {
            completableDeferred.cancel()
        }
    }
}