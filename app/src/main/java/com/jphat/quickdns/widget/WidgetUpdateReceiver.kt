package com.jphat.quickdns.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver that handles centralized widget update requests.
 * Listens for ACTION_UPDATE_ALL_WIDGETS and triggers updates across all widget types.
 */
class WidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WidgetUpdateCoordinator.ACTION_UPDATE_ALL_WIDGETS) {
            // Delay slightly to allow system settings to be fully applied
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                WidgetUpdateCoordinator.updateAllWidgets(context)
            }, 300)
        }
    }
}
