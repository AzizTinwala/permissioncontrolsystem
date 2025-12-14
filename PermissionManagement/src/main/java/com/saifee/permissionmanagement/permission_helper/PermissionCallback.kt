package com.saifee.permissionmanagement.permission_helper

interface PermissionCallback {
    fun onMultiplePermissionResult(result: Map<String, Boolean>)
    fun onSinglePermissionResult(isGranted: Boolean)
}
