package com.example

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.preferences.LocalUserPreferences
import com.example.preferences.PreferencesStateHolder
import com.example.preferences.UserPreferences
import com.example.tutor.TutorDatabase
import com.example.tutor.TutorPreloadData
import com.example.tutor.TutorProfilesRepository
import com.example.tutor.TutorProfilesViewModel
import com.example.tutor.TutorRepository
import com.example.tutor.TutorViewModel
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

  private lateinit var db: TutorDatabase
  private lateinit var repository: TutorRepository
  private lateinit var profilesRepository: TutorProfilesRepository
  private lateinit var viewModel: TutorViewModel
  private lateinit var profilesViewModel: TutorProfilesViewModel

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Create an in-memory Room database for fast local unit testing
    db = Room.inMemoryDatabaseBuilder(context, TutorDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    
    val dao = db.tutorDao()
    dao.insertUsers(TutorPreloadData.initialUsers)
    
    val profileDao = db.tutorProfileDao()
    profileDao.insertTutors(TutorPreloadData.initialTutorProfiles)

    repository = TutorRepository(dao)
    profilesRepository = TutorProfilesRepository(profileDao)

    viewModel = TutorViewModel(repository)
    profilesViewModel = TutorProfilesViewModel(profilesRepository)
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
          MainAppContent(
            viewModel = viewModel,
            profilesViewModel = profilesViewModel
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
