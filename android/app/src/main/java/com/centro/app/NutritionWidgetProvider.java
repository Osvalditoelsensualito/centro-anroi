package com.centro.app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/** Widget real de Android para Calorías/Macros. Lee los datos ya sincronizados por la app
 *  (ver CentroWidgetPlugin) y no depende de que la app esté abierta para mostrarse actualizado. */
public class NutritionWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetUpdater.updateNutrition(context);
    }

    @Override
    public void onEnabled(Context context) {
        WidgetUpdater.updateNutrition(context);
    }
}
