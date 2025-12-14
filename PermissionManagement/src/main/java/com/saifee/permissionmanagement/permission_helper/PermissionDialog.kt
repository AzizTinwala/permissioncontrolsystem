package com.saifee.permissionmanagement.permission_helper

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Simple wrapper around MaterialAlertDialogBuilder.
 *
 * Purpose:
 * - Keeps permission UI logic separate from PermissionManager
 * - Allows easy future customization (theme, button text, analytics)
 */
class PermissionDialog(
    val context: Context,
    private val provider: PermissionTextProvider,
    private val isPermanentlyDeclined: Boolean,
    private val onDismissClick: () -> Unit,
    private val onOkClick: () -> Unit,
    private val onGoToAppSettingsClick: () -> Unit
) {
    /**
     * Builds and displays the permission explanation dialog.
     * Button behavior:
     * - Cancel → dismiss dialog
     * - OK → retry permission request
     * - Grant Permission → redirect to app settings
     */

    fun show() {
        val builder = MaterialAlertDialogBuilder(context)
            .setTitle("Permission required")
            .setMessage(provider.getDescription(isPermanentlyDeclined))
            .setNegativeButton("Cancel") { _, _ ->
                onDismissClick()
            }

        if (isPermanentlyDeclined) {
            builder.setPositiveButton("Grant Permission") { _, _ ->
                onGoToAppSettingsClick()
            }
        } else {
            builder.setPositiveButton("OK") { _, _ ->
                onOkClick()
            }
        }

        builder.show()
    }
}


