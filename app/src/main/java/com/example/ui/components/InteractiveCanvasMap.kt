package com.example.ui.components

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.local.HealthFacilityEntity
import com.example.data.local.ReportEntity

data class SelectedMapPin(
    val title: String,
    val subtitle: String,
    val details: String,
    val type: String
)

data class TappedMapCoordinate(
    val lat: Double,
    val lng: Double,
    val locationName: String
)

class MapBridge(
    val onPinSelected: (title: String, subtitle: String, details: String, type: String) -> Unit,
    val onMapTapped: (lat: Double, lng: Double) -> Unit
) {
    @JavascriptInterface
    fun onPinClick(title: String, subtitle: String, details: String, type: String) {
        onPinSelected(title, subtitle, details, type)
    }

    @JavascriptInterface
    fun onMapClick(lat: Double, lng: Double) {
        onMapTapped(lat, lng)
    }
}

@Composable
fun InteractiveCanvasMap(
    reports: List<ReportEntity>,
    facilities: List<HealthFacilityEntity>,
    selectedFilter: String, // "Todos", "Brotes/Casos", "Criaderos", "Centros Salud", "Farmacias"
    onAddReportClick: () -> Unit,
    onAddReportAtLocation: (locationName: String, lat: Double, lng: Double) -> Unit,
    isEasyMode: Boolean
) {
    var selectedPin by remember { mutableStateOf<SelectedMapPin?>(null) }
    var tappedCoord by remember { mutableStateOf<TappedMapCoordinate?>(null) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Generate HTML for Leaflet + OpenStreetMap (Global Real Map)
    val mapHtml = remember {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map { height: 100%; width: 100%; margin: 0; padding: 0; background: #e2ece9; }
                .leaflet-popup-content-wrapper { border-radius: 12px; font-family: system-ui, sans-serif; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
                .leaflet-popup-content { margin: 12px; font-size: 13px; line-height: 1.4; }
                .custom-pin { transition: transform 0.2s ease; }
                .custom-pin:hover { transform: scale(1.2); }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false, attributionControl: false }).setView([2.1865, -75.8924], 14);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    minZoom: 2
                }).addTo(map);

                var tempMarker = null;

                map.on('click', function(e) {
                    var lat = e.latlng.lat;
                    var lng = e.latlng.lng;

                    if (tempMarker) map.removeLayer(tempMarker);

                    var pulseIcon = L.divIcon({
                        className: 'custom-pulse',
                        html: "<div style='background:#00B0FF;width:22px;height:22px;border-radius:50%;border:3px solid white;box-shadow:0 0 8px rgba(0,176,255,0.8);'></div>",
                        iconSize: [22, 22],
                        iconAnchor: [11, 11]
                    });

                    tempMarker = L.marker([lat, lng], { icon: pulseIcon }).addTo(map);

                    if (window.AndroidBridge) {
                        window.AndroidBridge.onMapClick(lat, lng);
                    }
                });

                function clearAllMarkers() {
                    map.eachLayer(function(layer) {
                        if (layer instanceof L.Marker && layer !== tempMarker) {
                            map.removeLayer(layer);
                        }
                    });
                }

                function addMarker(lat, lng, title, subtitle, details, type) {
                    var color = '#f57c00'; // Criadero (Orange)
                    if (type === 'Caso') color = '#d32f2f'; // Caso (Red)
                    if (type === 'Fumigado') color = '#388e3c'; // Fumigado (Green)
                    if (type === 'CentroSalud') color = '#0288d1'; // Hospital (Blue)
                    if (type === 'Farmacia') color = '#7b1fa2'; // Farmacia (Purple)

                    var iconHtml = "<div style='background:" + color + ";width:20px;height:20px;border-radius:50%;border:3px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.4);'></div>";

                    var markerIcon = L.divIcon({
                        className: 'custom-pin',
                        html: iconHtml,
                        iconSize: [20, 20],
                        iconAnchor: [10, 10]
                    });

                    var m = L.marker([lat, lng], { icon: markerIcon }).addTo(map);
                    m.bindPopup("<b>" + title + "</b><br/><span style='color:#555;'>" + subtitle + "</span><br/><p style='margin:4px 0;'>" + details + "</p>");
                    
                    m.on('click', function() {
                        if (window.AndroidBridge) {
                            window.AndroidBridge.onPinClick(title, subtitle, details, type);
                        }
                    });
                }

                function flyTo(lat, lng, zoom) {
                    map.flyTo([lat, lng], zoom, { duration: 1.5 });
                }

                function zoomIn() { map.zoomIn(); }
                function zoomOut() { map.zoomOut(); }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    // Update markers on webview whenever reports, facilities or filter change
    fun updateMapMarkers(wv: WebView) {
        wv.evaluateJavascript("clearAllMarkers();", null)

        val activeReports = reports.filter { r ->
            val type = when (r.reportType) {
                "Caso Dengue/Zika" -> "Caso"
                "Criadero Detectado" -> "Criadero"
                "Zona Fumigada" -> "Fumigado"
                else -> "Criadero"
            }
            when (selectedFilter) {
                "Brotes/Casos" -> type == "Caso"
                "Criaderos" -> type == "Criadero"
                else -> selectedFilter == "Todos"
            }
        }

        activeReports.forEachIndexed { idx, r ->
            val type = when (r.reportType) {
                "Caso Dengue/Zika" -> "Caso"
                "Criadero Detectado" -> "Criadero"
                "Zona Fumigada" -> "Fumigado"
                else -> "Criadero"
            }
            val lat = if (r.latitude != 0.0) r.latitude else 2.1865 + (idx * 0.003 - 0.006)
            val lng = if (r.longitude != 0.0) r.longitude else -75.8924 + (idx * 0.004 - 0.008)
            val safeTitle = r.title.replace("'", "\\'")
            val safeSub = "${r.reportType} • ${r.locationName}".replace("'", "\\'")
            val safeDet = r.description.replace("'", "\\'")
            val js = "addMarker($lat, $lng, '$safeTitle', '$safeSub', '$safeDet', '$type');"
            wv.evaluateJavascript(js, null)
        }

        val activeFacilities = facilities.filter { f ->
            val type = if (f.facilityType.contains("Farmacia")) "Farmacia" else "CentroSalud"
            when (selectedFilter) {
                "Centros Salud" -> type == "CentroSalud"
                "Farmacias" -> type == "Farmacia"
                else -> selectedFilter == "Todos"
            }
        }

        activeFacilities.forEach { f ->
            val type = if (f.facilityType.contains("Farmacia")) "Farmacia" else "CentroSalud"
            val safeTitle = f.name.replace("'", "\\'")
            val safeSub = "${f.facilityType} • ${f.address}".replace("'", "\\'")
            val safeDet = "Tel: ${f.phone} | Disponibilidad: ${f.availableMeds}".replace("'", "\\'")
            val js = "addMarker(${f.latitude}, ${f.longitude}, '$safeTitle', '$safeSub', '$safeDet', '$type');"
            wv.evaluateJavascript(js, null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interactive_map_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2ECE9))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            // Real Global OpenStreetMap via WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                updateMapMarkers(this@apply)
                            }
                        }
                        addJavascriptInterface(
                            MapBridge(
                                onPinSelected = { title, subtitle, details, type ->
                                    selectedPin = SelectedMapPin(title, subtitle, details, type)
                                    tappedCoord = null
                                },
                                onMapTapped = { lat, lng ->
                                    val locName = "Coordenadas: ${String.format("%.4f", lat)}°, ${String.format("%.4f", lng)}° (Huila / Global)"
                                    tappedCoord = TappedMapCoordinate(lat, lng, locName)
                                    selectedPin = null
                                }
                            ),
                            "AndroidBridge"
                        )
                        loadDataWithBaseURL("https://openstreetmap.org", mapHtml, "text/html", "UTF-8", null)
                        webViewRef = this
                    }
                },
                update = { wv ->
                    updateMapMarkers(wv)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Top Control Bar: Global Map Indicator & Zoom Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 3.dp,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "🌎 Mapa Global OpenStreetMap",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset / Center to El Pescador, La Argentina
                    IconButton(
                        onClick = {
                            webViewRef?.evaluateJavascript("flyTo(2.1865, -75.8924, 14);", null)
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.92f), CircleShape)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Ir a El Pescador", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    }

                    // Zoom In
                    IconButton(
                        onClick = { webViewRef?.evaluateJavascript("zoomIn();", null) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.92f), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Acercar", modifier = Modifier.size(18.dp))
                    }

                    // Zoom Out
                    IconButton(
                        onClick = { webViewRef?.evaluateJavascript("zoomOut();", null) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.92f), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Alejar", modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Bottom Map Instructions
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🌐 Desplázate por Colombia / Mundo • Toca cualquier punto para reportar",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Main Floating Action Button: Report
            FloatingActionButton(
                onClick = onAddReportClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .testTag("fab_add_report_map"),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = "Reportar Foco")
                    Text("Reportar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Tapped Location Action Banner
        AnimatedVisibility(
            visible = tappedCoord != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            tappedCoord?.let { coord ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📍 Coordenada Seleccionada en Mapa Global",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = coord.locationName,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Button(
                            onClick = {
                                onAddReportAtLocation(coord.locationName, coord.lat, coord.lng)
                                tappedCoord = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reportar Aquí", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Selected Existing Pin Card Details
        AnimatedVisibility(visible = selectedPin != null) {
            selectedPin?.let { pin ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("card_map_pin_detail"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = when(pin.type) {
                                        "Caso" -> Icons.Default.Warning
                                        "Criadero" -> Icons.Default.WaterDrop
                                        "CentroSalud" -> Icons.Default.LocalHospital
                                        "Farmacia" -> Icons.Default.LocalPharmacy
                                        else -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = pin.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            IconButton(
                                onClick = { selectedPin = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }

                        Text(
                            text = pin.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (pin.details.isNotBlank()) {
                            Text(
                                text = pin.details,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
