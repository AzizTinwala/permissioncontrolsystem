package com.saifee.permissionmanagement.permission_helper

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class PermissionControllerFragment : Fragment() {

    lateinit var callback: PermissionCallback
    private lateinit var singleLauncher: ActivityResultLauncher<String>
    private lateinit var multiLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        singleLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            callback.onSinglePermissionResult(granted)
        }

        multiLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            callback.onMultiplePermissionResult(result)
        }
    }

    fun requestMultiple(permissions: Array<String>) {
        multiLauncher.launch(permissions)
    }

    fun requestSingle(permission: String) {
        singleLauncher.launch(permission)
    }
}
