package com.centro.app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/** Widget real de Android para Hábitos pendientes de hoy. */
public class HabitsWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetUpdater.updateHabits(context);
    }

    @Override
    public void onEnabled(Context context) {
        WidgetUpdater.updateHabits(context);
    }
}
