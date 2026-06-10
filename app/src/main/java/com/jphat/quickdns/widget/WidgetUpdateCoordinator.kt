package com.jphat.quickdns.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Centralized coordinator for updating all widget instances across all widget types.
 * Ensures that when one widget is toggled, all other widgets are updated immediately.
 */
object WidgetUpdateCoordinator {
    
    const val ACTION_UPDATE_ALL_WIDGETS = "com.jphat.quickdns.ACTION_UPDATE_ALL_WIDGETS"
    
    /**
     * Triggers an update for all widget instances of all types
     */
    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        
        // Update PrivateDNSWidget instances
        val smallWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, PrivateDNSWidget::class.java)
        )
        if (smallWidgetIds.isNotEmpty()) {
            val smallWidget = PrivateDNSWidget()
            smallWidget.onUpdate(context, appWidgetManager, smallWidgetIds)
        }
        
        // Update PrivateDNSWidgetLarge instances
        val largeWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, PrivateDNSWidgetLarge::class.java)
        )
        if (largeWidgetIds.isNotEmpty()) {
            val largeWidget = PrivateDNSWidgetLarge()
            largeWidget.onUpdate(context, appWidgetManager, largeWidgetIds)
        }
        
        // Update PrivateDNSWidgetTile instances
        val tileWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, PrivateDNSWidgetTile::class.java)
        )
        if (tileWidgetIds.isNotEmpty()) {
            val tileWidget = PrivateDNSWidgetTile()
            tileWidget.onUpdate(context, appWidgetManager, tileWidgetIds)
        }
    }
    
    /**
     * Broadcasts an intent to trigger updates on all widgets
     */
    fun broadcastWidgetUpdate(context: Context) {
        val intent = Intent(context, WidgetUpdateReceiver::class.java)
        intent.action = ACTION_UPDATE_ALL_WIDGETS
        context.sendBroadcast(intent)
    }
}
