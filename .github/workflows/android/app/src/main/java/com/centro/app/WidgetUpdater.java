package com.centro.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Construye y envía las actualizaciones reales de RemoteViews para los 3 widgets nativos
 * de Centro (Nutrición, Agua, Hábitos), leyendo los datos que la app web (JavaScript) escribió
 * en SharedPreferences a través de CentroWidgetPlugin. No depende de que la app esté abierta:
 * se invoca cada vez que la app sincroniza datos, y además el sistema Android vuelve a llamar
 * a onUpdate() periódicamente y tras el arranque del teléfono.
 */
public class WidgetUpdater {
    public static final String PREFS = "centro_widgets";

    static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void updateAll(Context context) {
        updateNutrition(context);
        updateWater(context);
        updateHabits(context);
    }

    private static PendingIntent openAppIntent(Context context, int requestCode) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getActivity(context, requestCode, intent, flags);
    }

    public static void updateNutrition(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, NutritionWidgetProvider.class);
        int[] ids = mgr.getAppWidgetIds(cn);
        if (ids.length == 0) return;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_nutrition);
        try {
            String raw = prefs(context).getString("nutrition", null);
            JSONObject o = raw != null ? new JSONObject(raw) : new JSONObject();
            int cal = o.optInt("calories", 0);
            int calGoal = Math.max(1, o.optInt("caloriesGoal", 2200));
            int protein = o.optInt("protein", 0);
            int fat = o.optInt("fat", 0);
            int carbs = o.optInt("carbs", 0);
            int pct = Math.max(0, Math.min(100, (int) Math.round(cal * 100.0 / calGoal)));
            views.setTextViewText(R.id.widget_nutrition_kcal, cal + " / " + calGoal + " kcal");
            views.setProgressBar(R.id.widget_nutrition_progress, 100, pct, false);
            views.setTextViewText(R.id.widget_nutrition_protein, "P " + protein + "g");
            views.setTextViewText(R.id.widget_nutrition_fat, "G " + fat + "g");
            views.setTextViewText(R.id.widget_nutrition_carbs, "C " + carbs + "g");
        } catch (Exception ignored) { }
        views.setOnClickPendingIntent(R.id.widget_nutrition_root, openAppIntent(context, 101));
        mgr.updateAppWidget(cn, views);
    }

    public static void updateWater(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, WaterWidgetProvider.class);
        int[] ids = mgr.getAppWidgetIds(cn);
        if (ids.length == 0) return;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_water);
        try {
            String raw = prefs(context).getString("water", null);
            JSONObject o = raw != null ? new JSONObject(raw) : new JSONObject();
            int glasses = o.optInt("glasses", 0);
            int goal = Math.max(1, o.optInt("goalGlasses", 8));
            int pct = Math.max(0, Math.min(100, (int) Math.round(glasses * 100.0 / goal)));
            views.setTextViewText(R.id.widget_water_glasses, glasses + " / " + goal + " vasos");
            views.setProgressBar(R.id.widget_water_progress, 100, pct, false);
        } catch (Exception ignored) { }
        views.setOnClickPendingIntent(R.id.widget_water_root, openAppIntent(context, 102));
        mgr.updateAppWidget(cn, views);
    }

    public static void updateHabits(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, HabitsWidgetProvider.class);
        int[] ids = mgr.getAppWidgetIds(cn);
        if (ids.length == 0) return;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_habits);
        int[] rowIds = { R.id.widget_habit_row_1, R.id.widget_habit_row_2, R.id.widget_habit_row_3 };
        int[] textIds = { R.id.widget_habit_text_1, R.id.widget_habit_text_2, R.id.widget_habit_text_3 };
        try {
            String raw = prefs(context).getString("habits", null);
            JSONObject o = raw != null ? new JSONObject(raw) : new JSONObject();
            JSONArray items = o.optJSONArray("items");
            int total = items != null ? items.length() : 0;
            for (int i = 0; i < rowIds.length; i++) {
                if (items != null && i < items.length()) {
                    JSONObject h = items.getJSONObject(i);
                    String icon = h.optString("icon", "⭐");
                    String name = h.optString("name", "");
                    views.setViewVisibility(rowIds[i], android.view.View.VISIBLE);
                    views.setTextViewText(textIds[i], (icon + " " + name).trim());
                } else {
                    views.setViewVisibility(rowIds[i], android.view.View.GONE);
                }
            }
            int shown = Math.min(3, total);
            int remaining = total - shown;
            if (remaining > 0) {
                views.setViewVisibility(R.id.widget_habit_more, android.view.View.VISIBLE);
                views.setTextViewText(R.id.widget_habit_more, "+" + remaining + " más");
            } else {
                views.setViewVisibility(R.id.widget_habit_more, android.view.View.GONE);
            }
            views.setViewVisibility(R.id.widget_habit_empty, total == 0 ? android.view.View.VISIBLE : android.view.View.GONE);
        } catch (Exception ignored) { }
        views.setOnClickPendingIntent(R.id.widget_habits_root, openAppIntent(context, 103));
        mgr.updateAppWidget(cn, views);
    }
}
