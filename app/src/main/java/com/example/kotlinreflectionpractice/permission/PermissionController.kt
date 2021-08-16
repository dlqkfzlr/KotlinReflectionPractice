package com.example.kotlinreflectionpractice.permission

import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinreflectionpractice.PermissionResult
import com.example.kotlinreflectionpractice.permission.base.BasePermissionController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PermissionController: BasePermissionController() {

    companion object {
        /*
        *//** Invoke from Activity *//*
        suspend fun requestPermissions(
            activity: AppCompatActivity,
            requestId: Int,
            vararg permissions: String
        ): PermissionResult {
            return withContext(Dispatchers.Main) {
                return@withContext _requestPermissions(
                    activity,
                    requestId,
                    *permissions
                )
            }
        }

        *//** Invoke from Fragment *//*
        suspend fun requestPermissions(
            fragment: androidx.fragment.app.Fragment,
            requestId: Int,
            vararg permissions: String
        ): PermissionResult {
            return withContext(Dispatchers.Main) {
                return@withContext _requestPermissions(
                    fragment,
                    requestId,
                    *permissions
                )
            }
        }*/
    }

    private lateinit var completableDeferred: CompletableDeferred<PermissionResult>

    override fun onPermissionResult(permissionResult: PermissionResult) {
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