package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun UniversalAccessibilityHeader(
    isEasyMode: Boolean,
    onToggleEasyMode: () -> Unit,
    unreadAlertsCount: Int,
    onOpenAlerts: () -> Unit,
    onOpenWhatsApp: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("accessibility_header"),
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isEasyMode) 44.dp else 38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo_punto_cero_1784764036242),
                            contentDescription = "Logo Punto Cero",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Column {
                        Text(
                            text = "Punto Cero",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isEasyMode) 22.sp else 18.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = if (isEasyMode) "Modo Fácil / Accesible" else "Proyecto Escolar de Salud",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = if (isEasyMode) 13.sp else 11.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // WhatsApp Quick Call Button
                    IconButton(
                        onClick = onOpenWhatsApp,
                        modifier = Modifier
                            .testTag("btn_whatsapp_quick")
                            .size(if (isEasyMode) 48.dp else 40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "WhatsApp Médico",
                            tint = Color.White,
                            modifier = Modifier.size(if (isEasyMode) 26.dp else 20.dp)
                        )
                    }

                    // Alerts Badge Button
                    IconButton(
                        onClick = onOpenAlerts,
                        modifier = Modifier
                            .testTag("btn_alerts_badge")
                            .size(if (isEasyMode) 48.dp else 40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadAlertsCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    ) {
                                        Text(unreadAlertsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alertas",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(if (isEasyMode) 26.dp else 20.dp)
                            )
                        }
                    }

                    // Easy Mode Toggle
                    FilterChip(
                        selected = isEasyMode,
                        onClick = onToggleEasyMode,
                        label = {
                            Text(
                                text = if (isEasyMode) "Modo Fácil ON" else "Accesibilidad",
                                fontSize = if (isEasyMode) 13.sp else 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isEasyMode) Icons.Default.Visibility else Icons.Default.AccessibilityNew,
                                contentDescription = "Modo Accesible",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("toggle_easy_mode"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            labelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}
