# Centro — Proyecto Android (Capacitor)

Este es el proyecto Android **completo y real** generado a partir de tu aplicación web "Centro"
(repositorio `Osvalditoelsensualito/centro`). No es una explicación de cómo hacerlo: es el
código ya escrito, listo para compilar.

## Qué se hizo

Se analizó por completo tu `index.html` (habitos, tareas/recordatorios, agua, calorías con
análisis por foto vía Gemini, running/pasos, trading multi-cuenta, finanzas, gimnasio,
películas, notas, clima de Montevideo, recordatorio de smartwatch, ajustes, etc.) y se envolvió
con [Capacitor](https://capacitorjs.com) para convertirlo en una app Android nativa real,
manteniendo el 100% de tu lógica de negocio (toda tu app original vive, sin recortes, en
`www/index.html`).

Cambios respecto a la versión web, exactamente los que pediste:

- **Almacenamiento local real**: los datos siguen viviendo en IndexedDB/localStorage, pero
  ahora corren dentro del WebView nativo de Android, en el almacenamiento privado de la app
  (no en Chrome). Sobreviven a cerrar la app, reiniciar el teléfono y funcionan sin conexión.
- **Notificaciones reales del sistema Android**: se agregó el plugin
  `@capacitor/local-notifications`. Ahora hay un botón "Activar notificaciones" en Ajustes,
  y los recordatorios, hábitos con hora y el aviso de agua se programan como notificaciones
  reales de Android (siguen funcionando con la app cerrada, incluso tras reiniciar el
  teléfono, gracias al receiver de arranque que instala el propio plugin).
- **Sin reescalado manual**: se quitó el control de "Escalado visual" y "Margen lateral" de
  Ajustes. La interfaz (ya era responsive) se adapta sola al tamaño/densidad real del
  teléfono.
- **Exportar / Importar / Borrar todo**: se mantuvo tal cual (ya estaba muy bien resuelto en
  tu app). En Android, "Descargar datos" ahora usa el sistema de archivos nativo + la hoja de
  compartir de Android para guardar o enviar el JSON (en la web se seguía usando la descarga
  de blob de siempre). Importar y "Eliminar todos mis datos" no cambiaron.
- **Gemini**: tu integración con la API de Gemini (la API key que se guarda en Ajustes) se
  mantuvo intacta; solo necesita conexión a Internet cuando la usas, igual que antes.

## ⚠️ Importante: por qué no te entrego ya el archivo .apk compilado

Intenté compilar la APK directamente en este entorno, pero la política de red de este entorno
en la nube **bloquea el acceso a los repositorios de Google (`dl.google.com`,
`maven.google.com`) y a Maven Central**, que son imprescindibles para descargar el Android
SDK y las librerías de Android/AndroidX. No es algo que pueda rodear: no es una limitación de
la app, es una restricción de red de este entorno de trabajo.

Para solucionarlo, dejé preparado un **workflow de GitHub Actions**
(`.github/workflows/build-apk.yml`) que compila la APK automáticamente en los servidores de
GitHub (que sí tienen acceso completo) apenas subas este proyecto a tu repositorio. No
necesitas instalar nada en tu computadora.

### Opción A (recomendada): dejar que GitHub compile la APK por ti

1. Descomprime este proyecto.
2. Copia **todo** el contenido (carpetas `android/`, `www/`, `.github/`, y los archivos
   `package.json`, `package-lock.json`, `capacitor.config.json`, `.gitignore`) dentro de tu
   repositorio local de `centro` (el mismo que ya tienes clonado, o clónalo de nuevo).
3. Sube los cambios:
   ```bash
   cd centro
   git add .
   git commit -m "Agregar proyecto Android (Capacitor) de Centro"
   git push
   ```
4. Entra a la pestaña **Actions** de tu repositorio en GitHub. Vas a ver correr
   "Build Centro APK". Cuando termine (2-4 minutos), abre esa ejecución y descarga el archivo
   `centro-debug-apk` en "Artifacts": ahí está tu `app-debug.apk`, ya firmado con una clave de
   depuración, lista para instalar en cualquier Android (activando "Instalar apps de fuentes
   desconocidas" si tu teléfono lo pide).
5. Cada vez que vuelvas a subir cambios a `main`, se genera una APK nueva automáticamente.

### Opción B: compilarla vos mismo con Android Studio

1. Abre la carpeta `android/` de este proyecto con Android Studio (Android Studio ya trae el
   SDK necesario).
2. Espera a que sincronice Gradle.
3. Build → Build Bundle(s) / APK(s) → Build APK(s).
4. La APK queda en `android/app/build/outputs/apk/debug/app-debug.apk`.

### Opción C: compilarla vos mismo por línea de comandos

Necesitas tener instalado el Android SDK (por ejemplo vía Android Studio) y Node.js.

```bash
npm install
npx cap copy android
cd android
./gradlew assembleDebug
# La APK queda en android/app/build/outputs/apk/debug/app-debug.apk
```

## Permisos y notificaciones — cosas a tener en cuenta

- Al abrir la app por primera vez, andá a **Ajustes → Notificaciones → Activar
  notificaciones** para conceder el permiso (obligatorio desde Android 13).
- En Android 12 o superior, para que los recordatorios se disparen exactamente a la hora
  programada, Android puede pedirte el permiso especial "Alarmas y recordatorios" (esto lo
  pide el propio sistema operativo, no algo que la app pueda evitar).
- El recordatorio de agua se programa en horarios fijos (9:00, 12:00, 15:00, 18:00, 21:00)
  mientras no hayas completado tu meta del día; es la forma estándar en que Android permite
  reprogramar avisos incluso con la app cerrada (a diferencia de la cuenta regresiva dinámica
  que solo puede correr con la app abierta).

## Estructura del proyecto

```
centro-android/
├── www/index.html          ← tu app (con los cambios de arriba), empaquetada en la APK
├── android/                 ← proyecto Android nativo completo (Gradle, manifest, íconos…)
├── .github/workflows/       ← compilación automática en la nube
├── capacitor.config.json
└── package.json
```

## Ícono y pantalla de bienvenida

El ícono de la app, el ícono de las notificaciones y la pantalla de carga se generan ahora a
partir del logo **"Claridad"** (el logo blanco/claro), que es el logo predeterminado de Centro
desde esta corrección.

---

## Corrección #2 (sobre esta misma versión) — qué se arregló

Esta ronda **no reescribió la app**: se tomó exactamente el proyecto anterior y se aplicaron
correcciones puntuales sobre él, tal como pediste.

### 1. Notificaciones reales de Android — causa del problema y arreglo

Encontramos la causa concreta de que el recordatorio de prueba solo apareciera *dentro* de la
app: la app programaba la notificación real de Android con un pequeño retraso ("debounce" de
1.5 s) después de guardar el recordatorio, para no recalcular todo en cada tecla. Si cerrabas o
minimizabas la app en ese lapso (lo más probable al hacer una prueba rápida), ese temporizador
nunca llegaba a ejecutarse y la alarma real del sistema **nunca se llegaba a programar** — por
eso a la hora indicada no había notificación del sistema, solo la burbuja interna (que sí sigue
funcionando con la app abierta, y que se mantuvo porque también es parte de tu funcionalidad
actual).

Arreglos aplicados:
- Se programa la notificación real de Android **de inmediato** (sin esperar) apenas creas o
  editas un recordatorio o hábito, o marcas/desmarcas uno como hecho.
- Se agregó un listener de "pausa" de la app: si igualmente llegaras a minimizarla en ese
  instante, Android avisa a la app un momento antes de pasar a segundo plano, y ahí forzamos la
  programación real para que quede en manos del sistema operativo, no de la pestaña abierta.
- Se creó un canal de notificaciones propio de Centro con importancia **alta** (antes se usaba
  el canal "default" del plugin), para que la notificación se muestre como una notificación
  emergente real de Android, con luz y vibración, y no una notificación silenciosa de baja
  prioridad.
- Se agregó soporte para el permiso especial "Alarmas y recordatorios" que Android 12+ pide por
  separado del permiso de notificaciones: si tu teléfono lo requiere, en Ajustes → Notificaciones
  vas a ver un botón "Permitir alarmas exactas" que te lleva directo a esa pantalla del sistema.
- Se agregó un botón "🧪 Probar notificación real (10 s)" en Ajustes → Notificaciones: prográmalo,
  cierra la app por completo y en 10 segundos debe aparecer en la barra de notificaciones de tu
  teléfono. Esta es la forma más directa de confirmar que todo quedó funcionando en tu equipo —
  no tenemos manera de instalar la APK y probarla en un teléfono real desde este entorno de
  trabajo, así que te pedimos que hagas esta prueba de 10 segundos vos mismo apenas instales la
  nueva APK.

### 2. Logo predeterminado

El logo predeterminado de la app ahora es **"Claridad"** (el logo blanco), en vez del logo ámbar
anterior. Esto se cambió tanto en el ícono real de la app instalada en Android (que antes "Cambiar
logo" no podía tocar, porque el ícono del launcher se compila dentro del APK) como en el logo que
se ve dentro de la propia app, arriba de cada pantalla, junto a la palabra "CENTRO". Los tres
logos de "Cambiar logo" (Aurora, Claridad, Vívido) ahora son los que realmente se usan dentro de
la app: al elegir uno, ese logo pasa a mostrarse de inmediato en el encabezado de todas las
pantallas.

### 3. Escalado general

Se ajustó el factor de escala automático (ya existía, no había ningún control manual) para que en
teléfonos el contenido se vea un poco más chico / con más aire, en vez de sentirse "pegado" a la
pantalla. Sigue siendo 100% automático y responsive.

### 4. Rediseño visual

Se refinó la apariencia general (tarjetas con sombra suave y más aire, tipografía y jerarquía más
marcadas, botones con mejor feedback al tocar, barra inferior con una "píldora" detrás del ítem
activo) inspirado en las referencias que enviaste, sin tocar ninguna lógica ni quitar
funcionalidad. Las tarjetas de resumen de Inicio (agua/calorías) ahora tienen un degradé de color
sutil y animado detrás, al estilo "burbuja de color" que mostraste.

### Qué pudimos verificar y qué no

Desde este entorno de trabajo no tenemos un teléfono ni un emulador Android para instalar la APK
y probarla de forma real — solo puede compilarse en la nube (ver Opción A más abajo) o en tu
computadora. Lo que sí hicimos: revisamos el código Java del plugin de notificaciones que ya
tenías instalado para confirmar exactamente cómo maneja los permisos, el canal, las alarmas
exactas y el reinicio del teléfono, verificamos que los 4 bloques de JavaScript de la app siguen
siendo válidos después de todos los cambios, y renderizamos las pantallas modificadas para
revisar visualmente el rediseño. La prueba real de extremo a extremo (que la notificación llegue
a tu barra de notificaciones) queda pendiente de que la hagas vos con el botón de prueba de 10
segundos apenas instales esta nueva APK — si por algún motivo no llegara, avisame exactamente qué
viste (¿ni siquiera con la app cerrada? ¿tu teléfono es Xiaomi/Huawei/Samsung con restricciones de
batería agresivas?) para seguir ajustando.
