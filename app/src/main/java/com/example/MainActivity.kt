package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.preferences.LocalUserPreferences
import com.example.preferences.PreferencesManager
import com.example.preferences.PreferencesStateHolder
import com.example.tutor.AppLanguage
import com.example.tutor.TutorDatabase
import com.example.tutor.TutorProfilesRepository
import com.example.tutor.TutorProfilesViewModel
import com.example.tutor.TutorProfilesViewModelFactory
import com.example.tutor.TutorRepository
import com.example.tutor.TutorViewModel
import com.example.tutor.TutorViewModelFactory
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.BotChatScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.RegistrationFormScreen
import com.example.ui.theme.AnnaBlue
import com.example.ui.theme.AnnaEmerald
import com.example.ui.theme.AnnaGold
import com.example.ui.theme.AnnaNavy
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    EXPLORE,
    BOT_CHAT,
    FORM_REGISTER,
    ADMIN_PANEL,
    ABOUT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            // Initialize preferences
            val preferencesManager = remember { PreferencesManager(context) }
            val preferencesHolder = remember {
                PreferencesStateHolder(preferencesManager.loadPreferences()) { updated ->
                    preferencesManager.savePreferences(updated)
                }
            }

            // Initialize AnnA Tutor Database and Repositories
            val database = remember { TutorDatabase.getDatabase(context, scope) }
            val repository = remember { TutorRepository(database.tutorDao()) }
            val profilesRepository = remember { TutorProfilesRepository(database.tutorProfileDao()) }

            val tutorViewModel: TutorViewModel = viewModel(
                factory = TutorViewModelFactory(repository)
            )
            val tutorProfilesViewModel: TutorProfilesViewModel = viewModel(
                factory = TutorProfilesViewModelFactory(profilesRepository)
            )

            CompositionLocalProvider(LocalUserPreferences provides preferencesHolder) {
                MyApplicationTheme {
                    MainAppContent(
                        viewModel = tutorViewModel,
                        profilesViewModel = tutorProfilesViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: TutorViewModel,
    profilesViewModel: TutorProfilesViewModel
) {
    var currentScreen by remember { mutableStateOf(Screen.EXPLORE) }
    val language by viewModel.language.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    // Handle back button on secondary screens to return to Explore
    if (currentScreen != Screen.EXPLORE) {
        BackHandler {
            currentScreen = Screen.EXPLORE
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AnnaNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_anna_launcher),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "AnnA Tutor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == AppLanguage.AMHARIC) "አና ቱተር • ማዕከላዊ መድረክ" else "Central Tutoring Platform",
                                fontSize = 10.sp,
                                color = AnnaBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher Toggle
                    Surface(
                        onClick = { viewModel.toggleLanguage() },
                        shape = RoundedCornerShape(20.dp),
                        color = AnnaBlue.copy(alpha = 0.12f),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("language_toggle_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language",
                                tint = AnnaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.AMHARIC) "አማርኛ" else "English",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnnaBlue
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // 1. Explore Tab
                NavigationBarItem(
                    selected = currentScreen == Screen.EXPLORE,
                    onClick = { currentScreen = Screen.EXPLORE },
                    icon = {
                        Icon(imageVector = Icons.Default.School, contentDescription = "Explore")
                    },
                    label = {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "ፈልግ" else "Explore",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        indicatorColor = AnnaBlue
                    ),
                    modifier = Modifier.testTag("nav_explore")
                )

                // 2. Telegram Bot Tab
                NavigationBarItem(
                    selected = currentScreen == Screen.BOT_CHAT,
                    onClick = { currentScreen = Screen.BOT_CHAT },
                    icon = {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = "Bot")
                    },
                    label = {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "ቦት" else "Bot",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        indicatorColor = AnnaBlue
                    ),
                    modifier = Modifier.testTag("nav_bot")
                )

                // 3. Form Registration Tab
                NavigationBarItem(
                    selected = currentScreen == Screen.FORM_REGISTER,
                    onClick = { currentScreen = Screen.FORM_REGISTER },
                    icon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Register")
                    },
                    label = {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "ምዝገባ" else "Register",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        indicatorColor = AnnaBlue
                    ),
                    modifier = Modifier.testTag("nav_register")
                )

                // 4. Admin Panel Tab
                NavigationBarItem(
                    selected = currentScreen == Screen.ADMIN_PANEL,
                    onClick = { currentScreen = Screen.ADMIN_PANEL },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (allUsers.isNotEmpty()) {
                                    Badge(containerColor = AnnaEmerald) {
                                        Text(
                                            text = "${allUsers.size}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.SupervisorAccount, contentDescription = "Admin")
                        }
                    },
                    label = {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "አድሚን" else "Admin",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        indicatorColor = AnnaBlue
                    ),
                    modifier = Modifier.testTag("nav_admin")
                )

                // 5. About Tab
                NavigationBarItem(
                    selected = currentScreen == Screen.ABOUT,
                    onClick = { currentScreen = Screen.ABOUT },
                    icon = {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "About")
                    },
                    label = {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "ስለ እኛ" else "About",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        indicatorColor = AnnaBlue
                    ),
                    modifier = Modifier.testTag("nav_about")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.EXPLORE -> ExploreScreen(
                    viewModel = viewModel,
                    profilesViewModel = profilesViewModel
                )
                Screen.BOT_CHAT -> BotChatScreen(
                    viewModel = viewModel,
                    onNavigateToAdmin = { currentScreen = Screen.ADMIN_PANEL }
                )
                Screen.FORM_REGISTER -> RegistrationFormScreen(
                    viewModel = viewModel,
                    profilesViewModel = profilesViewModel,
                    onSuccessNav = { currentScreen = Screen.ADMIN_PANEL }
                )
                Screen.ADMIN_PANEL -> AdminPanelScreen(viewModel = viewModel)
                Screen.ABOUT -> AboutScreen(viewModel = viewModel)
            }
        }
    }
}
