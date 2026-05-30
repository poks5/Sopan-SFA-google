package com.example

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.SfaRepository
import com.example.ui.DashboardScreen
import com.example.ui.LoginScreen
import com.example.ui.SfaViewModel
import com.example.ui.SfaViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private var database: AppDatabase? = null
  private var repository: SfaRepository? = null
  private var viewModel: SfaViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Uncaught exception reporter
    val prefs = getSharedPreferences("sfa_diagnostics", Context.MODE_PRIVATE)
    val savedCrash = prefs.getString("last_crash", null)

    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      val stackTrace = Log.getStackTraceString(throwable)
      val loggedCrash = "Thread: ${thread.name}\n\nException: ${throwable.localizedMessage}\n\nStacktrace:\n$stackTrace"
      prefs.edit().putString("last_crash", loggedCrash).commit()
      defaultHandler?.uncaughtException(thread, throwable)
    }

    enableEdgeToEdge()

    if (savedCrash != null) {
      setContent {
        MyApplicationTheme {
          CrashDiagnosticScreen(
            crashInfo = savedCrash,
            onClear = {
              prefs.edit().remove("last_crash").commit()
              // Recreate activity to clean state
              recreate()
            }
          )
        }
      }
      return
    }

    try {
      var db: AppDatabase? = null
      try {
        db = Room.databaseBuilder(
          applicationContext,
          AppDatabase::class.java,
          "sopan_sfa_database"
        ).fallbackToDestructiveMigration()
         .build()

        // Force Room to verify schema and open SQLite database safely on a background thread
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
          db.openHelper.writableDatabase
        }

      } catch (e: Exception) {
        Log.e("MainActivity", "Database schema integrity verification failed, resetting file...", e)
        // Safely wipe out corrupt or mismatched database files on the mobile filesystem
        applicationContext.deleteDatabase("sopan_sfa_database")

        // Recreate the database fresh
        db = Room.databaseBuilder(
          applicationContext,
          AppDatabase::class.java,
          "sopan_sfa_database"
        ).fallbackToDestructiveMigration()
         .build()

        // Force verify schema verification on the fresh database to ensure success
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
          db.openHelper.writableDatabase
        }
      }

      database = db

      val repo = SfaRepository(db.sfaDao())
      repository = repo

      val factory = SfaViewModelFactory(repo)
      val vm = ViewModelProvider(this, factory)[SfaViewModel::class.java]
      viewModel = vm

      // Clear any previously rescued crash logs since the SFA app is now successfully loaded!
      prefs.edit().remove("last_crash").commit()

      setContent {
        MyApplicationTheme {
          val isLoggedIn by vm.isLoggedIn.collectAsState()

          Box(modifier = Modifier.fillMaxSize()) {
            if (isLoggedIn) {
              DashboardScreen(
                viewModel = vm,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              LoginScreen(
                viewModel = vm,
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }
    } catch (t: Throwable) {
      val stackTrace = Log.getStackTraceString(t)
      val loggedCrash = "OnCreate Failure\n\nException: ${t.localizedMessage}\n\nStacktrace:\n$stackTrace"
      prefs.edit().putString("last_crash", loggedCrash).commit()
      
      setContent {
        MyApplicationTheme {
          CrashDiagnosticScreen(
            crashInfo = loggedCrash,
            onClear = {
              prefs.edit().remove("last_crash").commit()
              recreate()
            }
          )
        }
      }
    }
  }
}

@Composable
fun CrashDiagnosticScreen(crashInfo: String, onClear: () -> Unit) {
  Scaffold { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(Color(0xFF212121))
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = "Warning",
        tint = Color(0xFFEF5350),
        modifier = Modifier.size(54.dp)
      )
      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "SOPAN SFA CRASH LOGS",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "An unhandled exception occurred during application execution:",
        style = MaterialTheme.typography.bodySmall,
        color = Color.LightGray,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(14.dp))
      Surface(
        color = Color.Black,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = crashInfo,
            color = Color(0xFF81C784),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onClear,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = MaterialTheme.shapes.medium
      ) {
        Text("Clear Error & Safely Restart SFA", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  }
}
