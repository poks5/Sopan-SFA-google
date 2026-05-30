package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SopanEmerald
import com.example.ui.theme.SopanTeal
import com.example.ui.theme.SopanOrange

@Composable
fun LoginScreen(
    viewModel: SfaViewModel,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("sopan123") }
    var selectedRole by remember { mutableStateOf("MR") }
    var showPassword by remember { mutableStateOf(false) }

    val rolesList = listOf(
        "MR" to "Medical Representative",
        "ASM" to "Area Sales Manager",
        "Manager" to "Operational Manager",
        "Executive" to "Strategic Leader",
        "Admin" to "System Administrator"
    )

    val prefilledPersonas = mapOf(
        "MR" to listOf("Sohan Shrestha", "Bipin Thapa"),
        "ASM" to "Mr. Dev Raj Adhikari",
        "Manager" to listOf("Mr. Suman Guragain", "Mr. Sharad Khanal"),
        "Executive" to listOf("Mr. Suman Neupane", "Mr. Anil Shakya"),
        "Admin" to listOf("Administrator", "Sopan Admin")
    )

    // Infinite ambient animations for the gorgeous background orbs
    val infiniteTransition = rememberInfiniteTransition(label = "BgAnimation")
    
    val orbOffset1 by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb1"
    )
    
    val orbOffset2 by infiniteTransition.animateFloat(
        initialValue = 150f,
        targetValue = -150f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 2026 Ambient Dynamic Glow Mesh in Background
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Draw Glowing Teal Orb at Top Left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SopanTeal.copy(alpha = 0.45f), Color.Transparent),
                    center = this.center.copy(
                        x = 80f + orbOffset1,
                        y = 120f + orbOffset2
                    ),
                    radius = 500f
                ),
                radius = 550f,
                center = this.center.copy(
                    x = 80f + orbOffset1,
                    y = 120f + orbOffset2
                )
            )

            // Draw Glowing Emerald Orb at Bottom Right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SopanEmerald.copy(alpha = 0.35f), Color.Transparent),
                    center = this.center.copy(
                        x = size.width - 100f - orbOffset2,
                        y = size.height - 150f - orbOffset1
                    ),
                    radius = 600f
                ),
                radius = 650f,
                center = this.center.copy(
                    x = size.width - 100f - orbOffset2,
                    y = size.height - 150f - orbOffset1
                )
            )

            // Draw Soft Tech Orange Accent in Center
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SopanOrange.copy(alpha = 0.15f), Color.Transparent),
                    center = this.center,
                    radius = 400f
                ),
                radius = 450f,
                center = this.center
            )
        }

        // Subtly layered decorative grid lines to look like an analytics/SFA system console
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.015f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main glass content card wrapper
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .wrapContentHeight()
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                Color.Transparent,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .testTag("login_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Header Brand Section
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(SopanTeal, SopanEmerald),
                                            tileMode = TileMode.Clamp
                                        )
                                    )
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MedicalServices,
                                    contentDescription = "Sopan SFA Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "SOPAN SFA",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 3.sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "PHARMA & PV COMPLIANCE SUITE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 2.dp),
                                textAlign = TextAlign.Center
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = "Region",
                                        tint = SopanEmerald,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "The Healing Partner \u2022 Nepal Hub",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SopanEmerald
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "\uD83C\uDDF3\uD83C\uDDF5",
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // Separation Divider with subtle glow-dots
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(SopanTeal.copy(alpha = 0.5f))
                            )
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                        }
                    }

                    // Persona Role Chooser - Clean grid/segmented capsule structure
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Select SFA Module Profile",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                rolesList.forEach { (roleKey, roleName) ->
                                    val isSelected = selectedRole == roleKey
                                    
                                    val icon = when(roleKey) {
                                        "MR" -> Icons.Filled.LocalHospital
                                        "ASM" -> Icons.Filled.WorkspacePremium
                                        "Manager" -> Icons.Filled.BusinessCenter
                                        "Executive" -> Icons.Filled.Leaderboard
                                        else -> Icons.Filled.Person
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .then(
                                                if (isSelected) {
                                                    Modifier.background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(SopanTeal, SopanTeal.copy(alpha = 0.8f))
                                                        )
                                                    )
                                                } else {
                                                    Modifier
                                                }
                                            )
                                            .clickable {
                                                selectedRole = roleKey
                                                username = when (roleKey) {
                                                    "MR" -> "Sohan Shrestha"
                                                    "ASM" -> "Mr. Dev Raj Adhikari"
                                                    "Manager" -> "Mr. Suman Guragain"
                                                    "Executive" -> "Mr. Suman Neupane"
                                                    else -> ""
                                                }
                                            }
                                            .testTag("role_tab_$roleKey"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = roleKey,
                                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = roleKey,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp
                                                ),
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Input Form Fields (Outlined but styled with 2026 glassy backgrounds)
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Authorized Identity Name") },
                                placeholder = { Text("Enter reference name") },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Filled.Person, 
                                        contentDescription = "User",
                                        tint = if (username.isNotEmpty()) SopanTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    ) 
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("username_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SopanTeal,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                )
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Secure Access PIN") },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Filled.Lock, 
                                        contentDescription = "Password",
                                        tint = if (password.isNotEmpty()) SopanTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    ) 
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = "Toggle password visibility",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("password_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SopanTeal,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }

                    // Quick-Selector Authorized Employee Capsule Chips
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.OfflinePin,
                                    contentDescription = "Quick access",
                                    tint = SopanOrange,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Quick-Fill Active Profile ID",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                                )
                            }

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val list = when (val p = prefilledPersonas[selectedRole]) {
                                    is String -> listOf(p)
                                    is List<*> -> p.filterIsInstance<String>()
                                    else -> emptyList()
                                }
                                list.forEach { name ->
                                    val isSelectedChip = username == name
                                    SuggestionChip(
                                        onClick = { username = name },
                                        label = { 
                                            Text(
                                                text = name, 
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelectedChip) FontWeight.Bold else FontWeight.Medium
                                            ) 
                                        },
                                        modifier = Modifier.testTag("quick_persona_$name"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = if (isSelectedChip) SopanTeal.copy(alpha = 0.08f) else Color.Transparent,
                                            labelColor = if (isSelectedChip) SopanTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (isSelectedChip) SopanTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Submit Action Button - High contrast, dynamic gradient with ripple
                    item {
                        Button(
                            onClick = {
                                if (username.isNotBlank()) {
                                    viewModel.login(selectedRole, username)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent // handled by background brush
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(SopanTeal, SopanEmerald)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Filled.Login, contentDescription = "Login", tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "INITIALIZE SFA SESSION",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.5.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // WHO-GMP and ISO standard details badge
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                    .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "WHO-GMP \u2022 National GMP \u2022 ISO 9001:2015",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SopanEmerald,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Sopan Pharmaceuticals Manufacturing Facility",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "Lalitpur - 22, Nepal",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}
