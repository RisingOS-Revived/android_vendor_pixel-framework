package com.google.android.systemui.columbus.actions

import android.content.Context
import android.util.Log

import com.android.systemui.statusbar.notification.collection.NotificationEntry
import com.android.systemui.statusbar.notification.headsup.HeadsUpManager
import com.android.systemui.statusbar.notification.headsup.OnHeadsUpChangedListener

import com.google.android.systemui.columbus.gates.Gate
import com.google.android.systemui.columbus.gates.SilenceAlertsDisabled
import com.google.android.systemui.columbus.sensors.GestureSensor

import java.util.Optional

class UnpinNotifications(
    context: Context,
    private val silenceAlertsDisabled: SilenceAlertsDisabled,
    optionalHeadsUpManager: Optional<HeadsUpManager>
) : Action(context, null, 2, null) {

    companion object {
        val TAG = "Columbus/UnpinNotif"
    }

    private val gateListener: Gate.Listener
    private var hasPinnedHeadsUp = false
    private val headsUpChangedListener: OnHeadsUpChangedListener
    private val headsUpManager: HeadsUpManager?

    init {
        val headsUpManagerOpt = optionalHeadsUpManager.orElse(null)
        this.headsUpManager = headsUpManagerOpt
        this.headsUpChangedListener = object : OnHeadsUpChangedListener {
            override fun onHeadsUpPinned(notificationEntry: NotificationEntry) {
                super.onHeadsUpPinned(notificationEntry)
            }

            override fun onHeadsUpPinnedModeChanged(isPinned: Boolean) {
                hasPinnedHeadsUp = isPinned
                updateAvailable()
            }

            override fun onHeadsUpStateChanged(notificationEntry: NotificationEntry, isHeadsUp: Boolean) {
                super.onHeadsUpStateChanged(notificationEntry, isHeadsUp)
            }

            override fun onHeadsUpUnPinned(notificationEntry: NotificationEntry) {
                super.onHeadsUpUnPinned(notificationEntry)
            }
        }

        this.gateListener = object : Gate.Listener {
            override fun onGateChanged(gate: Gate) {
                val isBlocking = silenceAlertsDisabled.isBlocking
                if (isBlocking) {
                    onSilenceAlertsDisabled()
                } else {
                    onSilenceAlertsEnabled()
                }
            }
        }

        if (headsUpManagerOpt == null) {
            Log.w(TAG, "No HeadsUpManager")
        } else {
            silenceAlertsDisabled.registerListener(gateListener)
        }

        updateAvailable()
    }

    private fun onSilenceAlertsDisabled() {
        headsUpManager?.removeListener(headsUpChangedListener)
    }

    private fun onSilenceAlertsEnabled() {
        headsUpManager?.addListener(headsUpChangedListener)
        headsUpManager?.let { hasPinnedHeadsUp = it.hasPinnedHeadsUp() }
    }

    private fun updateAvailable() {
        setAvailable(!silenceAlertsDisabled.isBlocking && hasPinnedHeadsUp)
    }

    override fun `getTag$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig`(): String {
        return TAG
    }

    override fun onTrigger(detectionProperties: GestureSensor.DetectionProperties) {
        headsUpManager?.unpinAll(true)
    }
}
