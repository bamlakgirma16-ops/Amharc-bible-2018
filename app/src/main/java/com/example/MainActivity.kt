package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bible.BibleDatabase
import com.example.bible.BibleRepository
import com.example.bible.BibleVerse
import com.example.bible.BibleViewModel
import com.example.bible.BibleViewModelFactory
import com.example.bible.StudyNote
import com.example.bible.TranslationMode
import com.example.preferences.AccentColor
import com.example.preferences.AppTheme
import com.example.preferences.LocalUserPreferences
import com.example.preferences.PreferencesManager
import com.example.preferences.PreferencesStateHolder
import com.example.preferences.usePreferences
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize user preferences helper & load initial values
        val preferencesManager = PreferencesManager(this)
        val initialPrefs = preferencesManager.loadPreferences()

        setContent {
            val scope = rememberCoroutineScope()

            // 2. Instantiate Room Database & Repository
            val context = LocalContext.current
            val bibleDatabase = remember { BibleDatabase.getDatabase(context, scope) }
            val bibleRepository = remember { BibleRepository(bibleDatabase.bibleDao()) }

            // 3. Setup User Preferences state holder (Acts as custom context value provider)
            val preferencesStateHolder = remember {
                PreferencesStateHolder(initialPrefs) { updatedPrefs ->
                    preferencesManager.savePreferences(updatedPrefs)
                }
            }

            // 4. Bind Preferences state to CompositionLocalProvider
            androidx.compose.runtime.CompositionLocalProvider(
                LocalUserPreferences provides preferencesStateHolder
            ) {
                MyApplicationTheme {
                    // 5. Instantiate ViewModel inside the providers wrapper
                    val bibleViewModel: BibleViewModel = viewModel(
                        factory = BibleViewModelFactory(bibleRepository)
                    )

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) { innerPadding ->
                        MainAppContent(
                            viewModel = bibleViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

// Available tabs in our Amharic Bible App
enum class BibleTab {
    READ,
    SEARCH,
    STUDY,
    SETTINGS
}

@Composable
fun MainAppContent(
    viewModel: BibleViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(BibleTab.READ) }

    Column(modifier = modifier.fillMaxSize()) {
        // Main Screen View Router
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                BibleTab.READ -> ReadScriptureScreen(viewModel)
                BibleTab.SEARCH -> SearchScreen(viewModel)
                BibleTab.STUDY -> StudyLibraryScreen(viewModel)
                BibleTab.SETTINGS -> SettingsScreen(viewModel)
            }
        }

        // Custom stylized Navigation bar
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.testTag("main_navigation_bar")
        ) {
            NavigationBarItem(
                selected = activeTab == BibleTab.READ,
                onClick = { activeTab = BibleTab.READ },
                icon = { Icon(Icons.Default.Book, contentDescription = "Read") },
                label = { Text("ማንበብ", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("nav_read_tab")
            )
            NavigationBarItem(
                selected = activeTab == BibleTab.SEARCH,
                onClick = { activeTab = BibleTab.SEARCH },
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text("ፈልግ", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("nav_search_tab")
            )
            NavigationBarItem(
                selected = activeTab == BibleTab.STUDY,
                onClick = { activeTab = BibleTab.STUDY },
                icon = { Icon(Icons.Default.LibraryBooks, contentDescription = "My Study") },
                label = { Text("የእኔ ጥናት", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("nav_study_tab")
            )
            NavigationBarItem(
                selected = activeTab == BibleTab.SETTINGS,
                onClick = { activeTab = BibleTab.SETTINGS },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("ቅንብሮች", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("nav_settings_tab")
            )
        }
    }
}

// ==================== TABS IMPLEMENTATIONS ====================

// --- 1. SCRIPTURE READER SCREEN ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReadScriptureScreen(viewModel: BibleViewModel) {
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val translationMode by viewModel.translationMode.collectAsState()
    val verses by viewModel.activeVerses.collectAsState()

    var showBookDropdown by remember { mutableStateOf(false) }
    var showChapterDropdown by remember { mutableStateOf(false) }

    // Active selected verse context for study bottom sheet
    var activeVerseForStudy by remember { mutableStateOf<BibleVerse?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // HEADER BAR WITH DESIGNER VECTOR LOGO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("bible_logo_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp),
            border = borderStroke()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // EXQUISITE UNIQUE BIBLE VECTOR LOGO INTEGRATION (MANDATORY REQUIREMENT)
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_bible_logo),
                        contentDescription = "Amharic Bible Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "የአማርኛ መጽሐፍ ቅዱስ 2018",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Amharic Bible 2018 New",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Developed by Samson Terefe",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // NAVIGATION SELECTORS ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Selector
            Box(modifier = Modifier.weight(1.5f)) {
                Button(
                    onClick = { showBookDropdown = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("book_selector_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = selectedBook,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                DropdownMenu(
                    expanded = showBookDropdown,
                    onDismissRequest = { showBookDropdown = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    viewModel.availableBooks.forEach { bookPair ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        bookPair.first,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        bookPair.second,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                viewModel.selectBook(bookPair.first)
                                showBookDropdown = false
                            },
                            modifier = Modifier.testTag("book_item_${bookPair.second.lowercase()}")
                        )
                    }
                }
            }

            // Chapter Selector
            Box(modifier = Modifier.weight(0.8f)) {
                Button(
                    onClick = { showChapterDropdown = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("chapter_selector_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "ምዕራፍ $selectedChapter",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                DropdownMenu(
                    expanded = showChapterDropdown,
                    onDismissRequest = { showChapterDropdown = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    // Based on available preloaded chapters
                    val chapters = if (selectedBook == "የዮሐንስ ወንጌል") listOf(1, 3) else listOf(1, 6, 23)
                    chapters.forEach { chap ->
                        DropdownMenuItem(
                            text = { Text("ምዕራፍ $chap", fontWeight = FontWeight.Bold) },
                            onClick = {
                                viewModel.selectChapter(chap)
                                showChapterDropdown = false
                            },
                            modifier = Modifier.testTag("chapter_item_$chap")
                        )
                    }
                }
            }
        }

        // TRANSLATION PILLS ROW
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TranslationMode.values().forEach { mode ->
                val isSelected = translationMode == mode
                val displayName = when (mode) {
                    TranslationMode.AMHARIC_ONLY -> "አማርኛ ብቻ"
                    TranslationMode.ENGLISH_ONLY -> "English Only"
                    TranslationMode.BILINGUAL -> "Bilingual (ሁለቱም)"
                }
                val chipColors = if (isSelected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { viewModel.setTranslationMode(mode) },
                    colors = chipColors,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("translation_btn_${mode.name.lowercase()}"),
                    contentPadding = ButtonDefaults.TextButtonContentPadding
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // SCRIPTURE TEXT LIST
        if (verses.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ምዕራፉ አልተጫነም (ምዕራፍ 1 ን ይምረጡ)",
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(verses, key = { it.id }) { verse ->
                    VerseItemCard(
                        verse = verse,
                        translationMode = translationMode,
                        onVerseTap = { activeVerseForStudy = verse },
                        onBookmarkTap = { viewModel.toggleBookmark(verse) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Interactive Study Sheets overlay
    activeVerseForStudy?.let { verse ->
        StudyBottomSheetOverlay(
            verse = verse,
            viewModel = viewModel,
            onDismiss = { activeVerseForStudy = null }
        )
    }
}

@Composable
fun VerseItemCard(
    verse: BibleVerse,
    translationMode: TranslationMode,
    onVerseTap: () -> Unit,
    onBookmarkTap: () -> Unit
) {
    val highlightBg = when (verse.highlightColor) {
        "yellow" -> Color(0xFFFFF2AC).copy(alpha = 0.25f)
        "green" -> Color(0xFFC0FCD9).copy(alpha = 0.25f)
        "pink" -> Color(0xFFFFD1DF).copy(alpha = 0.25f)
        "cyan" -> Color(0xFFD0F4FF).copy(alpha = 0.25f)
        else -> Color.Transparent
    }

    val borderStroke = if (verse.highlightColor != null) {
        val strokeColor = when (verse.highlightColor) {
            "yellow" -> Color(0xFFFFD54F)
            "green" -> Color(0xFF4CAF50)
            "pink" -> Color(0xFFE91E63)
            "cyan" -> Color(0xFF00BCD4)
            else -> MaterialTheme.colorScheme.primary
        }
        androidx.compose.foundation.BorderStroke(1.dp, strokeColor.copy(alpha = 0.5f))
    } else {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerseTap() }
            .testTag("verse_card_${verse.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (verse.highlightColor != null) highlightBg else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (verse.highlightColor != null) 1.dp else 0.dp),
        shape = RoundedCornerShape(14.dp),
        border = borderStroke ?: borderStroke()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Verse Number Circle Badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (verse.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = verse.verseNumber.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (verse.isBookmarked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Dual Language Verse Texts
            Column(modifier = Modifier.weight(1f)) {
                // Amharic Version (Primary Text)
                if (translationMode == TranslationMode.AMHARIC_ONLY || translationMode == TranslationMode.BILINGUAL) {
                    Text(
                        text = verse.amharicText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                if (translationMode == TranslationMode.BILINGUAL) {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // English Version (Secondary Text)
                if (translationMode == TranslationMode.ENGLISH_ONLY || translationMode == TranslationMode.BILINGUAL) {
                    Text(
                        text = verse.englishText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Quick Bookmark Icon
            IconButton(
                onClick = onBookmarkTap,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Top)
                    .testTag("bookmark_icon_btn_${verse.id}")
            ) {
                Icon(
                    imageVector = if (verse.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (verse.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// --- STUDY QUICK PANEL SHEET (CUSTOM OVERLAY FOR VERSE ACTIONS) ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyBottomSheetOverlay(
    verse: BibleVerse,
    viewModel: BibleViewModel,
    onDismiss: () -> Unit
) {
    var noteInput by remember { mutableStateOf("") }
    val notesFlow by remember(verse) { mutableStateOf(viewModel.allNotes) }
    val notesList by notesFlow.collectAsState()

    // Filter notes specifically for this verse
    val verseNotes = notesList.filter {
        it.bookName == verse.bookName && it.chapterNumber == verse.chapterNumber && it.verseNumber == verse.verseNumber
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }
            .testTag("study_overlay_root"),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) { }
                .testTag("study_overlay_sheet"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            border = borderStroke()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with verse citation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${verse.bookName} ${verse.chapterNumber}:${verse.verseNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_study_overlay")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Selected Verse Scripture Preview
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = verse.amharicText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = verse.englishText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Accent Highlighting Colors Panel
                Text(
                    text = "የቁጥር ቀለም ምልክት (Verse Highlight Color)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val highlightColors = listOf(
                        Pair("yellow", Color(0xFFFFD54F)),
                        Pair("green", Color(0xFF4CAF50)),
                        Pair("pink", Color(0xFFE91E63)),
                        Pair("cyan", Color(0xFF00BCD4))
                    )

                    highlightColors.forEach { (colorName, colorValue) ->
                        val isSelected = verse.highlightColor == colorName
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colorValue)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.setHighlight(
                                        verse,
                                        if (isSelected) null else colorName
                                    )
                                }
                                .testTag("highlight_picker_$colorName"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Clear Highlight Color Option
                    if (verse.highlightColor != null) {
                        IconButton(
                            onClick = { viewModel.setHighlight(verse, null) },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("clear_highlight")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear Highlight",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Add Study Notes Section
                Text(
                    text = "የጥናት ማስታወሻ ይጻፉ (Add Study Note)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    placeholder = { Text("ማስታወሻዎችን እዚህ ይጻፉ... (Write study notes here...)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_input_field"),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        viewModel.addNote(verse, noteInput)
                        noteInput = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("save_note_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ማስታወሻ አስቀምጥ (Save Note)", fontWeight = FontWeight.Bold)
                }

                // Active Study notes list for this verse
                if (verseNotes.isNotEmpty()) {
                    Text(
                        text = "የተቀመጡ ማስታወሻዎች (Saved Notes for this Verse)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    verseNotes.forEach { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = note.noteText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.deleteNote(note.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Note",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
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


// --- 2. SCRIPTURE SEARCH SCREEN ---
@Composable
fun SearchScreen(viewModel: BibleViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "ቃላትን መፈለጊያ (Search Engine)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Enter any keyword in Amharic or English (e.g. 'ብርሃን', 'shepherd', 'ፍቅር') to index across all preloaded scriptures instantly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            placeholder = { Text("መጽሐፍ ቅዱስን ፈልግ (Search scriptures...)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bible_search_input_field"),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )

        // Live Search Results Counter Label
        if (searchQuery.trim().isNotEmpty()) {
            Text(
                text = "የተገኙ ውጤቶች: ${searchResults.size} ምዕራፎች (Found ${searchResults.size} verses)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Live Search Results List
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (searchQuery.trim().isEmpty()) "ለመፈለግ ቃል ያስገቡ (Enter keyword to search)" else "ምንም ውጤት አልተገኘም (No matches found)",
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults, key = { "search_${it.id}" }) { verse ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_hit_${verse.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = borderStroke(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${verse.bookName} (${verse.bookNameEn}) ${verse.chapterNumber}:${verse.verseNumber}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                // Simple helper jump button
                                Button(
                                    onClick = {
                                        viewModel.selectBook(verse.bookName)
                                        viewModel.selectChapter(verse.chapterNumber)
                                        // Jump to read tab is handled by active tab selection usually, we just guide selection
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.secondary
                                    ),
                                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("ክፈት (Open)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = verse.amharicText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = verse.englishText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


// --- 3. STUDY LIBRARY SCREEN (Bookmarks, Notes) ---
@Composable
fun StudyLibraryScreen(viewModel: BibleViewModel) {
    val bookmarks by viewModel.bookmarkedVerses.collectAsState()
    val notes by viewModel.allNotes.collectAsState()

    var activeStudySection by remember { mutableStateOf(0) } // 0 = Bookmarks, 1 = Personal Notes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "የእኔ ጥናት ማዕከል (Study Library)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Custom stylized tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { activeStudySection = 0 },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("study_tab_bookmarks"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeStudySection == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeStudySection == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("ዕልባቶች (${bookmarks.size})", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { activeStudySection = 1 },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("study_tab_notes"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeStudySection == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeStudySection == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("ማስታወሻዎች (${notes.size})", fontWeight = FontWeight.Bold)
            }
        }

        if (activeStudySection == 0) {
            // BOOKMARKS LIST
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "ምንም ዕልባት የለም (No Bookmarks saved yet)",
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(bookmarks, key = { "bookmark_${it.id}" }) { verse ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = borderStroke(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${verse.bookName} ${verse.chapterNumber}:${verse.verseNumber}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { viewModel.toggleBookmark(verse) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Bookmark,
                                            contentDescription = "Unbookmark",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = verse.amharicText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = verse.englishText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // NOTES LIST
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "ምንም ማስታወሻ የለም (No Study Notes written yet)",
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notes, key = { "note_${it.id}" }) { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = borderStroke(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${note.bookName} ${note.chapterNumber}:${note.verseNumber}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteNote(note.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Note",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = note.noteText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- 4. SETTINGS & TRIBUTE INFO SCREEN ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: BibleViewModel) {
    val stateHolder = usePreferences
    val currentPrefs = stateHolder.preferences

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main title
        Text(
            text = "ቅንብሮች እና መረጃ (Settings & Info)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // --- developer info tribute panel ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("developer_tribute_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = borderStroke()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About This Edition",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = "The Amharic Bible 2018 New Edition represents an exquisite, high-quality translation of scriptures into modern Amharic Ge'ez script, dedicated to providing offline accessibility and search capabilities for readers worldwide.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
                Text(
                    text = "Developed with dedication by Samson Terefe in 2018.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // --- Core system preference managers ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("preferences_settings_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(20.dp),
            border = borderStroke()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Theme & Sizing Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // App Theme Selector
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppTheme.values().forEach { theme ->
                        val isSelected = currentPrefs.theme == theme
                        val buttonColors = if (isSelected) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { stateHolder.setTheme(theme) },
                            colors = buttonColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("theme_btn_${theme.name.lowercase()}"),
                            contentPadding = ButtonDefaults.TextButtonContentPadding
                        ) {
                            Text(
                                text = theme.displayName,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Accent Color Selector
                Text(
                    text = "App Color Accent Option",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccentColor.values().forEach { colorOption ->
                        val isSelected = currentPrefs.accentColor == colorOption
                        val circleColor = if (MaterialTheme.colorScheme.primary == colorOption.lightPrimary || !currentPrefs.theme.name.contains("LIGHT")) {
                            colorOption.darkPrimary
                        } else {
                            colorOption.lightPrimary
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(circleColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { stateHolder.setAccentColor(colorOption) }
                                .testTag("accent_color_${colorOption.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (circleColor == Color(0xFFFFB951)) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Font Scale Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Global Font Sizing Scale",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${(currentPrefs.fontSizeMultiplier * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Slider(
                    value = currentPrefs.fontSizeMultiplier,
                    onValueChange = { stateHolder.setFontSizeMultiplier(it) },
                    valueRange = 0.8f..1.4f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("font_scale_slider")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Username Input Field
                Text(
                    text = "Study Profile Name",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = currentPrefs.username,
                    onValueChange = { stateHolder.setUsername(it) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Notifications Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { stateHolder.setNotificationsEnabled(!currentPrefs.notificationsEnabled) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Reading Reminders",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Receive daily morning and evening scripture reminders.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = currentPrefs.notificationsEnabled,
                        onCheckedChange = { stateHolder.setNotificationsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("notifications_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


// --- COMMON HELPERS ---

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
)
