package com.saifee.permissioncontrolsystem

import android.Manifest.permission
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.saifee.permissioncontrolsystem.databinding.ActivitySampleAppCompactBinding
import com.saifee.permissionmanagement.permission_helper.PermissionManager
import com.saifee.permissionmanagement.permission_helper.PermissionTextProvider

class SampleAppCompact : AppCompatActivity() {
    lateinit var binding: ActivitySampleAppCompactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySampleAppCompactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnSingle.setOnClickListener {
            PermissionManager.request(
                activity = this,
                permission = permission.CAMERA
            )
        }
        PermissionTextProvider.register(permission.CAMERA,"Camera Permission Required","Camera Permission declined")
        binding.btnMulti.setOnClickListener {
            PermissionManager.request(
                activity = this,
                permissions = arrayOf(
                    permission.CAMERA,
                    permission.RECORD_AUDIO,
                    permission.CALL_PHONE
                )
            )
        }
    }
}