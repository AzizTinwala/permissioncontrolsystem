package com.saifee.permissionmanagement.permission_helper

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.saifee.permissionmanagement.extension.openAppSettings

/**
 * Central permission orchestration layer.
 *
 * Responsibilities:
 * - Requests runtime permissions (single / multiple)
 * - Tracks denied permissions
 * - Shows explanation dialogs sequentially
 * - Delegates actual permission requests to a headless fragment
 *
 * Why an object?
 * - Single source of truth
 * - Avoids leaking activity references across multiple managers
 */
object PermissionManager {
    /**
     * Tag used for attaching the headless PermissionControllerFragment.
     * Ensures fragment is reused across configuration changes.
     */
    private const val FRG_TAG = "PermissionController"

    /**
     * Callback implementation invoked by PermissionControllerFragment.
     * Receives permission results and queues denied permissions for explanation.
     */
    private val permissionCallback = object : PermissionCallback {
        /**
         * Called when multiple permissions are requested at once.
         * Any denied permission is enqueued for dialog explanation.
         */
        override fun onMultiplePermissionResult(result: Map<String, Boolean>) {
            result.forEach { (perm, granted) ->
                if (!granted) enqueue(perm)
            }
            if (permissionQueue.isEmpty()) return
            showNextDialog()
        }

        /**
         * Called when a single permission is requested.
         * Used mainly for retry flows from the dialog.
         */
        override fun onSinglePermissionResult(isGranted: Boolean) {
            if (permissionQueue.isEmpty()) return
            val perm = permissionQueue.first()
            if (!isGranted) enqueue(perm)
            showNextDialog()
        }

    }

    /**
     * Activity reference required for:
     * - Permission checks
     * - Showing dialogs
     * - Navigating to app settings
     *
     * NOTE: Must always be updated via ensureController()
     */
    private lateinit var activity: AppCompatActivity

    /**
     * Headless fragment that actually calls requestPermissions().
     * Keeps permission flow lifecycle-safe.
     */
    private lateinit var controller: PermissionControllerFragment

    /** currently visible permission dialog.*/
    private lateinit var dialog: PermissionDialog

    /**
     * Queue of permissions that were denied and need explanation.
     * ArrayDeque ensures FIFO behavior.
     */
    private var permissionQueue = ArrayDeque<String>()

    /**
     * Attaches PermissionControllerFragment only once.
     *
     * Why headless fragment?
     * - Survives configuration changes
     * - Avoids leaking Activity
     * - Keeps permission callbacks reliable
     */
    private fun ensureController(activity: AppCompatActivity) {
        this.activity = activity

        val fm: FragmentManager = activity.supportFragmentManager
        val existing = fm.findFragmentByTag(FRG_TAG)

        if (existing != null) {
            controller = existing as PermissionControllerFragment
            controller.callback = permissionCallback
        } else {
            controller = PermissionControllerFragment()
            controller.callback = permissionCallback
            fm.beginTransaction()
                .add(controller, FRG_TAG)
                .commitNow()
        }
    }

    /**
     * Requests multiple runtime permissions in a single call.
     *
     * Usage:
     * Call this when your feature depends on more than one permission (e.g. Camera + Microphone).
     *
     * Behavior:
     * - Filters out already granted permissions automatically
     * - Requests only ungranted permissions from the system
     * - If any permission is denied, an explanation dialog will be shown
     *   sequentially for each denied permission
     *
     * Notes:
     * - No dialog is shown if all permissions are already granted
     *
     * Example:
     * ```kotlin
     * PermissionManager.request(
     *     activity,
     *     arrayOf(
     *         Manifest.permission.CAMERA,
     *         Manifest.permission.RECORD_AUDIO
     *     )
     * )
     * ```
     * @warning
     * -This API uses a headless fragment internally.
     * -Do not call it after 'onSaveInstanceState()'.
     */
    fun request(activity: AppCompatActivity, permissions: Array<String>) {
        ensureController(activity)

        val ungranted = permissions.filter {
            activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isEmpty()) return

        permissionQueue.clear()
        permissionQueue.addAll(ungranted)

        controller.requestMultiple(ungranted.toTypedArray())
    }

    /**
     * Requests a single runtime permission.
     *
     * Usage:
     * Call this when a specific action requires one permission (e.g. Camera click, Location access).
     *
     * Behavior:
     * - If permission is already granted, the function exits silently
     * - If denied, an explanation dialog is shown
     * - If the user permanently declined the permission, they are
     *   redirected to App Settings
     *
     * Typical use cases:
     * - Retry permission after user clicks "OK" in dialog
     * - On-demand permission requests for a specific feature
     **
     * Example:
     * ```kotlin
     * PermissionManager.request(
     *     activity,
     *     Manifest.permission.ACCESS_FINE_LOCATION
     * )
     * ```
     * @warning
     * This API uses a headless fragment internally.
     * Do not call it after `onSaveInstanceState()`.
     *
     */
    fun request(activity: AppCompatActivity, permission: String) {
        ensureController(activity)

        if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return

        permissionQueue.clear()
        permissionQueue.add(permission)

        controller.requestSingle(permission)
    }

    /**
     * Adds permission back into queue only if not already present.
     * Prevents duplicate dialogs.
     */
    private fun enqueue(permission: String) {
        if (!permissionQueue.contains(permission)) {
            permissionQueue.add(permission)
        }
    }


    /**
     * Safely removes next permission from the queue.
     * Defensive check prevents crashes.
     */
    private fun popNext(): String? {
        if (permissionQueue.isEmpty()) return null
        return permissionQueue.removeFirst()
    }


    /**
     * Shows explanation dialog for the next denied permission.
     *
     * Dialog behavior:
     * - If permanently declined → redirect to App Settings
     * - Else → retry permission request
     */
    private fun showNextDialog() {
        if (permissionQueue.isEmpty()) return
        val permission = permissionQueue.firstOrNull() ?: return
        permissionQueue.removeFirst()

        val provider = PermissionTextProvider.from(permission)

        val permanentlyDeclined =
            !activity.shouldShowRequestPermissionRationale(permission)

        dialog = PermissionDialog(
            context = activity,
            provider = provider,
            isPermanentlyDeclined = permanentlyDeclined,
            onDismissClick = { popNext() },
            onOkClick = {
                popNext()
                controller.requestSingle(permission) // retry
            },
            onGoToAppSettingsClick = {
                popNext()
                activity.openAppSettings()
            }
        )

        dialog.show()
    }
}


