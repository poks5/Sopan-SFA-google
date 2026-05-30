package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun SystemAdminDashboard(viewModel: SfaViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val divisions by viewModel.allDivisions.collectAsState(initial = emptyList())
    var newDivisionName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Business Admin: Divisions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Add Division Form
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newDivisionName,
                    onValueChange = { newDivisionName = it },
                    label = { Text("New Division Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (newDivisionName.isNotBlank()) {
                            // Using a safer ID generation (System.currentTimeMillis().toInt() or similar)
                            // Even better is if Room handled this, but for now, let's use a simple safe increment
                            val newId = (divisions.maxOfOrNull { it.id } ?: 0) + 1
                            viewModel.addDivision(DivisionEntity(id = newId, name = newDivisionName))
                            newDivisionName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Division")
                }
            }
        }

        // Divisions List
        Text("Active Divisions (${divisions.size})", style = MaterialTheme.typography.titleMedium)
        divisions.forEach { division ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(division.name, fontWeight = FontWeight.Bold)
                    Text("ID: ${division.id}", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Note: Setup for Territories and Employees is locked in Admin for future appropriateness.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Backend Sync Controls
        Text("Backend Sync (Firebase)", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.pushToFirebase(context) }, modifier = Modifier.weight(1f)) {
                Text("Push Divisions to Cloud")
            }
            Button(onClick = { viewModel.pullFromFirebase(context) }, modifier = Modifier.weight(1f)) {
                Text("Pull Divisions from Cloud")
            }
        }
    }
}
