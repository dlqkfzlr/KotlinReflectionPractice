package com.example.kotlinreflectionpractice

sealed class PermissionResult(val requestCode: Int) {
    class PermissionGranted(requestCode: Int): PermissionResult(requestCode)
    class PermissionDenied(
        requestCode: Int,
        val deniedPermissions: List<String>): PermissionResult(requestCode)
    class ShowRationale(requestCode: Int): PermissionResult(requestCode)
    class PermissionDeniedPermanently(
        requestCode: Int,
        val deniedPermissions: List<String>): PermissionResult(requestCode)
}
