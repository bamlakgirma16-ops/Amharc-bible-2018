package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tutor.AppLanguage
import com.example.tutor.TutorProfilesViewModel
import com.example.tutor.TutorViewModel
import com.example.ui.theme.AnnaBlue
import com.example.ui.theme.AnnaEmerald
import com.example.ui.theme.AnnaGold
import com.example.ui.theme.AnnaNavy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistrationFormScreen(
    viewModel: TutorViewModel,
    profilesViewModel: TutorProfilesViewModel,
    onSuccessNav: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()

    var selectedRole by remember { mutableStateOf("አስጠኚ (Tutor)") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var customSubject by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("Grade 9 - 12") }
    var selectedLocation by remember { mutableStateOf("Bole, Addis Ababa") }
    var rate by remember { mutableStateOf("300 ETB/hr") }
    var selectedMode by remember { mutableStateOf("Home & Online") }
    var bio by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    val selectedSubjects = remember { mutableStateListOf("Mathematics (ሒሳብ)", "Physics (ፊዚክስ)") }

    val commonSubjects = listOf(
        "Mathematics (ሒሳብ)",
        "Physics (ፊዚክስ)",
        "Chemistry (ኬሚስትሪ)",
        "Biology (ባዮሎጂ)",
        "English (እንግሊዝኛ)",
        "Amharic (አማርኛ)",
        "Coding (ኮዲንግ)",
        "General Science",
        "Social Studies"
    )

    val grades = listOf(
        "Grade 1 - 4 (Primary)",
        "Grade 5 - 8 (Middle)",
        "Grade 9 - 12 (High School)",
        "University / College",
        "Adult / Diaspora Learning"
    )

    val locations = listOf(
        "Bole (ቦሌ)",
        "Kazanchis (ካዛንቺስ)",
        "Piassa (ፒያሳ)",
        "Sarbet (ሳርቤት)",
        "CMC (ሲኤምሲ)",
        "Yeka (የካ)",
        "Gerji (ገርጂ)",
        "Megenagna (መገናኛ)",
        "Hawassa",
        "Bahir Dar"
    )

    val modes = listOf(
        "Home & Online",
        "Home Tutoring",
        "Online Only"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("registration_form_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AnnaNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "የ AnnA Tutor ምዝገባ ቅጽ 📝" else "AnnA Tutor Registration Form 📝",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.AMHARIC) {
                            "መረጃዎን በማዕከላዊው SQLite ዳታቤዝ ውስጥ ይመዝግቡ። በቴሌግራም ቦቱም ሆነ በሞባይል መተግበሪያው ላይ ወዲያውኑ ይታያል።"
                        } else {
                            "Register your profile in the central SQLite database. Directly synced with the Telegram bot."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = AnnaGold
                    )
                }
            }
        }

        // Success Card if just submitted
        item {
            AnimatedVisibility(visible = isSubmitted) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AnnaEmerald.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = AnnaEmerald,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.AMHARIC) "ምዝገባዎ በተሳካ ሁኔታ ተጠናቋል! 🎉" else "Registered Successfully! 🎉",
                                fontWeight = FontWeight.Bold,
                                color = AnnaEmerald
                            )
                            Text(
                                text = if (language == AppLanguage.AMHARIC) {
                                    "መረጃዎ ወደ ዳታቤዝ ተቀምጧል። በአድሚን ፓነል ውስጥ ማየት ይችላሉ።"
                                } else {
                                    "Profile saved to SQLite database. Available in directory and admin panel."
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 1. Role Selection Tab
        item {
            Text(
                text = if (language == AppLanguage.AMHARIC) "የምዝገባ ሚና (Choose Role):" else "Select Role:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            val roles = listOf("አስጠኚ (Tutor)", "ተማሪ/ወላጅ (Student/Parent)")
            val selectedIndex = if (selectedRole.contains("አስጠኚ")) 0 else 1

            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = AnnaBlue
                    )
                }
            ) {
                roles.forEachIndexed { index, role ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { selectedRole = role },
                        text = {
                            Text(
                                text = role,
                                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedIndex == index) AnnaBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("form_role_tab_$index")
                    )
                }
            }
        }

        // 2. Full Name
        item {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(if (language == AppLanguage.AMHARIC) "ሙሉ ስም (Full Name) *" else "Full Name *") },
                placeholder = { Text("ለምሳሌ፡ አበበ በቀለ (Abebe Bekele)") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AnnaBlue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("form_input_name"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 3. Phone Number
        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(if (language == AppLanguage.AMHARIC) "ስልክ ቁጥር (Phone Number) *" else "Phone Number *") },
                placeholder = { Text("09... ወይም 07...") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AnnaEmerald) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("form_input_phone"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 4. Subjects
        item {
            Text(
                text = if (language == AppLanguage.AMHARIC) "የትምህርት አይነቶች (Subjects):" else "Subjects:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonSubjects.forEach { sub ->
                    val isChecked = selectedSubjects.contains(sub)
                    FilterChip(
                        selected = isChecked,
                        onClick = {
                            if (isChecked) selectedSubjects.remove(sub) else selectedSubjects.add(sub)
                        },
                        label = { Text(text = sub, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnnaBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 5. Grade Level
        item {
            Text(
                text = if (language == AppLanguage.AMHARIC) "የትምህርት ደረጃ (Grade Level):" else "Target Grade:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grades.forEach { g ->
                    FilterChip(
                        selected = selectedGrade == g,
                        onClick = { selectedGrade = g },
                        label = { Text(text = g, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnnaEmerald,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 6. Location
        item {
            Text(
                text = if (language == AppLanguage.AMHARIC) "አድራሻ / አካባቢ (Location):" else "Location:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                locations.forEach { loc ->
                    FilterChip(
                        selected = selectedLocation == loc,
                        onClick = { selectedLocation = loc },
                        label = { Text(text = loc, fontSize = 12.sp) }
                    )
                }
            }
        }

        // 7. Rate & Instruction Mode
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "ዋጋ (Rate)" else "Rate / Budget") },
                    leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null, tint = AnnaGold) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = selectedMode,
                    onValueChange = { selectedMode = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "ዘዴ (Mode)" else "Mode") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // 8. Bio / Additional Notes
        item {
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text(if (language == AppLanguage.AMHARIC) "ተጨማሪ ማብራሪያ (Bio / Experience / Need)" else "Bio / Experience / Details") },
                placeholder = { Text("የትምህርት ልምድ፣ ምቹ ሰዓቶች፣ ወዘተ...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (fullName.isBlank() || phone.isBlank()) {
                        Toast.makeText(
                            context,
                            if (language == AppLanguage.AMHARIC) "እባክዎን ስም እና ስልክ ቁጥር ያስገቡ!" else "Please provide both name and phone number!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val subjectsString = if (selectedSubjects.isNotEmpty()) {
                        selectedSubjects.joinToString(", ")
                    } else {
                        "General Education"
                    }

                    viewModel.registerUserFromForm(
                        fullName = fullName.trim(),
                        phone = phone.trim(),
                        role = selectedRole,
                        subjects = subjectsString,
                        gradeLevel = selectedGrade,
                        location = selectedLocation,
                        bio = bio.ifBlank { "Registered through AnnA Tutor Mobile Portal" },
                        rate = rate,
                        mode = selectedMode,
                        onSuccess = {
                            if (selectedRole.contains("አስጠኚ") || selectedRole.contains("Tutor")) {
                                val parsedRate = rate.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 300.0
                                profilesViewModel.addTutorProfile(
                                    name = fullName.trim(),
                                    phone = phone.trim(),
                                    expertise = subjectsString,
                                    hourlyRate = parsedRate,
                                    bio = bio.ifBlank { "Experienced and dedicated tutor offering personalized lessons." },
                                    location = selectedLocation,
                                    education = selectedGrade,
                                    experienceYears = 3,
                                    isAvailable = true,
                                    teachingMode = selectedMode
                                )
                            }
                            isSubmitted = true
                            Toast.makeText(
                                context,
                                if (language == AppLanguage.AMHARIC) "ምዝገባዎ በተሳካ ሁኔታ ተጠናቋል!" else "Successfully registered in central SQLite database!",
                                Toast.LENGTH_LONG
                            ).show()
                            onSuccessNav()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("form_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AnnaBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "✅ ምዝገባውን አጠናቅቅ (Submit)" else "✅ Complete Registration (Submit)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
