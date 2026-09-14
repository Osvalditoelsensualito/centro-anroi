package com.centro.app;

import android.os.Bundle;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Registra el plugin nativo (widgets + puente de batería/pantalla completa) antes de
        // que Capacitor arranque el bridge.
        registerPlugin(CentroWidgetPlugin.class);
        super.onCreate(savedInstanceState);
        enableImmersiveMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // El sistema puede volver a mostrar las barras (por ejemplo, al abrir el panel de
        // notificaciones y volver, o al mostrarse un diálogo). Las volvemos a ocultar cada vez
        // que la ventana recupera el foco, tal como recomienda la documentación de Android para
        // el modo inmersivo real.
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    /**
     * Modo de pantalla completa inmersivo real: oculta la barra de estado (hora, batería,
     * wifi, notificaciones) y la barra de navegación del sistema, dejando que el usuario las
     * revele momentáneamente con un deslizamiento desde el borde si lo necesita (por ejemplo,
     * para abrir el panel de notificaciones), y que vuelvan a ocultarse solas después.
     */
    private void enableImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }
}
