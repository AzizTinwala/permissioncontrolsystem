package com.saifee.permissioncontrolsystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.saifee.permissioncontrolsystem.databinding.ActivitySelectorBinding

class SelectorActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.gotoAppCompactActivity.setOnClickListener {
            startActivity(Intent(this, SampleAppCompact::class.java))
        }
        binding.gotoComposeActivity.setOnClickListener {
            showDialogComingSoon()
        }

    }

    lateinit var builder: MaterialAlertDialogBuilder
    fun showDialogComingSoon() {
        builder = MaterialAlertDialogBuilder(this@SelectorActivity)
            .setTitle("Coming Soon")
            .setMessage("Plug & play permission management library for Android Compose is in progress and will be available soon.")
            .setNeutralButton(
                "Ok"
            ) { dialog, _ -> dialog.dismiss() }

        builder.show()
    }
}
