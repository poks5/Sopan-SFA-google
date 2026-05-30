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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.SopanEmerald
import com.example.ui.theme.SopanOrange
import com.example.ui.theme.SopanTeal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: SfaViewModel,
    modifier: Modifier = Modifier
) {
    val userRole by viewModel.userRole.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    // Screen tab controllers for MR
    var currentMrTab by remember { mutableStateOf("ATTENDANCE") }

    Scaffold(
        topBar = {
            SfaDashboardHeader(
                userName = loggedInUser,
                role = userRole,
                onLogout = { viewModel.logout() }
            )
        },
        bottomBar = {
            if (userRole == "MR" || userRole == "ASM") {
                MRBottomNavigationBar(
                    selectedTab = currentMrTab,
                    onTabSelected = { currentMrTab = it }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (userRole) {
                "MR", "ASM" -> {
                    when (currentMrTab) {
                        "ATTENDANCE" -> AttendanceTab(viewModel)
                        "PORTFOLIO" -> PortfolioTab(viewModel)
                        "BOOKING" -> BookingAndRcpaTab(viewModel)
                        "COMPLIANCE" -> ComplianceAndTadaTab(viewModel)
                        "ORGANIZATION" -> OrganizationHierarchyTab(viewModel)
                    }
                }
                "Manager" -> ManagerDashboard(viewModel)
                "Executive" -> ExecutiveDashboard(viewModel)
                "Admin" -> SystemAdminDashboard(viewModel)
            }
        }
    }
}

@Composable
fun SfaDashboardHeader(
    userName: String,
    role: String,
    onLogout: () -> Unit
) {
    // Current date format (Bikram Sambat dynamic simulation: Nepali Calendar is approximate +56.7 Years)
    val today = Calendar.getInstance()
    // Compact English format e.g. "Fri, 29 May 2026"
    val sdf = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
    val formattedDate = sdf.format(today.time)
    
    // Simulate BS year (approximate)
    val bsYear = today.get(Calendar.YEAR) + 57
    val bsMonth = when(today.get(Calendar.MONTH)) {
        Calendar.JANUARY -> "Poush"
        Calendar.FEBRUARY -> "Magh"
        Calendar.MARCH -> "Falgun"
        Calendar.APRIL -> "Chaitra"
        Calendar.MAY -> "Baishakh/Jestha"
        Calendar.JUNE -> "Jestha"
        Calendar.JULY -> "Ashadh"
        Calendar.AUGUST -> "Shrawan"
        Calendar.SEPTEMBER -> "Bhadra"
        Calendar.OCTOBER -> "Ashwin"
        Calendar.NOVEMBER -> "Kartik"
        Calendar.DECEMBER -> "Mangsir"
        else -> "Baishakh"
    }
    val bsDay = today.get(Calendar.DAY_OF_MONTH)
    val nepaliDateText = "$bsDay $bsMonth, $bsYear BS"

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_header")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .border(
                                width = 1.2.dp,
                                brush = Brush.linearGradient(listOf(SopanTeal, SopanEmerald)),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(2).uppercase(),
                            color = SopanTeal,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            // Connected status indicator pulse
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(SopanEmerald)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = SopanTeal,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "ROLE: $role",
                                fontSize = 10.sp,
                                color = SopanTeal,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                            
                            Text(
                                text = "•",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )

                            // Unified WHO-GMP tag in profile header rather than pushing the dates card
                            Surface(
                                color = SopanTeal.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "WHO-GMP",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SopanTeal,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Premium Date Info-Capsule Badge (Fully optimized, compact, single-row pill)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    width = 0.8.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Date",
                        tint = SopanOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = " • ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    Text(
                        text = nepaliDateText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SopanEmerald
                    )
                }
            }
        }
    }
}

@Composable
fun MRBottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("mr_bottom_bar")
    ) {
        NavigationBarItem(
            selected = selectedTab == "ATTENDANCE",
            onClick = { onTabSelected("ATTENDANCE") },
            icon = { Icon(Icons.Filled.DirectionsWalk, contentDescription = "DCR") },
            label = { Text("Field Ops", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("nav_attendance")
        )
        NavigationBarItem(
            selected = selectedTab == "PORTFOLIO",
            onClick = { onTabSelected("PORTFOLIO") },
            icon = { Icon(Icons.Filled.MenuBook, contentDescription = "E-Detailing") },
            label = { Text("Detailing", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("nav_portfolio")
        )
        NavigationBarItem(
            selected = selectedTab == "BOOKING",
            onClick = { onTabSelected("BOOKING") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "POB") },
            label = { Text("Booking", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("nav_booking")
        )
        NavigationBarItem(
            selected = selectedTab == "COMPLIANCE",
            onClick = { onTabSelected("COMPLIANCE") },
            icon = { Icon(Icons.Filled.VerifiedUser, contentDescription = "TADA") },
            label = { Text("Compliance", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("nav_compliance")
        )
        NavigationBarItem(
            selected = selectedTab == "ORGANIZATION",
            onClick = { onTabSelected("ORGANIZATION") },
            icon = { Icon(Icons.Filled.AccountTree, contentDescription = "Hierarchy") },
            label = { Text("Hierarchy", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("nav_hierarchy")
        )
    }
}

// ==========================================
// TAB 1: FIELD OPERATIONS & GEO-ATTENDANCE
// ==========================================
@Composable
fun AttendanceTab(viewModel: SfaViewModel) {
    val allAttendance by viewModel.allAttendance.collectAsState()
    val isPunchedIn by viewModel.isPunchedIn.collectAsState()
    val lastClinic by viewModel.lastClinic.collectAsState()

    var selectedClinic by remember { mutableStateOf("Subidhanagar Clinic, Kathmandu") }
    var locationNote by remember { mutableStateOf("Tinkune Territory") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val nepalClinics = listOf(
        "Subidhanagar Clinic, Kathmandu",
        "Tinkune Healthcare Hub, Kathmandu",
        "Koteshwor Polyclinic, Lalitpur",
        "Imadol Medical Center, Lalitpur",
        "Bakhundole Pediatric Clinic, Lalitpur",
        "Chabahil Medicare Research Center, Kathmandu",
        "Mediciti Hospital Hub, Bhaisepati"
    )

    var bsDateInput by remember { mutableStateOf("2083-02-15") }
    var tourClinic by remember { mutableStateOf("Tinkune Healthcare Hub, Kathmandu") }
    var tourObjectives by remember { mutableStateOf("Scientific detailing of Ulshield 40mg with Dr. Shrestha and promo booking") }
    val toursList by viewModel.allTourPlans.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Punches Log card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isPunchedIn) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f) 
                                     else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            Color.Transparent,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("punch_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PinDrop,
                                contentDescription = null,
                                tint = SopanTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Geo-Attendance Panel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = if (isPunchedIn) SopanEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                1.1.dp,
                                if (isPunchedIn) SopanEmerald.copy(alpha = 0.30f) else MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (isPunchedIn) SopanEmerald else MaterialTheme.colorScheme.error)
                                )
                                Text(
                                    text = if (isPunchedIn) "PUNCHED IN" else "PUNCHED OUT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isPunchedIn) SopanEmerald else MaterialTheme.colorScheme.error,
                                    letterSpacing = 0.4.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isPunchedIn) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Currently conducting field operations at:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocalHospital,
                                    contentDescription = null,
                                    tint = SopanTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lastClinic,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SopanTeal
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Select your target clinic to check-in for official Daily Call Report (DCR) reporting:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (!isPunchedIn) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedClinic,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Target Nepal Territory Clinic") },
                                leadingIcon = { Icon(Icons.Filled.LocalHospital, null, tint = SopanTeal) },
                                trailingIcon = {
                                    IconButton(onClick = { dropdownExpanded = true }) {
                                        Icon(Icons.Filled.ArrowDropDown, "Select Clinic", tint = SopanTeal)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().clickable { dropdownExpanded = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SopanTeal,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                nepalClinics.forEach { clinic ->
                                    DropdownMenuItem(
                                        text = { Text(clinic, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                        onClick = {
                                            selectedClinic = clinic
                                            dropdownExpanded = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Filled.LocalHospital, null, tint = SopanTeal, modifier = Modifier.size(16.dp))
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = locationNote,
                            onValueChange = { locationNote = it },
                            label = { Text("Territory / Location Notes") },
                            leadingIcon = { Icon(Icons.Filled.EditNote, contentDescription = null, tint = SopanTeal) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("location_note_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SopanTeal,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isPunchedIn) {
                                viewModel.punchOut(lastClinic, "Territory Out")
                            } else {
                                viewModel.punchIn(selectedClinic, locationNote)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent // backgradient handled
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .testTag("punch_toggle_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (isPunchedIn) {
                                        Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.error,
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                            )
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            colors = listOf(
                                                SopanTeal,
                                                SopanEmerald
                                            )
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isPunchedIn) Icons.Filled.CheckCircle else Icons.Filled.PinDrop,
                                    contentDescription = "Punch Action",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPunchedIn) "PUNCH OUT & SUBMIT DCR" else "PUNCH IN AT SELECTED CLINIC",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // B.S. Calendar Tour Planning Section
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            Color.Transparent,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Nepali Calendar Tour Planner (B.S.)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Schedule doctor visits according to Bikram Sambat dates integrated with Nepalese national holidays.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = bsDateInput,
                        onValueChange = { bsDateInput = it },
                        label = { Text("B.S. Date (e.g. 2083-02-15)") },
                        leadingIcon = { Icon(Icons.Filled.CalendarMonth, null, tint = SopanTeal) },
                        modifier = Modifier.fillMaxWidth().testTag("bs_date_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SopanTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tourClinic,
                        onValueChange = { tourClinic = it },
                        label = { Text("Target Medical Center / Doctor") },
                        leadingIcon = { Icon(Icons.Filled.LocalHospital, null, tint = SopanTeal) },
                        modifier = Modifier.fillMaxWidth().testTag("tour_clinic_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SopanTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tourObjectives,
                        onValueChange = { tourObjectives = it },
                        label = { Text("Strategic Detailing Objectives") },
                        leadingIcon = { Icon(Icons.Filled.EditNote, null, tint = SopanTeal) },
                        modifier = Modifier.fillMaxWidth().height(80.dp).testTag("tour_objectives_input"),
                        maxLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SopanTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (tourClinic.isNotBlank() && bsDateInput.isNotBlank()) {
                                viewModel.addTourPlan(bsDateInput, tourClinic, tourObjectives)
                                tourClinic = ""
                                tourObjectives = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .testTag("add_tour_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            SopanTeal,
                                            SopanEmerald
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Plan", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SAVE TOUR ACTION PLAN (B.S.)", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Historic List of tours & punches
        item {
            Text(
                text = "Planned Territory Tours List",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (toursList.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No planned tours saved in current cycle. Add your first visit above.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(toursList) { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = plan.bsDate,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = plan.doctorOrClinic,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row {
                                Surface(
                                    color = if (plan.status == "Approved") SopanEmerald.copy(alpha = 0.15f) 
                                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = plan.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (plan.status == "Approved") SopanEmerald else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { viewModel.deleteTourPlan(plan.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        if (plan.objectives.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Objective: ${plan.objectives}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Attendance history logs list
        item {
            Text(
                text = "My Geo-Attendance & Check-in History Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (allAttendance.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No clinical punch-in logs in current session.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(allAttendance) { log ->
                val timeString = SimpleDateFormat("HH:mm:ss a (\'GMT\'Z)", Locale.US).format(Date(log.timestamp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (log.action == "PUNCH IN") SopanEmerald.copy(alpha = 0.15f) 
                                    else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (log.action == "PUNCH IN") Icons.Filled.PinDrop else Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (log.action == "PUNCH IN") SopanEmerald else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = log.action,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (log.action == "PUNCH IN") SopanEmerald else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = timeString,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            Text(
                                text = log.clinic,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "GPS: Lat ${log.latitude}, Lng ${log.longitude} | Area: ${log.location}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: SCIENTIFIC DETAILING & PORTFOLIO
// ==========================================
@Composable
fun PortfolioTab(viewModel: SfaViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedProductForDetailing by remember { mutableStateOf<ProductFormulation?>(null) }
    
    val products = ProductPortfolio.products
    val categories = listOf("All") + products.map { it.category }.distinct()

    val filteredProducts = products.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (it.brandName.contains(searchQuery, ignoreCase = true) || 
         it.genericName.contains(searchQuery, ignoreCase = true))
    }

    if (selectedProductForDetailing != null) {
        // Detailed E-Detailing Interactive Aids view
        EDetailingVisualAidView(
            product = selectedProductForDetailing!!,
            onBack = { selectedProductForDetailing = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Scientific E-Detailing Aids",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Interactive detailing slides and side-by-side comparative efficacy studies to pitch brand strength to clinicians.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Formulation / Molecule") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth().testTag("portfolio_search_bar"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category filter chips row
            ScrollableChipsRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1.0f)
            ) {
                items(filteredProducts) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Open scientific interactive detailing!
                                selectedProductForDetailing = item
                            }
                            .testTag("product_item_${item.brandName}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.brandName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Surface(
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.category,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Composition: ${item.genericName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            if (item.isKeyMolecule) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = SopanEmerald.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.FeaturedPlayList,
                                        contentDescription = "Detailing Available",
                                        tint = SopanEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tap to Launch Interactive E-Detailing Slide",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SopanEmerald
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableChipsRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(cat) },
                label = { Text(cat, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("category_chip_$cat")
            )
        }
    }
}

@Composable
fun EDetailingVisualAidView(
    product: ProductFormulation,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }.padding(bottom = 12.dp)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back to Portfolio List", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Clinical Data Header
                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "VISUAL AID: CLINICAL DATA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = product.brandName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = product.genericName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                // 2. Efficacy Details
                Column {
                    Text(
                        text = "1. Efficacy details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (product.isKeyMolecule) product.efficacyDetails 
                               else "Sopan formulations are certified under WHO-GMP standards with rigorous purity indexing. Manufactured with world-class cGMP machinery in Lalitpur.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 18.sp
                    )
                }

                // 3. Side-By-Side Clinical Comparison
                Column {
                    Text(
                        text = "2. Side-By-Side Clinical Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Clinical recovery margin vs generic competitor Molecule X:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        // Sopan Brand Bar (92%)
                        Text(
                            text = "${product.brandName} (Sopan)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SopanEmerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(0.92f)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Brush.linearGradient(listOf(SopanEmerald, SopanTeal)))
                            )
                            Spacer(modifier = Modifier.weight(0.08f))
                            Text(
                                  text = "92%",
                                  fontSize = 12.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = SopanEmerald,
                                  modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Competitor Bar (71%)
                        Text(
                            text = "Competitor Generic X",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(0.71f)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            )
                            Spacer(modifier = Modifier.weight(0.29f))
                            Text(
                                text = "71%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }

                // 4. Compliance Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SopanEmerald.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .background(SopanEmerald.copy(alpha = 0.04f))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Verified, "Cert", tint = SopanEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Therapeutic Compliance",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SopanEmerald
                        )
                    }

                    Text(
                        text = "Produced in cGMP state-of-the-art facility utilizing double compression layout techniques, lowering dissolution periods for faster bioavailability.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}


// ==========================================
// TAB 3: PERSONAL ORDER BOOKING & RCPA AUDIT
// ==========================================
@Composable
fun BookingAndRcpaTab(viewModel: SfaViewModel) {
    val orderBookings by viewModel.allOrderBookings.collectAsState()
    val competitorAudits by viewModel.allCompetitorAudits.collectAsState()

    var activeSubTab by remember { mutableStateOf("ORDER") } // ORDER, RCPA, or DCR

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { activeSubTab = "ORDER" },
                modifier = Modifier.weight(1.0f),
                contentPadding = PaddingValues(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == "ORDER") MaterialTheme.colorScheme.primary 
                                     else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("POB Order", fontSize = 11.sp, color = if (activeSubTab == "ORDER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = { activeSubTab = "RCPA" },
                modifier = Modifier.weight(1.0f),
                contentPadding = PaddingValues(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == "RCPA") MaterialTheme.colorScheme.primary 
                                     else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("RCPA Audit", fontSize = 11.sp, color = if (activeSubTab == "RCPA") Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = { activeSubTab = "DCR" },
                modifier = Modifier.weight(1.0f),
                contentPadding = PaddingValues(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == "DCR") MaterialTheme.colorScheme.primary 
                                     else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Doctor DCR", fontSize = 11.sp, color = if (activeSubTab == "DCR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        when (activeSubTab) {
            "ORDER" -> OrderBookingSection(viewModel, orderBookings)
            "RCPA" -> CompetitorAuditSection(viewModel, competitorAudits)
            "DCR" -> DcrReportingSection(viewModel)
        }
    }
}

@Composable
fun OrderBookingSection(viewModel: SfaViewModel, orderBookings: List<OrderBookingEntity>) {
    var chemistName by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf(ProductPortfolio.products.first()) }
    var quantityInput by remember { mutableStateOf("50") }
    var chemistSchemeResult by remember { mutableStateOf("No active scheme") }

    // Search and selector state
    var showProductSelector by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val schemesMap = mapOf(
        "Vitex (C/M/L)" to "10+1 Scheme (10 boxes + 1 free)",
        "Ulshield 20/40" to "20+3 Scheme",
        "Lactusol" to "15+2 Scheme",
        "Pbin (25/50/75/150)" to "10+1 Scheme"
    )

    // Calculate simulated price per formulation box
    val calculatedPrice = (quantityInput.toIntOrNull() ?: 0) * 120.0

    LaunchedEffect(selectedProduct) {
        chemistSchemeResult = schemesMap[selectedProduct.brandName] ?: "No active seasonal benefits"
    }

    // Filtered products list
    val filteredProducts = remember(searchQuery) {
        ProductPortfolio.products.filter {
            it.brandName.contains(searchQuery, ignoreCase = true) ||
            it.genericName.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    // Beautiful Searchable Product Selector Dialog
    if (showProductSelector) {
        AlertDialog(
            onDismissRequest = { 
                showProductSelector = false 
                searchQuery = ""
            },
            title = {
                Text(
                    text = "Search Sopan Products",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search brand, generic, or therapy...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search icon") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_search_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (filteredProducts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "No results icon",
                                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No matching products found",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(filteredProducts) { prod ->
                                Surface(
                                    color = if (prod.brandName == selectedProduct.brandName) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedProduct = prod
                                            showProductSelector = false
                                            searchQuery = ""
                                        }
                                        .testTag("product_select_item_${prod.brandName.replace(" ", "_")}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = prod.brandName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = if (prod.brandName == selectedProduct.brandName) {
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                                if (prod.isKeyMolecule) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        color = SopanOrange.copy(alpha = 0.15f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "KEY",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SopanOrange,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = prod.genericName,
                                                fontSize = 11.sp,
                                                color = if (prod.brandName == selectedProduct.brandName) {
                                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                                } else {
                                                    MaterialTheme.colorScheme.outline
                                                }
                                            )
                                        }
                                        
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = prod.category,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { 
                        showProductSelector = false 
                        searchQuery = ""
                    }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "New chemist order booking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = chemistName,
                        onValueChange = { chemistName = it },
                        label = { Text("Nepal Chemist / Pharmacy Name") },
                        modifier = Modifier.fillMaxWidth().testTag("chemist_name_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Sopan Product Formulation:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Simple row containing selected product name with a clickable dropdown trigger
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().clickable {
                            showProductSelector = true
                        }.testTag("product_selector_trigger")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(selectedProduct.brandName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(selectedProduct.genericName, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Search", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Icon(Icons.Filled.ArrowDropDown, "Dropdown", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it },
                        label = { Text("Order Quantity (Boxes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("order_qty_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Scheme visual output
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row {
                                Icon(Icons.Filled.CardGiftcard, null, tint = SopanOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Active Nepalese Schema Benefits:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SopanOrange)
                            }
                            Text(chemistSchemeResult, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Estimated Total Price: NPR. $calculatedPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (chemistName.isNotBlank() && quantityInput.toIntOrNull() != null) {
                                viewModel.addOrderBooking(
                                    chemistName = chemistName,
                                    productName = selectedProduct.brandName,
                                    quantity = quantityInput.toInt(),
                                    totalAmount = calculatedPrice,
                                    schemeText = chemistSchemeResult
                                )
                                chemistName = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_order_button")
                    ) {
                        Icon(Icons.Filled.Save, "Save order")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CONFIRM ORDER BOOKING (POB)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Booking list
        item {
            Text(
                text = "Recently Saved Chemists Bookings (POB)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (orderBookings.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No booking reports recorded today.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(orderBookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(booking.chemistName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("NPR. ${booking.totalAmount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Product: ${booking.productName} (${booking.quantity} Boxes)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Text("Scheme: ${booking.schemeText}", fontSize = 11.sp, color = SopanOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompetitorAuditSection(viewModel: SfaViewModel, audits: List<CompetitorAuditEntity>) {
    var auditChemist by remember { mutableStateOf("") }
    var myzithQtyInput by remember { mutableStateOf("120") }
    var compAQtyInput by remember { mutableStateOf("80") }
    var compBQtyInput by remember { mutableStateOf("45") }

    val myzithTotal = myzithQtyInput.toIntOrNull() ?: 0
    val compATotal = compAQtyInput.toIntOrNull() ?: 0
    val compBTotal = compBQtyInput.toIntOrNull() ?: 0
    val totalAzith = myzithTotal + compATotal + compBTotal
    val myzithSharePercent = if (totalAzith > 0) (myzithTotal.toFloat() / totalAzith * 100).toInt() else 0

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Competitor RCPA Retail Audit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Conduct competitor audits for azithromycin sales share (Myzith by Sopan vs other brands).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = auditChemist,
                        onValueChange = { auditChemist = it },
                        label = { Text("Pharmacy Chemist Name") },
                        modifier = Modifier.fillMaxWidth().testTag("rcpa_chemist_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Monthly Chemist Sales (Units):", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = myzithQtyInput,
                        onValueChange = { myzithQtyInput = it },
                        label = { Text("Myzith (Sopan)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = compAQtyInput,
                        onValueChange = { compAQtyInput = it },
                        label = { Text("Azithral (Competitor A)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = compBQtyInput,
                        onValueChange = { compBQtyInput = it },
                        label = { Text("Zithrox (Competitor B)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Share preview
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Myzith Market Share:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("$myzithSharePercent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SopanEmerald)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .weight(if (myzithSharePercent > 0) myzithSharePercent.toFloat() / 100 else 0.01f)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SopanEmerald)
                                )
                                if (myzithSharePercent < 100) {
                                    Box(
                                        modifier = Modifier
                                            .weight((100 - myzithSharePercent).toFloat() / 100)
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (auditChemist.isNotBlank()) {
                                viewModel.addCompetitorAudit(
                                    chemistName = auditChemist,
                                    myzithQty = myzithQtyInput.toIntOrNull() ?: 0,
                                    compAQty = compAQtyInput.toIntOrNull() ?: 0,
                                    compBQty = compBQtyInput.toIntOrNull() ?: 0
                                )
                                auditChemist = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_rcpa_button")
                    ) {
                        Icon(Icons.Filled.BarChart, "Audit")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SUBMIT RCPA MARKET SHARE AUDIT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Historic RCPA list
        item {
            Text(
                text = "Recently Submitted RCPA Audits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (audits.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No market intelligence reports saved.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(audits) { aud ->
                val tot = aud.myzithQty + aud.compAQty + aud.compBQty
                val share = if (tot > 0) (aud.myzithQty.toFloat() / tot * 100).toInt() else 0
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(aud.chemistName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Sopan Share: $share%", fontWeight = FontWeight.Bold, color = SopanEmerald)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Daily Qty: Myzith (${aud.myzithQty} units) vs Competitors (${aud.compAQty + aud.compBQty} units)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// TAB 4: FINANCIAL TA/DA ENGINE & COMPLIANCE (PV)
// ==========================================
@Composable
fun ComplianceAndTadaTab(viewModel: SfaViewModel) {
    val adrReports by viewModel.allADRReports.collectAsState()

    var activeSubTab by remember { mutableStateOf("TADA") } // TADA or PV (ADR)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { activeSubTab = "TADA" },
                modifier = Modifier.weight(1.0f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == "TADA") MaterialTheme.colorScheme.primary 
                                     else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("TA/DA Fuel Engine", color = if (activeSubTab == "TADA") Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }

            Button(
                onClick = { activeSubTab = "PV" },
                modifier = Modifier.weight(1.0f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == "PV") MaterialTheme.colorScheme.primary 
                                     else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Pharmacovigilance (ADR)", color = if (activeSubTab == "PV") Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }

        if (activeSubTab == "TADA") {
            TadaEngineSection()
        } else {
            ADRReportingSection(viewModel, adrReports)
        }
    }
}

@Composable
fun TadaEngineSection() {
    var startLocation by remember { mutableStateOf("Kathmandu Office Hub") }
    var destinationClinic by remember { mutableStateOf("Subidhanagar Clinic, Tinkune") }
    var selectedTerrainState by remember { mutableStateOf("Terai Plain") } // "Hilly vs Terai"
    var fuelDistanceInput by remember { mutableStateOf("22") } // in km
    var foodLodgingExpense by remember { mutableStateOf("1200") } // Nepalese NPR

    // Calculate dynamic allowances customized for terrains: Nepal's custom compensation rules:
    // Hilly terrains get elevated fuel compression of 22 NPR/km, Terai gets 14 NPR/km of travel.
    val perKmRate = if (selectedTerrainState == "Hilly Terrain") 22.0 else 14.0
    val subtotalFuel = (fuelDistanceInput.toDoubleOrNull() ?: 0.0) * perKmRate
    val subtotalFood = foodLodgingExpense.toDoubleOrNull() ?: 0.0
    val totalClaimEstimated = subtotalFuel + subtotalFood

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Territory TA/DA Expense Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Travel Allowance and Daily Allowance calculator featuring Nepalese terrain indexes centered around Kathmandu/Lalitpur hubs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = startLocation,
                        onValueChange = { startLocation = it },
                        label = { Text("Starting Hub / Location") },
                        modifier = Modifier.fillMaxWidth().testTag("tada_start_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = destinationClinic,
                        onValueChange = { destinationClinic = it },
                        label = { Text("Destination Clinic / Chemist") },
                        modifier = Modifier.fillMaxWidth().testTag("tada_dest_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Nepal Terrain Tier (Multiplier Index):", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Terai Plain", "Hilly Terrain").forEach { terr ->
                            val isSel = selectedTerrainState == terr
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedTerrainState = terr }
                                    .border(
                                        width = 1.dp,
                                        color = if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = terr,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fuelDistanceInput,
                        onValueChange = { fuelDistanceInput = it },
                        label = { Text("Cumulative Distance Traveled (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("tada_distance"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = foodLodgingExpense,
                        onValueChange = { foodLodgingExpense = it },
                        label = { Text("Food & Lodging Daily Allowance (NPR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("tada_allowance"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calculation Receipt simulation
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Allowance Subtotals (Official Slip):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Fuel claim ($fuelDistanceInput km at $perKmRate NPR):", fontSize = 12.sp)
                                Text("NPR. $subtotalFuel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Daily food & lodging limit:", fontSize = 12.sp)
                                Text("NPR. $subtotalFood", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Estimated Settlement:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("NPR. $totalClaimEstimated", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            // Fuel claim finalized! Show a toast simulator
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("tada_submit")
                    ) {
                        Icon(Icons.Filled.ReceiptLong, "Submit")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SUBMIT FORMAL EXPENSE SLIP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ADRReportingSection(viewModel: SfaViewModel, adrReports: List<ADRReportEntity>) {
    var patientInitials by remember { mutableStateOf("HPL") }
    var patientAge by remember { mutableStateOf("48") }
    var selectedSuspectedFormulation by remember { mutableStateOf(ProductPortfolio.products.first()) }
    var reactionDescription by remember { mutableStateOf("") }
    var severitySelection by remember { mutableStateOf("Moderate") }
    var reporterName by remember { mutableStateOf("District Representative") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Shield, "PV", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DDA Compliance ADR Registry",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "Department of Drug Administration (DDA) Nepal compliant adverse drug reaction (ADR) reporting for patient safety audits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = patientInitials,
                            onValueChange = { patientInitials = it },
                            label = { Text("Patient Initials") },
                            modifier = Modifier.weight(1.0f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = patientAge,
                            onValueChange = { patientAge = it },
                            label = { Text("Age (Years)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.0f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Suspected Sopan Formulation:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().clickable {
                            val currentIndex = ProductPortfolio.products.indexOf(selectedSuspectedFormulation)
                            val nextIndex = (currentIndex + 1) % ProductPortfolio.products.size
                            selectedSuspectedFormulation = ProductPortfolio.products[nextIndex]
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedSuspectedFormulation.brandName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Next Option", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Icon(Icons.Filled.ArrowDropDown, "Next", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = reactionDescription,
                        onValueChange = { reactionDescription = it },
                        label = { Text("Adverse Event / Clinical Symptoms description") },
                        modifier = Modifier.fillMaxWidth().height(80.dp).testTag("pv_reaction"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("ADR Severity Matrix Index:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Mild", "Moderate", "Severe").forEach { sev ->
                            val isSel = severitySelection == sev
                            val selColor = when (sev) {
                                "Severe" -> MaterialTheme.colorScheme.error
                                "Moderate" -> SopanOrange
                                else -> SopanEmerald
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) selColor else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { severitySelection = sev },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sev,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = reporterName,
                        onValueChange = { reporterName = it },
                        label = { Text("Reporter Name (Healthcare Worker)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (reactionDescription.isNotBlank()) {
                                viewModel.addADRReport(
                                    patientInitials = patientInitials,
                                    patientAge = patientAge,
                                    suspectedProduct = selectedSuspectedFormulation.brandName,
                                    reaction = reactionDescription,
                                    severity = severitySelection,
                                    reporterName = reporterName
                                )
                                reactionDescription = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_pv_button")
                    ) {
                        Icon(Icons.Filled.Campaign, "PV ADR")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("POST URGENT DDA-COMPLIANT ADR REPORT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Active ADR compliance log
        item {
            Text(
                text = "DDA Compliance Archives (PV ADR Hub)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (adrReports.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No adverse event safety records saved in this session cycle.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(adrReports) { report ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (report.severity) {
                                        "Severe" -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                        "Moderate" -> SopanOrange.copy(alpha = 0.15f)
                                        else -> SopanEmerald.copy(alpha = 0.15f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = when (report.severity) {
                                    "Severe" -> MaterialTheme.colorScheme.error
                                    "Moderate" -> SopanOrange
                                    else -> SopanEmerald
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Patient: ${report.patientInitials} (${report.patientAge} Yrs)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    color = when (report.severity) {
                                        "Severe" -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                        "Moderate" -> SopanOrange.copy(alpha = 0.15f)
                                        else -> SopanEmerald.copy(alpha = 0.15f)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = report.severity,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (report.severity) {
                                            "Severe" -> MaterialTheme.colorScheme.error
                                            "Moderate" -> SopanOrange
                                            else -> SopanEmerald
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Formula: ${report.suspectedProduct}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Text(
                                text = "ADR Report: ${report.reaction}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Text(
                                text = "Log Auditor: ${report.reporterName}",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// MANAGER DASHBOARD SCREEN
// ==========================================
@Composable
fun ManagerDashboard(viewModel: SfaViewModel) {
    val totalOrders by viewModel.totalOrdersCount.collectAsState()
    val totalVolume by viewModel.totalSalesVolume.collectAsState()
    
    // Observed lists
    val auditsList by viewModel.allCompetitorAudits.collectAsState()
    val toursList by viewModel.allTourPlans.collectAsState()
    val adrsList by viewModel.allADRReports.collectAsState()

    var showHierarchy by remember { mutableStateOf(false) }

    if (showHierarchy) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showHierarchy = false }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = SopanTeal)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Manager Control: Org & Sales Explorer", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Box(modifier = Modifier.weight(1.0f)) {
                OrganizationHierarchyTab(viewModel)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Sales Operations Control Cabin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Operational & Sales Management dashboard: Dev Raj Adhikari / Mr. Suman Guragain",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Interactive Hierarchy Explorer Board Advisor
        item {
            Card(
                onClick = { showHierarchy = true },
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.2.dp, SopanTeal.copy(alpha = 0.3f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = SopanTeal.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountTree,
                            contentDescription = "Org Explorer",
                            tint = SopanTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1.0f)) {
                        Text(
                            text = "Org & Reporting Hierarchy Plan",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SopanTeal
                        )
                        Text(
                            text = "Audit Sopan distribution structures vs. major Indian SFA frameworks.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Open",
                        tint = SopanTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Summary row cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(modifier = Modifier.weight(1.0f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("POB Orders Today", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        Text("$totalOrders Recs", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Card(modifier = Modifier.weight(1.0f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Total Booked Volume", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        Text("NPR. $totalVolume", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SopanEmerald)
                    }
                }
            }
        }

        // SFA Tour Approvals control
        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Field Force Tour Plan Approvals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Perform strategic audits, approving/rejecting representative medical territory tour schedules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (toursList.isEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Currently no outstanding field tour requests submitted.",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        toursList.filter { it.status == "Planned" }.forEach { plan ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(plan.doctorOrClinic, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Date: ${plan.bsDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { viewModel.updateTourPlanStatus(plan, "Approved") },
                                            colors = ButtonDefaults.buttonColors(containerColor = SopanEmerald),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Approve", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                }
                                if (plan.objectives.isNotBlank()) {
                                    Text("Objective: ${plan.objectives}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sopan Field Daily Call Report approvals
        item {
            DcrApprovalsSection(viewModel)
        }

        // Market Intelligence Summary
        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Competitor Intelligence Feed (Azithromycin)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (auditsList.isEmpty()) {
                        Text(
                            text = "No competitor audits (RCPA) received from representatives yet.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        auditsList.forEach { aud ->
                            val totalQty = aud.myzithQty + aud.compAQty + aud.compBQty
                            val sopanShare = if (totalQty > 0) (aud.myzithQty.toFloat() / totalQty * 100).toInt() else 0
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(aud.chemistName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Sopan Myzith Share: $sopanShare%", fontSize = 12.sp, color = SopanEmerald, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Adverse Safety Alert summaries
        item {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pharmacovigilance Compliance Audit Logs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (adrsList.isEmpty()) {
                        Text("No pharmacovigilance adverse reaction logs filed in current cycle. Compliances optimal.", fontSize = 11.sp)
                    } else {
                        adrsList.forEach { report ->
                            Text(
                                text = "Formula: ${report.suspectedProduct} | Reaction: ${report.reaction} [${report.severity}]",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// EXECUTIVE STRATEGIC DASHBOARD SCREEN
// ==========================================
@Composable
fun ExecutiveDashboard(viewModel: SfaViewModel) {
    val totalOrders by viewModel.totalOrdersCount.collectAsState()
    val totalVolume by viewModel.totalSalesVolume.collectAsState()
    val toursList by viewModel.allTourPlans.collectAsState()
    val adrsList by viewModel.allADRReports.collectAsState()

    val totalApprovedTours = toursList.filter { it.status == "Approved" }.size
    val totalPVClaims = adrsList.size

    var showHierarchy by remember { mutableStateOf(false) }

    if (showHierarchy) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showHierarchy = false }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = SopanTeal)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("C-Suite Advisor: Org & Route Architect", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Box(modifier = Modifier.weight(1.0f)) {
                OrganizationHierarchyTab(viewModel)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Sopan Strategic Advisory Suite",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Executive Oversight (CEO Suman Neupane / CBO Anil Shakya)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // C-Suite Strategic Hierarchy Explorer Card
        item {
            Card(
                onClick = { showHierarchy = true },
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.2.dp, SopanTeal.copy(alpha = 0.3f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = SopanTeal.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountTree,
                            contentDescription = "Org Explorer",
                            tint = SopanTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1.0f)) {
                        Text(
                            text = "Distribution & Reporting Architecture Plan",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SopanTeal
                        )
                        Text(
                            text = "Interactive strategic blueprint detailing 3-tier Nepal logistics and SFA roles.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Open",
                        tint = SopanTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Multi-indicator metrics deck
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Executive Core Operational Metric",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column {
                            Text("Est Revenue", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text("NPR. $totalVolume", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SopanEmerald)
                        }

                        Column {
                            Text("Order Slips", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text("$totalOrders Bookings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Column {
                            Text("Tour Audits", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text("$totalApprovedTours OKs", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sopan Field Daily Call Report approvals for C-Suite overview
        item {
            DcrApprovalsSection(viewModel)
        }

        // Company Corporate profile snippet from PRD
        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Business, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sopan Pharmaceuticals Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Inaugurated: Approx 2021-2022 AD (2078 B.S.).\ncGMP Compliant manufacturing facilities located at Lalitpur, and central corporate offices at Tinkune, Kathmandu.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Factory Contact", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text("+977-01-5227163", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Column {
                            Text("HQ Kathmandu Contact", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text("+977-01-5671212", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Active Safety compliance deck
        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DDA Compliance Pharmacovigilance Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Adverse Reaction Cases Filed:", fontSize = 12.sp)
                            Text("$totalPVClaims ADR records in local log", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }

                        Surface(
                            color = if (totalPVClaims == 0) SopanEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (totalPVClaims == 0) "Optimal Health Indicator" else "Oversight Required",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (totalPVClaims == 0) SopanEmerald else MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DcrReportingSection(viewModel: SfaViewModel) {
    val dcrList by viewModel.allDcrReports.collectAsState()
    
    // Form States
    var drName by remember { mutableStateOf("") }
    var selectedSpecialty by remember { mutableStateOf("General Medicine") }
    var clinicName by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf("Ulshield 20/40") }
    var samplesGivenInput by remember { mutableStateOf("") }
    var selectedInput by remember { mutableStateOf("Visual Aid") }
    var selectedReaction by remember { mutableStateOf("Interested") }
    var nextFollowUpInput by remember { mutableStateOf("2083-03-15") }
    
    var showProductDialog by remember { mutableStateOf(false) }
    var showSpecialtyDialog by remember { mutableStateOf(false) }
    var submissionSuccess by remember { mutableStateOf(false) }

    val specialties = listOf("General Medicine", "Cardiologist", "Pediatrician", "Neurologist", "Gastroenterologist", "Orthopedic Surgeon", "Dermatologist")
    val products = listOf("Ulshield 20/40", "Pbin (25/50/75/150)", "Zanstat 40/80", "Myzith (100/200/500)")
    val promoInputs = listOf("Visual Aid", "Literature (LBL)", "Brand Calendar", "Sample Pen", "None")
    val reactions = listOf("Highly Interested", "Interested", "Indifferent", "Demanded Samples")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // DCR KPI Progress Metric Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dynamic Field Detailing Tracker",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time doctor call alignment progress feed.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val dailyCount = dcrList.size
                    val percentage = (dailyCount.toFloat() / 5.0f).coerceAtMost(1.0f)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Target: $dailyCount / 5 calls logged",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(percentage * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SopanEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = percentage,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = SopanEmerald,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Logging form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Log Daily Doctor Call (DCR)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Record dynamic promotional actions and sample distribution on field.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Doctor Name
                    OutlinedTextField(
                        value = drName,
                        onValueChange = { drName = it },
                        label = { Text("Doctor's Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("dcr_dr_name_input"),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Specialty Clickable Row Trigger
                    OutlinedTextField(
                        value = selectedSpecialty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Specialty") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSpecialtyDialog = true },
                        trailingIcon = {
                            IconButton(onClick = { showSpecialtyDialog = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clinic Name
                    OutlinedTextField(
                        value = clinicName,
                        onValueChange = { clinicName = it },
                        label = { Text("Clinic / Hospital Name") },
                        modifier = Modifier.fillMaxWidth().testTag("dcr_clinic_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Product Selective Clickable
                    OutlinedTextField(
                        value = selectedProduct,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Brand Detailing") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showProductDialog = true },
                        trailingIcon = {
                            IconButton(onClick = { showProductDialog = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Samples Given
                    OutlinedTextField(
                        value = samplesGivenInput,
                        onValueChange = { samplesGivenInput = it },
                        label = { Text("Samples Gifted (Packs Qty)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Promo Inputs
                    Text("Promotional Input Offered:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        promoInputs.take(3).forEach { pi ->
                            val isSel = selectedInput == pi
                            Surface(
                                color = if (isSel) SopanTeal.copy(alpha = 0.15f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSel) SopanTeal else Color.LightGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedInput = pi }
                            ) {
                                Text(pi, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) SopanTeal else Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Doctor reaction flow chips
                    Text("Doctor's Advocacy Reaction Sentiment:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        reactions.take(3).forEach { rx ->
                            val isSel = selectedReaction == rx
                            Surface(
                                color = if (isSel) SopanEmerald.copy(alpha = 0.15f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSel) SopanEmerald else Color.LightGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedReaction = rx }
                            ) {
                                Text(rx, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) SopanEmerald else Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Follow Up Input
                    OutlinedTextField(
                        value = nextFollowUpInput,
                        onValueChange = { nextFollowUpInput = it },
                        label = { Text("Next Follow-Up Date (B.S. Format)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (submissionSuccess) {
                        Text("DCR logged successfully! Pending manager audit.", color = SopanEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                    }

                    Button(
                        onClick = {
                            if (drName.isNotBlank() && clinicName.isNotBlank()) {
                                viewModel.addDcrReport(
                                    date = "2083-02-15",
                                    mrName = "Suman Shrestha",
                                    doctorName = drName,
                                    specialty = selectedSpecialty,
                                    clinicName = clinicName,
                                    detailedProduct = selectedProduct,
                                    samplesGiven = samplesGivenInput.toIntOrNull() ?: 0,
                                    inputsDistributed = selectedInput,
                                    doctorReaction = selectedReaction,
                                    nextFollowUpDate = nextFollowUpInput
                                )
                                submissionSuccess = true
                                drName = ""
                                clinicName = ""
                                samplesGivenInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("dcr_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SopanTeal)
                    ) {
                        Text("Submit Daily Call Report", color = Color.White)
                    }
                }
            }
        }

        // DCR Logs history list
        item {
            Text(
                text = "My Field Call Activity Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (dcrList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "No Doctor Calls made in this cycle yet. Record your visits above.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
            }
        } else {
            items(dcrList.size) { index ->
                val dcr = dcrList[index]
                val borderCol = when (dcr.status) {
                    "Approved" -> SopanEmerald
                    "Rejected" -> MaterialTheme.colorScheme.error
                    else -> Color(0xFFFFB300)
                }
                
                Card(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, borderCol.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(dcr.doctorName, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                Text("${dcr.specialty} | ${dcr.clinicName}", fontSize = 11.sp, color = Color.Gray)
                            }
                            
                            Surface(
                                color = borderCol.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = dcr.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = borderCol,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text("BRANDS DETAILED", fontSize = 9.sp, color = Color.Gray)
                                Text(dcr.detailedProduct, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Column {
                                Text("SAMPLES GIFTED", fontSize = 9.sp, color = Color.Gray)
                                Text("${dcr.samplesGiven} Packs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Column {
                                Text("REACTION", fontSize = 9.sp, color = Color.Gray)
                                Text(dcr.doctorReaction, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SopanEmerald)
                            }
                        }

                        if (dcr.managerRemarks != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color.Gray.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Manager Remarks: ${dcr.managerRemarks}",
                                    fontSize = 10.sp,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Specialty selector Dialog
    if (showSpecialtyDialog) {
        AlertDialog(
            onDismissRequest = { showSpecialtyDialog = false },
            title = { Text("Select Target Specialty") },
            text = {
                Column {
                    specialties.forEach { spec ->
                        Text(
                            text = spec,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSpecialty = spec
                                    showSpecialtyDialog = false
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpecialtyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Product selector Dialog
    if (showProductDialog) {
        AlertDialog(
            onDismissRequest = { showProductDialog = false },
            title = { Text("Select Promoted Brand") },
            text = {
                Column {
                    products.forEach { prod ->
                        Text(
                            text = prod,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedProduct = prod
                                    showProductDialog = false
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProductDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun DcrApprovalsSection(viewModel: SfaViewModel) {
    val dcrList by viewModel.allDcrReports.collectAsState()
    var selectedReportForRemarks by remember { mutableStateOf<DcrReportEntity?>(null) }
    var managerRemarksText by remember { mutableStateOf("") }

    val pendingLogs = dcrList.filter { it.status == "Pending" }

    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sopan Field Daily Call (DCR) Approvals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Review detailing activities, sample allocations, and advocacy reactions submitted by field representatives.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (pendingLogs.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Currently no outstanding representative doctor call reports waiting review.",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                pendingLogs.forEach { dcr ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.0f)) {
                                Text(dcr.doctorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Met By: ${dcr.mrName} | Specialty: ${dcr.specialty}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                Text("Clinic: ${dcr.clinicName}", fontSize = 11.sp, color = Color.Gray)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.updateDcrStatus(dcr.id, "Approved", "Activity verified. Nice presentation of " + dcr.detailedProduct)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SopanEmerald),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Approve", fontSize = 10.sp, color = Color.White)
                                }
                                
                                Button(
                                    onClick = { selectedReportForRemarks = dcr; managerRemarksText = "" },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Reject", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text("Brand: ${dcr.detailedProduct}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("Samples: ${dcr.samplesGiven} Pks", fontSize = 11.sp)
                            Text("Reaction: ${dcr.doctorReaction}", fontSize = 11.sp, color = SopanEmerald, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (selectedReportForRemarks != null) {
        AlertDialog(
            onDismissRequest = { selectedReportForRemarks = null },
            title = { Text("Log Disapproval Remarks") },
            text = {
                Column {
                    Text("Provide correction remarks for ${selectedReportForRemarks?.doctorName}'s DCR submission:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = managerRemarksText,
                        onValueChange = { managerRemarksText = it },
                        label = { Text("Remarks") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedReportForRemarks?.let { report ->
                            viewModel.updateDcrStatus(report.id, "Rejected", managerRemarksText.ifBlank { "Re-verification required." })
                        }
                        selectedReportForRemarks = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Decline DCR", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedReportForRemarks = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun Internal_SystemAdminDashboard_Obsolete(viewModel: SfaViewModel) {
    val divisions by viewModel.allDivisions.collectAsState(initial = emptyList())
    val territories by viewModel.allTerritories.collectAsState(initial = emptyList())
    val employees by viewModel.filteredEmployees.collectAsState(initial = emptyList())
    val molecules by viewModel.allMolecules.collectAsState(initial = emptyList())
    val products by viewModel.allProducts.collectAsState(initial = emptyList())
    val categories by viewModel.allTherapeuticCategories.collectAsState(initial = emptyList())

    var activeTab by remember { mutableStateOf("SYSTEM_STATS") } // SYSTEM_STATS, GEOGRAPHY, MEDICINES, STAFF

    // Alert / Success Notification
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Administrative Masthead Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = "Admin Desk",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Sopan Administrative Suite & Control Center",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Admin Configuration Mode • Dynamic Masters Control",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // feedback status notification toast
        feedbackMessage?.let { text ->
            Surface(
                color = SopanEmerald.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SopanEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SopanEmerald, modifier = Modifier.weight(1.0f))
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = SopanEmerald,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { feedbackMessage = null }
                    )
                }
            }
        }

        // Horizontal visual Segmented Buttons for tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            item {
                FilterChip(
                    selected = activeTab == "SYSTEM_STATS",
                    onClick = { activeTab = "SYSTEM_STATS" },
                    label = { Text("Control Desk") },
                    leadingIcon = { Icon(Icons.Filled.SettingsInputAntenna, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = activeTab == "GEOGRAPHY",
                    onClick = { activeTab = "GEOGRAPHY" },
                    label = { Text("Territories & Divisions") },
                    leadingIcon = { Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = activeTab == "MEDICINES",
                    onClick = { activeTab = "MEDICINES" },
                    label = { Text("Molecules & Brands") },
                    leadingIcon = { Icon(Icons.Filled.Biotech, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = activeTab == "STAFF",
                    onClick = { activeTab = "STAFF" },
                    label = { Text("Recruit & Hierarchy") },
                    leadingIcon = { Icon(Icons.Filled.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        // Tab Content Router
        when (activeTab) {
            "SYSTEM_STATS" -> {
                // Control Desk
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // KPI stats overview grid-like container
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Database Matrix Counters", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                MiniMetricUnit("Divisions", divisions.size.toString(), SopanTeal)
                                MiniMetricUnit("Territories", territories.size.toString(), SopanOrange)
                                MiniMetricUnit("Reps/Staff", employees.size.toString(), SopanEmerald)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                MiniMetricUnit("Molecules", molecules.size.toString(), SopanTeal)
                                MiniMetricUnit("Brand Products", products.size.toString(), SopanOrange)
                                MiniMetricUnit("Therapeutic", categories.size.toString(), SopanEmerald)
                            }
                        }
                    }

                    // Sandbox Management Box
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "Database Dynamic Sandbox Control",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Performing a dynamic factory reset wipes all modifications across territories, molecules, staff, and brands; then restores pristine seed data default templates for testing.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.factoryResetDatabase()
                                    feedbackMessage = "Database cleared and default pristine Sopan demo datasets re-populated successfully!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dynamic Re-populate Default Seed Templates", color = Color.White)
                            }
                        }
                    }
                }
            }

            "GEOGRAPHY" -> {
                // Divisions & Territories Configuration
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Add Division Form
                    var newDivName by remember { mutableStateOf("") }
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Setup New Business Division", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SopanTeal)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newDivName,
                                onValueChange = { newDivName = it },
                                label = { Text("Division Name (e.g. Cardia Oncology)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (newDivName.isNotBlank()) {
                                        val maxId = (divisions.map { it.id }.maxOrNull() ?: 0) + 1
                                        viewModel.addDivision(DivisionEntity(id = maxId, name = newDivName))
                                        feedbackMessage = "Successfully added new Division: $newDivName"
                                        newDivName = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SopanTeal),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publish Division Node", color = Color.White)
                            }
                        }
                    }

                    // Add Territory Form
                    var newTerrName by remember { mutableStateOf("") }
                    var newTerrType by remember { mutableStateOf("Area") } // Zone, Region, Area, HQ
                    var parentIdInput by remember { mutableStateOf("") }

                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Setup New Geographical Territory Node", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SopanOrange)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newTerrName,
                                onValueChange = { newTerrName = it },
                                label = { Text("Territory / District Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("Territory Node Level Slicing:", style = MaterialTheme.typography.labelMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Zone", "Region", "Area", "HQ").forEach { type ->
                                    val isSel = newTerrType == type
                                    Surface(
                                        color = if (isSel) SopanOrange.copy(alpha = 0.15f) else Color.Transparent,
                                        border = BorderStroke(1.dp, if (isSel) SopanOrange else Color.LightGray),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.0f).clickable { newTerrType = type }
                                    ) {
                                        Text(type, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) SopanOrange else Color.Gray)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = parentIdInput,
                                onValueChange = { parentIdInput = it },
                                label = { Text("Parent Territory Node ID (Optional)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (newTerrName.isNotBlank()) {
                                        val nId = (territories.map { it.id }.maxOrNull() ?: 0) + 1
                                        val pId = parentIdInput.toIntOrNull() ?: 0
                                        viewModel.addTerritory(TerritoryEntity(id = nId, type = newTerrType, name = newTerrName, parentId = pId))
                                        feedbackMessage = "Successfully configured $newTerrType territory node: $newTerrName"
                                        newTerrName = ""
                                        parentIdInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SopanOrange),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Map Territory Node", color = Color.White)
                            }
                        }
                    }

                    // Active Node list breakdown
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Live Geographical Territory Directory", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("A list of registered divisions and administrative nodes mapped in standard Nepal levels.", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Text("Registered Divisions:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SopanTeal)
                            divisions.forEach { d ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("• ${d.name}", fontSize = 11.sp)
                                    Text("Node ID: ${d.id}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Text("Registered Territories (First 15):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SopanOrange)
                            territories.take(15).forEach { t ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("• [${t.type}] ${t.name}", fontSize = 11.sp)
                                    Text("ID: ${t.id} | Parent: ${t.parentId}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            "MEDICINES" -> {
                // Medicines configuration
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Add Active Molecule Form
                    var molName by remember { mutableStateOf("") }
                    var molStrength by remember { mutableStateOf("") }

                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Setup Dynamic Chemical Molecule Master", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SopanTeal)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = molName,
                                onValueChange = { molName = it },
                                label = { Text("Generic Molecule Name (e.g. Paracetamol)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = molStrength,
                                onValueChange = { molStrength = it },
                                label = { Text("Standard dosage strength (e.g. 500mg)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (molName.isNotBlank() && molStrength.isNotBlank()) {
                                        val nId = (molecules.map { it.id }.maxOrNull() ?: 0) + 1
                                        viewModel.addMolecule(MoleculeEntity(id = nId, name = molName, strength = molStrength))
                                        feedbackMessage = "Molecule generically registered: $molName ($molStrength)"
                                        molName = ""
                                        molStrength = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SopanTeal),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Register Molecule", color = Color.White)
                            }
                        }
                    }

                    // Add Dynamic Product Category Form
                    var catName by remember { mutableStateOf("") }

                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Setup New Therapeutic Category Class", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SopanOrange)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = catName,
                                onValueChange = { catName = it },
                                label = { Text("Therapeutic Name (e.g. Cardiovascular)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (catName.isNotBlank()) {
                                        val nId = (categories.map { it.id }.maxOrNull() ?: 0) + 1
                                        viewModel.addTherapeuticCategory(TherapeuticCategoryEntity(id = nId, name = catName))
                                        feedbackMessage = "Therapeutic Class created: $catName"
                                        catName = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SopanOrange),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Setup Category Group", color = Color.White)
                            }
                        }
                    }

                    // Live directories list
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Registered Chemical Molecules", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            molecules.forEach { m ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("▪ ${m.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Strength: ${m.strength} | Node: ${m.id}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            "STAFF" -> {
                // Active Staff Setup & Recruitment Console
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Recruiting / Register form
                    var nameInput by remember { mutableStateOf("") }
                    var empCode by remember { mutableStateOf("") }
                    var selectedRoleOption by remember { mutableStateOf("MR") } // MR, ASM, ZSM, RSM
                    var phoneInput by remember { mutableStateOf("") }
                    var emailInput by remember { mutableStateOf("") }
                    var reportsToIdInput by remember { mutableStateOf("") }

                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Recruit & Configure Field Operational Node", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SopanEmerald)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Employee Full Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = empCode,
                                onValueChange = { empCode = it },
                                label = { Text("Employee Code (e.g. MR15, ASM12)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("Select Node Role:", style = MaterialTheme.typography.labelMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("MR", "ASM", "ZSM", "RSM").forEach { r ->
                                    val isSel = selectedRoleOption == r
                                    Surface(
                                        color = if (isSel) SopanEmerald.copy(alpha = 0.15f) else Color.Transparent,
                                        border = BorderStroke(1.dp, if (isSel) SopanEmerald else Color.LightGray),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.0f).clickable { selectedRoleOption = r }
                                    ) {
                                        Text(r, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) SopanEmerald else Color.Gray)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = reportsToIdInput,
                                onValueChange = { reportsToIdInput = it },
                                label = { Text("Reports-To Supervisor Employee ID") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (nameInput.isNotBlank() && empCode.isNotBlank()) {
                                        val nId = (employees.map { it.id }.maxOrNull() ?: 10) + 1
                                        val repId = reportsToIdInput.toIntOrNull() ?: 0
                                        viewModel.addEmployee(
                                            EmployeeEntity(
                                                id = nId,
                                                name = nameInput,
                                                code = empCode,
                                                role = selectedRoleOption,
                                                divisionId = 1,
                                                territoryId = 11,
                                                reportsToId = repId,
                                                joiningDate = "2026-05-30",
                                                phone = phoneInput.ifBlank { "9851000000" },
                                                email = emailInput.ifBlank { "info@medorn.com" }
                                            )
                                        )
                                        feedbackMessage = "Recruited representative: $nameInput ($selectedRoleOption) assigned ID: $nId"
                                        nameInput = ""
                                        empCode = ""
                                        phoneInput = ""
                                        emailInput = ""
                                        reportsToIdInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SopanEmerald),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publish Employee Node", color = Color.White)
                            }
                        }
                    }

                    // Directory tree breakdown with direct dynamic delete operation!
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Active SFA Organization Directory Tree", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Shows real-time status of managers and reps. Administrators have master authorization to retire nodes.", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(10.dp))

                            employees.forEach { emp ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(1.dp, Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Role: ${emp.role} • Code: ${emp.code}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                        Text("ID: ${emp.id} • Reports-To: ${emp.reportsToId}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    
                                    // Remove employee action button
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteEmployee(emp.id)
                                            feedbackMessage = "Retired employee node: ${emp.name}"
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniMetricUnit(label: String, valStr: String, accentColor: Color) {
    Surface(
        color = accentColor.copy(alpha = 0.08f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        modifier = Modifier.width(96.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valStr, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
