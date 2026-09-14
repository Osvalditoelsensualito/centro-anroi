package com.centro.app;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Registra el plugin nativo de widgets antes de que Capacitor arranque el bridge.
        registerPlugin(CentroWidgetPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
