package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.SfaRepository
import com.example.ui.SfaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Before
  fun setUp() {
    Dispatchers.setMain(Dispatchers.Unconfined)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Sopan SFA", appName)
  }

  @Test
  fun `test viewModel database seeding and state flow propagation`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    
    val repo = SfaRepository(db.sfaDao())
    val viewModel = SfaViewModel(repo)
    
    // Check divisions
    val divisions = repo.allDivisions.first { it.isNotEmpty() }
    assertFalse("Divisions should not be empty after seeding!", divisions.isEmpty())
    assertTrue("Divisions should contain Rx (Prescription)!", divisions.any { it.name == "Rx (Prescription)" })

    // Check products
    val products = repo.allProducts.first { it.isNotEmpty() }
    assertFalse("Products list should be seeded!", products.isEmpty())
    assertTrue("Products list should contain Ulshield 20/40!", products.any { it.brand == "Ulshield 20/40" })

    db.close()
  }
}
