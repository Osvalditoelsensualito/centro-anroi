package com.centro.app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/** Widget real de Android para Agua. */
public class WaterWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetUpdater.updateWater(context);
    }

    @Override
    public void onEnabled(Context context) {
        WidgetUpdater.updateWater(context);
    }
}
