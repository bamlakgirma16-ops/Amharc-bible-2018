package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.tutor.AppLanguage
import com.example.tutor.TutorProfile
import com.example.tutor.TutorProfilesViewModel
import com.example.tutor.TutorSortOption
import com.example.tutor.TutorUser
import com.example.tutor.TutorViewModel
import com.example.ui.components.TutorCard
import com.example.ui.components.TutorDetailDialog
import com.example.ui.components.TutorProfileCard
import com.example.ui.components.TutorProfileDetailDialog
import com.example.ui.theme.AnnaBlue
import com.example.ui.theme.AnnaEmerald
import com.example.ui.theme.AnnaGold
import com.example.ui.theme.AnnaNavy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    viewModel: TutorViewModel,
    profilesViewModel: TutorProfilesViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val studentRequests by viewModel.studentRequests.collectAsState()
    val selectedTabFilter by viewModel.selectedTabFilter.collectAsState()

    // Tutor profiles state
    val filteredTutors by profilesViewModel.filteredTutors.collectAsState()
    val searchQuery by profilesViewModel.searchQuery.collectAsState()
    val selectedExpertise by profilesViewModel.selectedExpertise.collectAsState()
    val onlyAvailable by profilesViewModel.onlyAvailable.collectAsState()
    val maxHourlyRate by profilesViewModel.maxHourlyRate.collectAsState()
    val sortOption by profilesViewModel.sortOption.collectAsState()

    var showFilterPanel by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    var selectedTutorProfileForDialog by remember { mutableStateOf<TutorProfile?>(null) }
    var selectedStudentUserForDialog by remember { mutableStateOf<TutorUser?>(null) }

    val expertiseList = remember {
        listOf(
            "All" to "ሁሉም (All)",
            "Mathematics" to "ሒሳብ (Math)",
            "Physics" to "ፊዚክስ (Physics)",
            "Chemistry" to "ኬሚስትሪ (Chem)",
            "Biology" to "ባዮሎጂ (Bio)",
            "English" to "እንግሊዝኛ (English)",
            "Coding" to "ኮዲንግ (Coding)",
            "Amharic" to "አማርኛ (Amharic)"
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Banner with Educational Graphic
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_anna_banner),
                        contentDescription = "AnnA Tutor Classroom Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        AnnaNavy.copy(alpha = 0.88f)
                                    ),
                                    startY = 50f
                                )
                            )
                    )

                    // Text overlay on banner
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "እንኳን ወደ AnnA Tutor በደህና መጡ! 🎓" else "Welcome to AnnA Tutor! 🎓",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == AppLanguage.AMHARIC) {
                                "ብቃት ያላቸውን አስጠኚዎች በሙያ፣ በሰዓት ክፍያ እና በልምድ ይፈልጉ"
                            } else {
                                "Browse qualified tutors by expertise, hourly rates & verified bios"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = AnnaGold
                        )
                    }
                }
            }
        }

        // 2. Tab Row: Tutors (አስጠኚዎች) vs Student Requests (የተማሪ ጥያቄዎች)
        item {
            val tabs = listOf(
                "Tutors" to (if (language == AppLanguage.AMHARIC) "አስጠኚዎች (Tutors)" else "Tutor Profiles"),
                "Students" to (if (language == AppLanguage.AMHARIC) "የተማሪ ጥያቄዎች" else "Student Requests")
            )
            val selectedTabIndex = if (selectedTabFilter == "Students") 1 else 0

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AnnaBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, (key, label) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.selectedTabFilter.value = key },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) AnnaBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_$key")
                    )
                }
            }
        }

        // 3. Search Bar with Filter and Sort icons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        profilesViewModel.searchQuery.value = it
                        viewModel.searchQuery.value = it
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_input"),
                    placeholder = {
                        Text(
                            if (language == AppLanguage.AMHARIC) "በስም፣ በሙያ ወይም በሰዓት ክፍያ ፈልግ..." else "Search by name, expertise, bio...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = AnnaBlue)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                profilesViewModel.searchQuery.value = ""
                                viewModel.searchQuery.value = ""
                            }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnnaBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Toggle Filter Panel
                IconButton(
                    onClick = { showFilterPanel = !showFilterPanel },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (showFilterPanel) AnnaBlue else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("toggle_filter_panel")
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = if (showFilterPanel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sort Dropdown
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("sort_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort",
                            tint = AnnaBlue
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        TutorSortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (language == AppLanguage.AMHARIC) option.labelAmharic else option.labelEnglish,
                                        fontWeight = if (sortOption == option) FontWeight.Bold else FontWeight.Normal,
                                        color = if (sortOption == option) AnnaBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    profilesViewModel.sortOption.value = option
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // 4. Expandable Filter Panel: Hourly Rate & Availability Toggle
        item {
            AnimatedVisibility(visible = showFilterPanel) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Hourly Rate Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.AMHARIC) "ከፍተኛ የሰዓት ክፍያ (Max Hourly Rate):" else "Max Hourly Rate:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${maxHourlyRate.toInt()} ETB/hr",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AnnaBlue
                            )
                        }

                        Slider(
                            value = maxHourlyRate.toFloat(),
                            onValueChange = { profilesViewModel.maxHourlyRate.value = it.toDouble() },
                            valueRange = 150f..1000f,
                            steps = 17,
                            colors = SliderDefaults.colors(
                                thumbColor = AnnaBlue,
                                activeTrackColor = AnnaBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Availability Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.AMHARIC) "ክፍት የሆኑ አስጠኚዎች ብቻ" else "Available Tutors Only",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (language == AppLanguage.AMHARIC) "በአሁን ሰዓት አዳዲስ ተማሪዎችን የሚቀበሉ" else "Accepting new students right now",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = onlyAvailable,
                                onCheckedChange = { profilesViewModel.onlyAvailable.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AnnaEmerald,
                                    checkedTrackColor = AnnaEmerald.copy(alpha = 0.5f)
                                )
                            )
                        }

                        // Reset button
                        TextButton(
                            onClick = {
                                profilesViewModel.maxHourlyRate.value = 1000.0
                                profilesViewModel.onlyAvailable.value = false
                                profilesViewModel.selectedExpertise.value = "All"
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (language == AppLanguage.AMHARIC) "አጣራውን መልስ (Reset)" else "Reset Filters", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5. Expertise Filter Chips (Horizontal Scroll)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                expertiseList.forEach { (key, label) ->
                    val isSelected = selectedExpertise == key
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            profilesViewModel.selectedExpertise.value = key
                        },
                        label = {
                            Text(
                                text = if (language == AppLanguage.AMHARIC) label else key,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnnaBlue,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("expertise_chip_$key")
                    )
                }
            }
        }

        // 6. Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedTabFilter == "Students") {
                        if (language == AppLanguage.AMHARIC) "የተማሪ ጥያቄዎች ዝርዝር (${studentRequests.size})" else "Student Requests (${studentRequests.size})"
                    } else {
                        if (language == AppLanguage.AMHARIC) "የተገኙ አስጠኚዎች (${filteredTutors.size})" else "Available Tutors (${filteredTutors.size})"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (language == AppLanguage.AMHARIC) "ደርድር፡ ${sortOption.labelAmharic}" else "Sort: ${sortOption.labelEnglish}",
                    fontSize = 11.sp,
                    color = AnnaBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 7. List of Cards
        if (selectedTabFilter == "Students") {
            // Student requests
            if (studentRequests.isEmpty()) {
                item {
                    EmptyStateCard(language)
                }
            } else {
                items(studentRequests, key = { it.id }) { user ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        TutorCard(
                            user = user,
                            language = language,
                            onCardClick = { selectedStudentUserForDialog = user }
                        )
                    }
                }
            }
        } else {
            // Tutor Profiles (storing expertise, hourly rate, and bio)
            if (filteredTutors.isEmpty()) {
                item {
                    EmptyStateCard(language)
                }
            } else {
                items(filteredTutors, key = { it.id }) { tutor ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        TutorProfileCard(
                            profile = tutor,
                            language = language,
                            onCardClick = { selectedTutorProfileForDialog = tutor }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialogs
    selectedTutorProfileForDialog?.let { profile ->
        TutorProfileDetailDialog(
            profile = profile,
            language = language,
            onDismiss = { selectedTutorProfileForDialog = null }
        )
    }

    selectedStudentUserForDialog?.let { user ->
        TutorDetailDialog(
            user = user,
            language = language,
            onDismiss = { selectedStudentUserForDialog = null }
        )
    }
}

@Composable
fun EmptyStateCard(language: AppLanguage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PersonSearch,
                contentDescription = null,
                tint = AnnaBlue,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (language == AppLanguage.AMHARIC) "ምንም የተገኘ አስጠኚ የለም" else "No tutors match your criteria",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == AppLanguage.AMHARIC) {
                    "እባክዎን የሰዓት ክፍያውን፣ የሙያ መስኩን ወይም የፍለጋ ቃሉን ይቀይሩ"
                } else {
                    "Try increasing the hourly rate filter or selecting another subject"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
