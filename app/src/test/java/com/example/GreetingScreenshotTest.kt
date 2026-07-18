package com.example

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.bible.BibleDatabase
import com.example.bible.BiblePreloadData
import com.example.bible.BibleRepository
import com.example.bible.BibleViewModel
import com.example.preferences.LocalUserPreferences
import com.example.preferences.PreferencesStateHolder
import com.example.preferences.UserPreferences
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var db: BibleDatabase
  private lateinit var repository: BibleRepository
  private lateinit var viewModel: BibleViewModel

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Create an in-memory Room database for fast local unit testing
    db = Room.inMemoryDatabaseBuilder(context, BibleDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    
    val dao = db.bibleDao()
    // Pre-populate with our Amharic scriptures
    dao.insertVerses(BiblePreloadData.preloadedVerses)
    
    repository = BibleRepository(dao)
    viewModel = BibleViewModel(repository)
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun greeting_screenshot() {
    val mockHolder = PreferencesStateHolder(UserPreferences()) {}
    composeTestRule.setContent {
      CompositionLocalProvider(LocalUserPreferences provides mockHolder) {
        MyApplicationTheme {
          MainAppContent(viewModel = viewModel)
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
