package com.centro.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Puente JavaScript -> nativo Android para los widgets reales de la pantalla de inicio.
 * La app web llama a CentroWidgets.sync({...}) cada vez que cambian los datos de calorías,
 * agua o hábitos (mismo punto donde ya se sincronizan las notificaciones nativas), y este
 * plugin guarda esos datos en SharedPreferences y empuja la actualización real de los 3
 * widgets (AppWidgetManager.updateAppWidget), sin depender de que la app permanezca abierta.
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
}
