package com.saifee.permissionmanagement.permission_helper

import android.Manifest
import android.os.Build

/**
 * Centralized provider for permission-related explanation texts.
 *
 * Why sealed class?
 * - Ensures all permission text providers are known at compile time
 * - Allows safe `when` usage if needed in future
 * - Keeps permission messaging strongly typed and consistent
 */
sealed class PermissionTextProvider(
    private val normalText: String,
    private val declinedText: String
) {
    /**
     * Returns appropriate description based on whether
     * the permission was permanently declined (Don't ask again).
     */
    fun getDescription(isPermanentlyDeclined: Boolean): String =
        if (isPermanentlyDeclined) declinedText else normalText

    /* ---------- CAMERA ---------- */
    object Camera : PermissionTextProvider(
        normalText = "This app needs access to your camera to capture photos and videos.",
        declinedText = "It seems you permanently declined camera permission. You can go to the app settings to grant it."
    )

    /* ---------- MICROPHONE ---------- */
    object RecordAudio : PermissionTextProvider(
        normalText = "This app needs access to your microphone so that others can hear you clearly.",
        declinedText = "It seems you permanently declined microphone permission. You can go to the app settings to grant it."
    )

    /* ---------- PHONE ---------- */
    object PhoneCall : PermissionTextProvider(
        normalText = "This app needs phone calling permission so that you can make calls directly from the app.",
        declinedText = "It seems you permanently declined phone calling permission. You can go to the app settings to grant it."
    )

    object ReadPhoneState : PermissionTextProvider(
        normalText = "This app needs access to your phone state to manage calls and connectivity.",
        declinedText = "It seems you permanently declined phone state permission. You can go to the app settings to grant it."
    )

    object ReadPhoneNumbers : PermissionTextProvider(
        normalText = "This app needs access to your phone number for verification and security purposes.",
        declinedText = "It seems you permanently declined phone number permission. You can go to the app settings to grant it."
    )

    /* ---------- LOCATION ---------- */
    object FineLocation : PermissionTextProvider(
        normalText = "This app needs access to your precise location to provide accurate location-based features.",
        declinedText = "It seems you permanently declined location permission. You can go to the app settings to grant it."
    )

    object CoarseLocation : PermissionTextProvider(
        normalText = "This app needs access to your approximate location to function properly.",
        declinedText = "It seems you permanently declined location permission. You can go to the app settings to grant it."
    )

    object BackgroundLocation : PermissionTextProvider(
        normalText = "This app needs access to your location even in the background to ensure uninterrupted service.",
        declinedText = "It seems you permanently declined background location permission. You can go to the app settings to grant it."
    )

    /* ---------- CONTACTS ---------- */
    object ReadContacts : PermissionTextProvider(
        normalText = "This app needs access to your contacts to help you connect with people you know.",
        declinedText = "It seems you permanently declined contacts permission. You can go to the app settings to grant it."
    )

    object WriteContacts : PermissionTextProvider(
        normalText = "This app needs permission to save contacts on your device.",
        declinedText = "It seems you permanently declined contacts permission. You can go to the app settings to grant it."
    )

    /* ---------- CALENDAR ---------- */
    object ReadCalendar : PermissionTextProvider(
        normalText = "This app needs access to your calendar to show and manage upcoming events.",
        declinedText = "It seems you permanently declined calendar permission. You can go to the app settings to grant it."
    )

    object WriteCalendar : PermissionTextProvider(
        normalText = "This app needs permission to add and update events in your calendar.",
        declinedText = "It seems you permanently declined calendar permission. You can go to the app settings to grant it."
    )

    /* ---------- SMS ---------- */
    object SendSms : PermissionTextProvider(
        normalText = "This app needs permission to send SMS messages from your device.",
        declinedText = "It seems you permanently declined SMS permission. You can go to the app settings to grant it."
    )

    object ReadSms : PermissionTextProvider(
        normalText = "This app needs access to read SMS messages for verification purposes.",
        declinedText = "It seems you permanently declined SMS permission. You can go to the app settings to grant it."
    )

    object ReceiveSms : PermissionTextProvider(
        normalText = "This app needs permission to receive SMS messages on your device.",
        declinedText = "It seems you permanently declined SMS permission. You can go to the app settings to grant it."
    )

    /* ---------- STORAGE (Legacy) ---------- */
    object ReadStorage : PermissionTextProvider(
        normalText = "This app needs access to your storage to read files and media.",
        declinedText = "It seems you permanently declined storage permission. You can go to the app settings to grant it."
    )

    object WriteStorage : PermissionTextProvider(
        normalText = "This app needs access to your storage to save files and media.",
        declinedText = "It seems you permanently declined storage permission. You can go to the app settings to grant it."
    )

    /* ---------- SENSORS ---------- */
    object BodySensors : PermissionTextProvider(
        normalText = "This app needs access to body sensors for health and fitness features.",
        declinedText = "It seems you permanently declined sensor permission. You can go to the app settings to grant it."
    )

    /**
     * Allows developers to provide custom permission text dynamically
     * without modifying the sealed class structure.
     */
    data class UserCustomPermissionTextProvider(
        val normal: String,
        val declined: String
    ) : PermissionTextProvider(normal, declined)

    /**
     * Fallback provider when permission is not mapped explicitly.
     * Prevents crashes and ensures user always sees a message.
     */

    class IfNULL(permission: String) : PermissionTextProvider(
        normalText = "This app requires the permission: $permission to work properly.",
        declinedText = "You permanently declined the permission: $permission. Please grant it from app settings."
    )

    companion object {
        /**
         * Central permission → text provider mapping.
         */
        private val providerMap =
            mutableMapOf(
                Manifest.permission.CAMERA to Camera,
                Manifest.permission.RECORD_AUDIO to RecordAudio,
                Manifest.permission.CALL_PHONE to PhoneCall,
                Manifest.permission.READ_PHONE_STATE to ReadPhoneState,
                Manifest.permission.ACCESS_FINE_LOCATION to FineLocation,
                Manifest.permission.ACCESS_COARSE_LOCATION to CoarseLocation,
                Manifest.permission.READ_CONTACTS to ReadContacts,
                Manifest.permission.WRITE_CONTACTS to WriteContacts,
                Manifest.permission.READ_CALENDAR to ReadCalendar,
                Manifest.permission.WRITE_CALENDAR to WriteCalendar,
                Manifest.permission.SEND_SMS to SendSms,
                Manifest.permission.READ_SMS to ReadSms,
                Manifest.permission.RECEIVE_SMS to ReceiveSms,
                Manifest.permission.READ_EXTERNAL_STORAGE to ReadStorage,
                Manifest.permission.WRITE_EXTERNAL_STORAGE to WriteStorage,
                Manifest.permission.BODY_SENSORS to BodySensors
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Manifest.permission.READ_PHONE_NUMBERS to ReadPhoneNumbers
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION to BackgroundLocation
                }
            }

        /**
         * Registers or overrides permission explanation text.
         *
         * Usage:
         * Call this once (preferably in Application.onCreate)
         * if you want feature wise explanation write it in onCreate of that activity or fragment.
         * to customize permission messages without modifying
         * the library code.
         *
         * Behavior:
         * - Overrides default permission text if already registered
         * - Automatically used by permission dialogs
         *
         * Recommended when:
         * - App branding requires custom wording
         * - Feature-specific explanation is needed
         * - Different permissions require different tone/messages
         *
         * Example:
         * ```kotlin
         * PermissionTextProvider.register(
         *     permission = Manifest.permission.CAMERA,
         *     normalText = "We need camera access to scan documents.",
         *     declinedText = "Camera access is required. Please enable it from settings."
         * )
         * ```
         */
        fun register(
            permission: String,
            normalText: String,
            declinedText: String
        ) {
            providerMap[permission] =
                UserCustomPermissionTextProvider(normalText, declinedText)
        }

        /**
         * Returns matching PermissionTextProvider for a permission.
         * Falls back safely if permission is not registered.
         */
        fun from(permission: String): PermissionTextProvider =
            providerMap[permission] ?: IfNULL(permission)
    }
}
