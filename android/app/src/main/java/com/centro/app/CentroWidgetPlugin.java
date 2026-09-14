package com.centro.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Puente JavaScript -> nativo Android para funciones que Capacitor no cubre de fábrica:
 * - Los widgets reales de la pantalla de inicio (Nutrición, Agua, Hábitos).
 * - La exención de optimización de batería, necesaria en muchos fabricantes (Xiaomi, Huawei,
 *   Samsung, etc.) para que las notificaciones y alarmas programadas sigan disparándose con la
 *   app cerrada durante mucho tiempo: Android por sí solo (AlarmManager) ya lo permite, pero
 *   estos fabricantes agregan sus propias restricciones agresivas de batería por encima del
 *   sistema operativo, y esta es la forma estándar de pedirle al usuario que las desactive para
 *   esta app en particular.
 */
@CapacitorPlugin(name = "CentroWidgets")
public class CentroWidgetPlugin extends Plugin {

    @PluginMethod
    public void sync(PluginCall call) {
        Context context = getContext();
        SharedPreferences.Editor editor = WidgetUpdater.prefs(context).edit();
        try {
            JSObject nutrition = call.getObject("nutrition");
            if (nutrition != null) editor.putString("nutrition", nutrition.toString());
            JSObject water = call.getObject("water");
            if (water != null) editor.putString("water", water.toString());
            JSObject habits = call.getObject("habits");
            if (habits != null) editor.putString("habits", habits.toString());
            editor.apply();
        } catch (Exception e) {
            call.reject("No se pudieron guardar los datos de los widgets", e);
            return;
        }
        WidgetUpdater.updateAll(context);
        call.resolve();
    }

    @PluginMethod
    public void checkBatteryOptimization(PluginCall call) {
        JSObject result = new JSObject();
        boolean ignoring = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
                ignoring = pm != null && pm.isIgnoringBatteryOptimizations(getContext().getPackageName());
            } catch (Exception e) {
                ignoring = true;
            }
        }
        result.put("ignoring", ignoring);
        call.resolve(result);
    }

    @PluginMethod
    public void requestIgnoreBatteryOptimizations(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } catch (Exception e) {
                // Algunos fabricantes bloquean este intent directo; el usuario puede llegar
                // al mismo ajuste manualmente desde Ajustes del sistema > Batería > Centro.
            }
        }
        call.resolve();
    }
}
