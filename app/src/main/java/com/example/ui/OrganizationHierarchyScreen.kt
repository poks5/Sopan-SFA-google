package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SopanEmerald
import com.example.ui.theme.SopanOrange
import com.example.ui.theme.SopanTeal
import com.example.data.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun OrganizationHierarchyTab(viewModel: SfaViewModel) {
    var activeSubTab by remember { mutableStateOf("ORG_HIERARCHY") } // ORG_HIERARCHY, COMM_CHAIN, AUDIT_DECK
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quick visual header
        Card(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccountTree,
                        contentDescription = "Hierarchy",
                        tint = SopanTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Organizational & Commercial Router",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A robust plan & visual blueprint of Nepal territory distribution and reporting structures.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Premium Sub-Tabs Segment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val subTabs = listOf(
                        Triple("ORG_HIERARCHY", "Org & Staff", Icons.Filled.CorporateFare),
                        Triple("COMM_CHAIN", "Partners", Icons.Filled.LocalPharmacy),
                        Triple("PRODUCTS", "Products", Icons.Filled.Medication),
                        Triple("CLOUD_SYNC", "Sync Cloud", Icons.Filled.CloudSync)
                    )

                    subTabs.forEach { (route, title, icon) ->
                        val isSelected = activeSubTab == route
                        Surface(
                            color = if (isSelected) SopanTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) SopanTeal else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeSubTab = route }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = if (isSelected) SopanTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = title,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SopanTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sub tab contents
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeSubTab) {
                "ORG_HIERARCHY" -> OrgHierarchySubTab(viewModel)
                "COMM_CHAIN" -> CommChainSubTab(viewModel)
                "PRODUCTS" -> ProductsSubTab(viewModel)
                "CLOUD_SYNC" -> FirebaseConnectSubTab(viewModel)
            }
        }
    }
}

@Composable
fun FirebaseConnectSubTab(viewModel: SfaViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncStatusMsg by viewModel.syncStatusMessage.collectAsState()
    val testResult by viewModel.connectionTestResult.collectAsState()

    val attendanceList by viewModel.allAttendance.collectAsState()
    val tourList by viewModel.allTourPlans.collectAsState()
    val bookingsList by viewModel.allOrderBookings.collectAsState()
    val auditsList by viewModel.allCompetitorAudits.collectAsState()
    val adrsList by viewModel.allADRReports.collectAsState()

    val initialManager = remember { viewModel.getSyncManager(context) }
    var dbUrl by remember { mutableStateOf(initialManager.getDatabaseUrl()) }
    var dbSecret by remember { mutableStateOf(initialManager.getDatabaseSecret()) }
    var secretVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module Introduction Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Sync Settings",
                    tint = SopanTeal,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Realtime Firebase Synchronizer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SopanTeal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This synchronization subsystem facilitates seamless offline-to-online replication between this Android client's primary Room Database and a secure cloud Firebase Realtime Database.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Active State Progress Bar
        if (isSyncing) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SopanTeal.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, SopanTeal.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = SopanTeal,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cloud Sync Connection Active...",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SopanTeal
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        color = SopanTeal,
                        trackColor = SopanTeal.copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }
        }

        // Configuration Form Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Dns,
                        contentDescription = "Server Config",
                        tint = SopanTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Connection Configurations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                // Database URL Input
                OutlinedTextField(
                    value = dbUrl,
                    onValueChange = { dbUrl = it },
                    label = { Text("Firebase RTDB Reference URL") },
                    placeholder = { Text("https://[your-project]-default-rtdb.firebaseio.com/") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("firebase_url_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SopanTeal,
                        focusedLabelColor = SopanTeal
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Link,
                            contentDescription = "DB URL",
                            tint = SopanTeal.copy(alpha = 0.6f)
                        )
                    }
                )

                // Database Secret Input
                OutlinedTextField(
                    value = dbSecret,
                    onValueChange = { dbSecret = it },
                    label = { Text("Database API Secret / Auth token (Optional)") },
                    placeholder = { Text("Enter secret key or token") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("firebase_key_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SopanTeal,
                        focusedLabelColor = SopanTeal
                    ),
                    visualTransformation = if (secretVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = "DB Secret",
                            tint = SopanTeal.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { secretVisible = !secretVisible }) {
                            Icon(
                                imageVector = if (secretVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle Secret Eye"
                            )
                        }
                    }
                )

                // Action Buttons Row inside panel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save parameters
                    Button(
                        onClick = {
                            viewModel.saveFirebaseConfig(context, dbUrl, dbSecret)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SopanTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.1f).testTag("save_firebase_config_btn")
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Settings", fontSize = 11.5.sp)
                    }

                    // Pre-fill demo
                    OutlinedButton(
                        onClick = {
                            dbUrl = "https://sopan-sfa-ledger-default-rtdb.asia-southeast1.firebasedatabase.app/"
                            dbSecret = ""
                            viewModel.saveFirebaseConfig(context, dbUrl, dbSecret)
                        },
                        border = BorderStroke(1.2.dp, SopanTeal),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SopanTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("demo_sandbox_btn")
                    ) {
                        Icon(Icons.Filled.OfflineBolt, contentDescription = "Demo sandbox", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Demo Sandbox", fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Network Status and Connection Diagnostics Box
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.OfflineBolt,
                            contentDescription = "Diagnostics",
                            tint = SopanTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connection Diagnostics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Color Indicator Dot
                    val stateColor = when {
                        testResult == null -> SopanOrange
                        testResult?.isSuccess == true -> SopanEmerald
                        else -> MaterialTheme.colorScheme.error
                    }
                    val stateLabel = when {
                        testResult == null -> "Unverified"
                        testResult?.isSuccess == true -> "Connected (${testResult?.latencyMs}ms)"
                        else -> "Offline/Fail"
                    }

                    Surface(
                        color = stateColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, stateColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(stateColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stateLabel,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = stateColor
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                // Ping Check Trigger Button
                Button(
                    onClick = { viewModel.testFirebase(context) },
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("ping_test_btn")
                ) {
                    Icon(Icons.Filled.Sync, contentDescription = "Ping Check", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Execute Reachability Ping Test", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Live Dual Sync Action Panels
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CloudSync,
                        contentDescription = "Cloud replica",
                        tint = SopanTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tables Replication Panel",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                // Inform local table counts
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Primary Local Tables Audit (Ready to Sync)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SopanTeal
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("• Attendance logs: ${attendanceList.size}", fontSize = 11.sp)
                                Text("• Tour plans: ${tourList.size}", fontSize = 11.sp)
                                Text("• Pharmacy Bookings: ${bookingsList.size}", fontSize = 11.sp)
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("• Competitor Audits: ${auditsList.size}", fontSize = 11.sp)
                                Text("• Safety reports: ${adrsList.size}", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Dual buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // PUSH UPLINK
                    Button(
                        onClick = { viewModel.pushToFirebase(context) },
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = SopanTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("push_firebase_btn")
                    ) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = "Push", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Push Up (Upload)", fontSize = 11.sp)
                    }

                    // PULL DOWNLINK
                    Button(
                        onClick = { viewModel.pullFromFirebase(context) },
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = SopanOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("pull_firebase_btn")
                    ) {
                        Icon(Icons.Filled.CloudDownload, contentDescription = "Pull", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pull down (Sync)", fontSize = 11.sp)
                    }
                }
            }
        }

        // Live Feed / Developers Terminal View
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Live Sync Protocol Console Feed",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Surface(
                color = Color(0xFF1E1E24),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.2.dp, Color(0xFF2D2D37)),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "sopan-sync-terminal:~$",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SopanEmerald,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Active Session",
                            color = Color.LightGray.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Base Server URL: $dbUrl\nStatus: $syncStatusMsg",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Color(0xFFA8B4CE),
                        fontSize = 10.5.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (testResult != null) {
                        Text(
                            text = "Ping Speed: ${testResult?.latencyMs}ms  | Ready: ${testResult?.isSuccess}",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = if (testResult?.isSuccess == true) SopanEmerald else MaterialTheme.colorScheme.error,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SUB-TAB 1: ORGANIZATIONAL SALES FORCE HIERARCHY
// ==========================================
@Composable
fun OrgHierarchySubTab(viewModel: SfaViewModel) {
    var selectedLevel by remember { mutableStateOf("MR") } // HO, ZSM, ASM, MR
    val employees by viewModel.filteredEmployees.collectAsState()
    val employeesMap by viewModel.employeesMap.collectAsState()
    val divisionsMap by viewModel.divisionsMap.collectAsState()
    val territoriesMap by viewModel.territoriesMap.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Dialog state
    var empName by remember { mutableStateOf("") }
    var empCode by remember { mutableStateOf("") }
    var empRole by remember { mutableStateOf("MR") }
    var empPhone by remember { mutableStateOf("") }
    var empEmail by remember { mutableStateOf("") }
    var empDivisionId by remember { mutableStateOf(1) }
    var empTerritoryId by remember { mutableStateOf(11) }
    var empReportsToId by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Strategy",
                        tint = SopanTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Nepal's pharma marketing operates on a synchronized structural chain under the Medical Representative (MR) up to Head Office. Tap any tier in the flowchart below to explore reporting roles and parameters.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Beautiful Interactive Interactive Flow chart Tree
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tier 1: HQ
                HierarchyNodeButton(
                    title = "Tier 1: HEAD OFFICE (Corporate HQ)",
                    subtitle = "CEO / Commercial Business Head",
                    icon = Icons.Filled.Domain,
                    isSelected = selectedLevel == "HO",
                    color = SopanTeal,
                    onClick = { selectedLevel = "HO" }
                )

                ConnectorLine()

                // Tier 2: ZSM
                HierarchyNodeButton(
                    title = "Tier 2: ZONAL SALES MANAGER (ZSM)",
                    subtitle = "Regional Strategy & Depot Coordinator",
                    icon = Icons.Filled.Map,
                    isSelected = selectedLevel == "ZSM",
                    color = SopanOrange,
                    onClick = { selectedLevel = "ZSM" }
                )

                ConnectorLine()

                // Tier 3: ASM
                HierarchyNodeButton(
                    title = "Tier 3: AREA SALES MANAGER (ASM)",
                    subtitle = "Territory Field Supervisor",
                    icon = Icons.Filled.DirectionsRun,
                    isSelected = selectedLevel == "ASM",
                    color = SopanEmerald,
                    onClick = { selectedLevel = "ASM" }
                )

                ConnectorLine()

                // Tier 4: MR
                HierarchyNodeButton(
                    title = "Tier 4: MEDICAL REPRESENTATIVE (MR)",
                    subtitle = "Dynamic Field Promoters & POB Bookers",
                    icon = Icons.Filled.PeopleAlt,
                    isSelected = selectedLevel == "MR",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { selectedLevel = "MR" }
                )
            }
        }

        // Details of selected node
        item {
            AnimatedContent(
                targetState = selectedLevel,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "LevelDetailsAnimation"
            ) { level ->
                val details = getHierarchyLevelDetails(level)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.2.dp, details.accentColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = details.icon,
                                contentDescription = null,
                                tint = details.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = details.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = details.accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Reporting Structure in Sopan SFA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Key Roles / Scope list
                        details.roles.forEachIndexed { idx, role ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = SopanEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = role,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Features tracked in the App
                        Text(
                            text = "App Features Required at this Level:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            details.features.forEach { feat ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = details.accentColor.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = details.accentColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = feat,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = details.accentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Collapsible Sopan MR & Hierarchy Setup Playbook
        item {
            var showPlaybook by remember { mutableStateOf(false) }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPlaybook = !showPlaybook },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Help,
                                contentDescription = "Help",
                                tint = SopanOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sopan SFA Setup Playbook",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Icon(
                            imageVector = if (showPlaybook) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = "Toggle",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (showPlaybook) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "A clear guide to setting up field operational nodes, assigning supervisors, and routing hierarchies in Nepal pharmaceutical channels.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Divider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                        Text("1. How to Setup and Map a new MR:", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = SopanTeal)
                        Text(
                            text = "To recruit and register a new Medical Representative (MR), click on '+ Add Staff' button below. Fill in their name, choose the role 'MR', assign their Nepal medical territory/division, and most importantly set their Reports-To ID to correspond to their Area Sales Manager (ASM) to bind their geo-fenced DCR reports to that local supervisor.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text("2. Who is their Area Sales Manager (ASM)?", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = SopanTeal)
                        Text(
                            text = "The Area Sales Manager is the direct field supervisor for a cluster of 5-10 local MRs in specific Nepalese district territories (e.g. Lalitpur, Lalitpur Area). The ASM performs weekly tour audits, approves local expense claims (TADA), and joins MRs on strategic customer calls to hospitals and Key Opinion Leader (KOL) doctors.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text("3. Who is the Zonal Sales Manager (ZSM)?", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = SopanOrange)
                        Text(
                            text = "The Zonal Sales Manager supervises multiple ASMs across Nepalese regional divisions (such as Bagmati Zone, Koshi Zone). They operate strategic inventory depots (e.g. Eastern Depot/Stockist), coordinate stock clearances, and track zonal sales performance metrics.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text("4. What is the role of Head Office (Corporate HQ)?", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = SopanEmerald)
                        Text(
                            text = "The Head Office forms Tier 1. Managed by the CEO and subsidiaries, HQ performs master configurations: registering newly released GMO-approved pharmaceutical molecules, establishing country-wide pricing/margins (MRP, PTR, PTS), setting valid promotional product schemes, and conducting safety compliance logs review.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Live Employee Directory Headers & Operations
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Field Force Registry (${employees.size})",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            viewModel.addEmployee(EmployeeEntity(name = "Kishor G.C. (CSV)", code = "MR99", role = "MR", divisionId = 1, territoryId = 12, phone = "9856012222", email = "kishor@sopanpharma.com", joiningDate = "2024-06-01"))
                            viewModel.addEmployee(EmployeeEntity(name = "Sujal Adhikari (CSV)", code = "ASM99", role = "ASM", divisionId = 2, territoryId = 13, phone = "9841223344", email = "sujal@sopanpharma.com", joiningDate = "2024-01-15"))
                            snackbarMessage = "Successfully imported selected MRs and ASMs!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SopanOrange),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Bulk CSV", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SopanTeal),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Add Staff", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (snackbarMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SopanEmerald.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, SopanEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = SopanEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(snackbarMessage!!, fontSize = 11.sp, color = SopanEmerald, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            tint = SopanEmerald,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { snackbarMessage = null }
                        )
                    }
                }
            }
        }

        if (employees.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No staff registered. Seeding automatically...", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            items(employees) { emp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = when (emp.role) {
                                                "Chairman", "CEO" -> SopanTeal.copy(alpha = 0.15f)
                                                "ZSM" -> SopanOrange.copy(alpha = 0.15f)
                                                "ASM" -> SopanEmerald.copy(alpha = 0.15f)
                                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emp.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = when (emp.role) {
                                            "Chairman", "CEO" -> SopanTeal
                                            "ZSM" -> SopanOrange
                                            "ASM" -> SopanEmerald
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = emp.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${emp.code} | Reports to: ${employeesMap[emp.reportsToId]?.name ?: "Board"}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Surface(
                                color = when (emp.role) {
                                    "Chairman", "CEO" -> SopanTeal.copy(alpha = 0.1f)
                                    "ZSM" -> SopanOrange.copy(alpha = 0.1f)
                                    "ASM" -> SopanEmerald.copy(alpha = 0.1f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = emp.role.uppercase(),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when (emp.role) {
                                        "Chairman", "CEO" -> SopanTeal
                                        "ZSM" -> SopanOrange
                                        "ASM" -> SopanEmerald
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Division: ${divisionsMap[emp.divisionId]?.name ?: "Rx (Prescription)"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                Text(text = "Territory: ${territoriesMap[emp.territoryId]?.name ?: "HQ Depot"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { /* Simulated call */ },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Phone, contentDescription = "Call", tint = SopanTeal, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.deleteEmployee(emp.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Register Field Employee", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SopanTeal)

                    OutlinedTextField(value = empName, onValueChange = { empName = it }, label = { Text("Employee Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = empCode, onValueChange = { empCode = it }, label = { Text("Code (e.g. MR66)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = empPhone, onValueChange = { empPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = empEmail, onValueChange = { empEmail = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())

                    Text("Reports to", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            val selectedEmployee = employees.find { it.id == empReportsToId }
                            Text(selectedEmployee?.name ?: "Select Supervisor")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None (Board)") },
                                onClick = { empReportsToId = 0; expanded = false }
                            )
                            employees.forEach { emp ->
                                val canReportTo = when(empRole) {
                                    "MR" -> emp.role == "ASM" 
                                    "ASM" -> emp.role == "ZSM" 
                                    "ZSM" -> emp.role == "Division Head" || emp.role == "Marketing Head"
                                    "Division Head" -> emp.role == "Marketing Director"
                                    "Marketing Director" -> emp.role == "CEO"
                                    "CEO" -> emp.role == "Chairman"
                                    else -> true 
                                }
                                if (canReportTo) {
                                    DropdownMenuItem(
                                        text = { Text("${emp.name} (${emp.role})") },
                                        onClick = { empReportsToId = emp.id; expanded = false }
                                    )
                                }
                            }
                        }
                    }

                    Text("Role Option", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("MR", "ASM", "ZSM").forEach { role ->
                            val isSel = empRole == role
                            Surface(
                                color = if (isSel) SopanTeal.copy(alpha = 0.15f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSel) SopanTeal else Color.LightGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { empRole = role }
                            ) {
                                Text(role, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) SopanTeal else Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (empName.isNotBlank() && empCode.isNotBlank()) {
                                    viewModel.addEmployee(
                                        EmployeeEntity(
                                            name = empName,
                                            code = empCode,
                                            role = empRole,
                                            divisionId = empDivisionId,
                                            territoryId = empTerritoryId,
                                            reportsToId = empReportsToId,
                                            joiningDate = "2026-05-29",
                                            phone = empPhone,
                                            email = empEmail
                                        )
                                    )
                                    showAddDialog = false
                                    empName = ""
                                    empCode = ""
                                    empPhone = ""
                                    empEmail = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SopanTeal)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HierarchyNodeButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Icon(
                imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ConnectorLine() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(16.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
    )
}

data class LevelDetails(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accentColor: Color,
    val roles: List<String>,
    val features: List<String>
)

fun getHierarchyLevelDetails(level: String): LevelDetails {
    return when (level) {
        "HO" -> LevelDetails(
            title = "Corporate Headquarters (HQ)",
            icon = Icons.Filled.Domain,
            accentColor = SopanTeal,
            roles = listOf(
                "Supervises National Sales Targets & Divides Sales Portfolios (e.g. Cardio/Diabetic div, Critical Care, Obs/Gyn).",
                "Determines corporate schemes, seasonal free benefits (e.g. 10+2, 10+1 free schemes).",
                "Monitors WHO-GMP compliance, pharmacovigilance reports, and registers new molecule visual aids."
            ),
            features = listOf("HQ Analytical Deck", "National Order Ledger", "Molecule Admin")
        )
        "ZSM" -> LevelDetails(
            title = "Zonal Sales Manager (ZSM)",
            icon = Icons.Filled.Map,
            accentColor = SopanOrange,
            roles = listOf(
                "Oversees entire key strategic logistics channels in Nepal (e.g., Eastern depot in Biratnagar, Central valley depot, Western plain depot in Nepalgunj).",
                "Approves/Coordinates bulk stock clearances from Depot to stockists.",
                "Mentors Area Sales Managers (ASM) & executes strategic visual aids detailing launch events."
            ),
            features = listOf("Zonal Target Tracking", "Depot stock clearance", "ASM Tour audits")
        )
        "ASM" -> LevelDetails(
            title = "Area Sales Manager (ASM)",
            icon = Icons.Filled.DirectionsRun,
            accentColor = SopanEmerald,
            roles = listOf(
                "Secures and guides District-level SFA operations (Kathmandu valley, Pokhara hub, Chitwan corridor, Birganj zone).",
                "Audits Medical Representatives' (MR) Tour Plans (TP) weekly or monthly.",
                "Executes key joint-working visits with MRs to top hospital KOLs (Key Opinion Leaders) and approves expense claims / TADA."
            ),
            features = listOf("Joint Vis DCR", "TP Approv Suite", "TADA claims check")
        )
        else -> LevelDetails(
            title = "Medical Representative (MR)",
            icon = Icons.Filled.PeopleAlt,
            accentColor = SopanTeal,
            roles = listOf(
                "Primary ground-execution engine conducting Doctors visual-detailing calls in Kathmandu, Lalitpur, etc.",
                "Collects Daily Call Reports (DCR) via geo-fenced check-ins at targeted clinics or hospitals.",
                "Books Personal Order Bookings (POBs) directly from local pharmacies (retailers) routed to authorized stockists."
            ),
            features = listOf("Geo-DCR Punch-in", "E-Detailing Aid", "POB Order booking")
        )
    }
}

// ==========================================
// SUB-TAB 2: LOGISTICS AND COMMERCIAL DISTRIBUTION CHAIN
// ==========================================
@Composable
fun CommChainSubTab(viewModel: SfaViewModel) {
    var selectedChainStep by remember { mutableStateOf("SS") } // SS, STOCKIST, PHARMACY
    val bookings by viewModel.allOrderBookings.collectAsState()

    var showOrderDialog by remember { mutableStateOf(false) }
    var chemistName by remember { mutableStateOf("") }
    var stockistName by remember { mutableStateOf("") }
    var bookedAmt by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SopanEmerald.copy(alpha = 0.06f)),
                border = BorderStroke(1.dp, SopanEmerald.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = "Logistics",
                        tint = SopanEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Commercial billing, stock dispatch, and prescription audits flow in a 3-tier distribution route in Nepal. Tap any commercial point below to inspect operational mechanisms.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Horizontal billing route flow representation
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Sopan SFA Digital Routing & Dispatch Pipeline",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Node A: Super Stockist Depot
                        ChainStepNode(
                            shortTitle = "DEPOT / SS",
                            title = "Super Stockist",
                            isSelected = selectedChainStep == "SS",
                            color = SopanTeal,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedChainStep = "SS" }
                        )

                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Flow",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp)
                        )

                        // Node B: Regional Stockist
                        ChainStepNode(
                            shortTitle = "STOCKIST",
                            title = "Wholesaler",
                            isSelected = selectedChainStep == "STOCKIST",
                            color = SopanOrange,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedChainStep = "STOCKIST" }
                        )

                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Flow",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp)
                        )

                        // Node C: Retail Pharmacy / Hospital
                        ChainStepNode(
                            shortTitle = "CHEMIST / RX",
                            title = "Clinic/Pharmacy",
                            isSelected = selectedChainStep == "PHARMACY",
                            color = SopanEmerald,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedChainStep = "PHARMACY" }
                        )
                    }
                }
            }
        }

        // Interactive Information Display Accordion
        item {
            AnimatedContent(
                targetState = selectedChainStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ChainAnimation"
            ) { step ->
                val cardDetails = getChainStepDetails(step)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.2.dp, cardDetails.indicatorColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = cardDetails.icon,
                                contentDescription = null,
                                tint = cardDetails.indicatorColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = cardDetails.stepFullTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = cardDetails.indicatorColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Commercial Logic & App Synchronization:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        cardDetails.points.forEach { pt ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowCircleRight,
                                    contentDescription = null,
                                    tint = cardDetails.indicatorColor,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pt,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = cardDetails.indicatorColor.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = cardDetails.indicatorColor.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Sync,
                                        contentDescription = null,
                                        tint = cardDetails.indicatorColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cardDetails.appSyncTitle,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = cardDetails.indicatorColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cardDetails.appSyncDesc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Order Bookings Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personal Order Bookings (POBs: ${bookings.size})",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )

                Button(
                    onClick = { showOrderDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SopanEmerald),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("+ Record Order", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (bookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No POB bookings found in secondary database.", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            items(bookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = booking.chemistName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Brand Product: ${booking.productName} (Qty: ${booking.quantity})",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            Text(
                                text = "NPR ${booking.totalAmount}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = SopanEmerald
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Scheme: ${booking.schemeText}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    color = SopanEmerald.copy(alpha = 0.10f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "LOCAL PIPELINE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SopanEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteOrderBooking(booking.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showOrderDialog) {
        Dialog(onDismissRequest = { showOrderDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Record Pharmacy POB Order", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SopanEmerald)

                    OutlinedTextField(value = chemistName, onValueChange = { chemistName = it }, label = { Text("Retail Chemist Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = stockistName, onValueChange = { stockistName = it }, label = { Text("Brand / Product Ordered") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bookedAmt, onValueChange = { bookedAmt = it }, label = { Text("Booking Value (NPR)") }, modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showOrderDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (chemistName.isNotBlank() && stockistName.isNotBlank()) {
                                    val amt = bookedAmt.toDoubleOrNull() ?: 15000.0
                                    viewModel.addOrderBooking(
                                        chemistName = chemistName,
                                        productName = stockistName,
                                        quantity = 24,
                                        totalAmount = amt,
                                        schemeText = "10+1 Scheme Offer"
                                    )
                                    showOrderDialog = false
                                    chemistName = ""
                                    stockistName = ""
                                    bookedAmt = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SopanEmerald)
                        ) {
                            Text("Record")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChainStepNode(
    shortTitle: String,
    title: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        ),
        modifier = modifier.height(58.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = shortTitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class ChainStepDetails(
    val stepFullTitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val indicatorColor: Color,
    val points: List<String>,
    val appSyncTitle: String,
    val appSyncDesc: String
)

fun getChainStepDetails(step: String): ChainStepDetails {
    return when (step) {
        "SS" -> ChainStepDetails(
            stepFullTitle = "Sopan Depot / Super Stockist",
            icon = Icons.Filled.Store,
            indicatorColor = SopanTeal,
            points = listOf(
                "Primary billing center where physical medicine inventory from Sopan manufacturing plant is dispatched and recorded.",
                "Coordinates primary stock dispatch to authorized distributors and wholesale stockists across regional territories.",
                "Oversees bulk stock inventory levels and processes commercial billing orders."
            ),
            appSyncTitle = "Primary Orders Validation",
            appSyncDesc = "In Sopan SFA, depot supervisors and ASMs track high-level billing logs, verifying replenishment demands to prevent district-level stockouts."
        )
        "STOCKIST" -> ChainStepDetails(
            stepFullTitle = "Regional Wholesaler / Stockist",
            icon = Icons.Filled.Business,
            indicatorColor = SopanOrange,
            points = listOf(
                "Authorized wholesale distributors who service individual retail chemists, local clinics, and private medical stores.",
                "Maintains credit terms (typically 21 to 45-day credit circles) with pharmacies.",
                "Receives POB (Personal Order Bookings) captured in real-time by Sopan field representatives on their cellphones."
            ),
            appSyncTitle = "Secondary POB Routing & Credit Checks",
            appSyncDesc = "MR order bookings are instantly routed to selected stockists via SFA interface. The wholesaler automatically receives an SMS notification and confirmation ledger."
        )
        else -> ChainStepDetails(
            stepFullTitle = "Pharmacy / Clinical/Hospital Chemist Shop",
            icon = Icons.Filled.LocalPharmacy,
            indicatorColor = SopanEmerald,
            points = listOf(
                "Final dispensing counter representing the interface with Nepal's patients and doctors.",
                "Subject to strategic prescription audits, where the Sopan MR records doctor brand loyalty.",
                "Tracks Retail Chemist Product Availability (RCPA) to audit opponent brand stock vs. Sopan's products (e.g. Myzith, Sopan-D)."
            ),
            appSyncTitle = "RCPA Audits & Pharmacovigilance (ADR)",
            appSyncDesc = "MRs log competitor audits and document Adverse Drug Reactions (PV ADR) directly on-site at hospital pharmacies to ensure instant WHO safety reporting."
        )
    }
}

// ==========================================
// SUB-TAB 3: COMPARATIVE AUDIT DECK: NEPAL VS INDIAN SFA APPS
// ==========================================
@Composable
fun AuditDeckSubTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-level conceptual audit summary header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SopanOrange.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, SopanOrange.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sopan SFA Symmetrical Audit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = SopanOrange
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Analyzing Sopan SFA against leading Indian pharmaceutical SFA engines (Marg SFA, PepUpSales) reveals critical architecture requirements tailored specifically for Nepal's geographical plain & hill terrains.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Audited Comparison Cards
        item {
            ComparisonAuditCard(
                feature = "1. Territory Distribution Channels",
                nepalSfa = "Depot/Super Stockist -> Stockist Wholesaler -> Retail Pharmacy / Hospital counter. Nepal's system relies heavily on distinct regional territory hubs.",
                indianSfa = "Primary C&F (Clearing & Forwarding Agents) -> Super Stockist -> Stockist -> Retail Chemist shop. Broad multifactor channel layers.",
                sopanAdvantage = "Sopan SFA directly integrates a streamlined 3-tier mapping, eliminating redundant layers and making secondary booking routes 40% faster.",
                color = SopanTeal
            )
        }

        item {
            ComparisonAuditCard(
                feature = "2. Field Geo-fencing & DCR Validation",
                nepalSfa = "Location-restricted punch-ins linked to specific target territory lists (e.g., Subidhanagar Clinic). Adapts to dense clinical clusters in the Kathmandu Valley.",
                indianSfa = "Strict GPS tracking with radius lockups (usually 50m to doctor champer). If the representative is out of bounds, check-in is strictly blocked.",
                sopanAdvantage = "Allows offline backup logs with ASM verify overrides, catering to remote Nepal territory signal dropouts.",
                color = SopanEmerald
            )
        }

        item {
            ComparisonAuditCard(
                feature = "3. SFA Joint Visit (Double Call Logs)",
                nepalSfa = "Joint travel loggings (MR working with ASM/RSM) directly registers dual feedback reports and splits travel allowances automatically.",
                indianSfa = "Joint tour logs requiring manager approval within 48 hours for MR compliance points confirmation.",
                sopanAdvantage = "Integrated ASM approvals in a central SFA control board allows managers to approve travel plans in real-time.",
                color = SopanOrange
            )
        }

        item {
            ComparisonAuditCard(
                feature = "4. Territory Transport Allowance & Expense (TADA)",
                nepalSfa = "Supports specific multi-terrain per-km rates tailored for Hill terrain routes (NPR. 22/km) vs Flat Terai plain routes (NPR. 14/km).",
                indianSfa = "Standard fuel-rate formulas based on state boundaries or generic ASM/MR allowance brackets.",
                sopanAdvantage = "Dynamic terrain-based expense calculator matches actual Nepal geography costs to maintain highly transparent and fair MR reimbursement.",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ComparisonAuditCard(
    feature: String,
    nepalSfa: String,
    indianSfa: String,
    sopanAdvantage: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = feature,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Grid or table-like comparison
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = color.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "NEPAL SYSTEM PLAN",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nepalSfa,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 15.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "INDIAN SFA AUDIT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = indianSfa,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = SopanEmerald.copy(alpha = 0.06f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SopanEmerald.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Sopan edge",
                        tint = SopanEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Sopan Architectural Advantage :",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SopanEmerald
                        )
                        Text(
                            text = sopanAdvantage,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SUB-TAB 3: PRODUCT BACKEND MASTER & SCHEMES
// ==========================================
@Composable
fun ProductsSubTab(viewModel: SfaViewModel) {
    val products by viewModel.allProducts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog state
    var pBrand by remember { mutableStateOf("") }
    var pForm by remember { mutableStateOf("Tablets") }
    var pPackSize by remember { mutableStateOf("10x10 ALUALU") }
    var pDdaSchedule by remember { mutableStateOf("Schedule G") }
    var pTripsStatus by remember { mutableStateOf("Generics") }
    var pLaunchedOn by remember { mutableStateOf("2026-05-29") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SopanOrange.copy(alpha = 0.06f)),
                border = BorderStroke(1.dp, SopanOrange.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Store,
                        contentDescription = "Products",
                        tint = SopanOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Product Backend Master regulates official Nepalese DDA schedule category, formulation dosage form, HSN codes, and patent vs. generic status.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Action header Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Operational Brands Directory (${products.size})",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            viewModel.addProduct(
                                ProductEntity(
                                    brand = "Sopan-D",
                                    form = "Capsules",
                                    packSize = "10x10 ALUALU",
                                    hsnCode = "3004.90",
                                    ddaSchedule = "Schedule G",
                                    tripsStatus = "Generics",
                                    launchedOn = "2024-06-01"
                                )
                            )
                            viewModel.addProduct(
                                ProductEntity(
                                    brand = "Myzith 500",
                                    form = "Tablets",
                                    packSize = "3 Tablets Strip",
                                    hsnCode = "3004.50",
                                    ddaSchedule = "Schedule A",
                                    tripsStatus = "Patent",
                                    launchedOn = "2024-01-15"
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SopanTeal),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Seed Demo", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SopanOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+ New Brand", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (products.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No drugs found in products master. Tap 'Seed Demo' or add manual brand.", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            items(products) { prod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod.brand,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Formulation: ${prod.form} | Pack: ${prod.packSize}",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = SopanTeal.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = prod.tripsStatus,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SopanTeal,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = SopanOrange.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "DDA: ${prod.ddaSchedule}",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SopanOrange,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "HSN: ${prod.hsnCode}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "Launched: ${prod.launchedOn}",
                                    fontSize = 9.sp,
                                    color = SopanEmerald,
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(
                                    onClick = { viewModel.deleteProduct(prod.id) },
                                    modifier = Modifier.size(28.dp).padding(top = 4.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Register Brand details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SopanOrange)

                    OutlinedTextField(value = pBrand, onValueChange = { pBrand = it }, label = { Text("Brand Name (e.g. Myzith)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pForm, onValueChange = { pForm = it }, label = { Text("Formulation (Tablets / Capsules / Liquid)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pPackSize, onValueChange = { pPackSize = it }, label = { Text("Pack Size (e.g. 10x10 AluAlu)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pDdaSchedule, onValueChange = { pDdaSchedule = it }, label = { Text("DDA Schedule (e.g. Schedule G)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pTripsStatus, onValueChange = { pTripsStatus = it }, label = { Text("Trips Status (Patent / Generics)") }, modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (pBrand.isNotBlank()) {
                                    viewModel.addProduct(
                                        ProductEntity(
                                            brand = pBrand,
                                            form = pForm,
                                            packSize = pPackSize,
                                            hsnCode = "3004.90",
                                            ddaSchedule = pDdaSchedule,
                                            tripsStatus = pTripsStatus,
                                            launchedOn = pLaunchedOn
                                        )
                                    )
                                    showAddDialog = false
                                    pBrand = ""
                                    pForm = "Tablets"
                                    pPackSize = "10x10 ALUALU"
                                    pDdaSchedule = "Schedule G"
                                    pTripsStatus = "Generics"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SopanOrange)
                        ) {
                            Text("Add brand")
                        }
                    }
                }
            }
        }
    }
}
