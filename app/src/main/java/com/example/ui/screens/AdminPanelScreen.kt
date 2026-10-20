package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tutor.AppLanguage
import com.example.tutor.TutorUser
import com.example.tutor.TutorViewModel
import com.example.ui.components.dialPhone
import com.example.ui.theme.AnnaBlue
import com.example.ui.theme.AnnaEmerald
import com.example.ui.theme.AnnaGold
import com.example.ui.theme.AnnaNavy
import com.example.ui.theme.AnnaRose

@Composable
fun AdminPanelScreen(
    viewModel: TutorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val tutorsCount = allUsers.count { it.isTutor }
    val studentsCount = allUsers.size - tutorsCount

    var selectedViewMode by remember { mutableStateOf(0) } // 0 = Interactive Cards, 1 = Raw Telegram /users Text
    var userToDelete by remember { mutableStateOf<TutorUser?>(null) }

    val adminMarkdown = remember(allUsers) {
        viewModel.getAdminMarkdown()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_panel_screen")
    ) {
        // Summary Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AnnaNavy)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "የአድሚን ፓነል (Admin Panel) 📋" else "Admin Dashboard 📋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Command: /users",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = AnnaGold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AnnaBlue
                    ) {
                        Text(
                            text = "SQLite: anna_tutor.db",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBadge(
                        label = if (language == AppLanguage.AMHARIC) "ጠቅላላ ተጠቃሚ" else "Total Users",
                        count = allUsers.size.toString(),
                        color = AnnaGold,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBadge(
                        label = if (language == AppLanguage.AMHARIC) "አስጠኚዎች" else "Tutors",
                        count = tutorsCount.toString(),
                        color = AnnaEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBadge(
                        label = if (language == AppLanguage.AMHARIC) "ተማሪዎች" else "Students",
                        count = studentsCount.toString(),
                        color = Color(0xFF60A5FA),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Action Buttons Row: Copy Markdown & Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("AnnA Tutor Users", adminMarkdown)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        context,
                        if (language == AppLanguage.AMHARIC) "የተጠቃሚዎች ዝርዝር ወደ Clipboard ተቀድቷል!" else "Copied /users output to clipboard!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnnaBlue),
                modifier = Modifier
                    .weight(1f)
                    .testTag("copy_markdown_button")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (language == AppLanguage.AMHARIC) "ዝርዝሩን ቅዳ" else "Copy /users")
            }

            OutlinedButton(
                onClick = {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, adminMarkdown)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share AnnA Tutor Users via"))
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("share_users_button")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (language == AppLanguage.AMHARIC) "አጋራ" else "Share")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // View Mode Toggle (Interactive List vs Raw Telegram Markdown)
        TabRow(
            selectedTabIndex = selectedViewMode,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Tab(
                selected = selectedViewMode == 0,
                onClick = { selectedViewMode = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.AMHARIC) "ዝርዝር ካርዶች" else "Interactive List")
                    }
                },
                modifier = Modifier.testTag("admin_tab_cards")
            )

            Tab(
                selected = selectedViewMode == 1,
                onClick = { selectedViewMode = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.AMHARIC) "ቦት ፅሁፍ (Bot Raw)" else "Bot Markdown")
                    }
                },
                modifier = Modifier.testTag("admin_tab_raw")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area
        if (selectedViewMode == 0) {
            // Interactive Cards List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(allUsers, key = { _, user -> user.id }) { index, user ->
                    AdminUserCard(
                        index = index + 1,
                        user = user,
                        language = language,
                        onCall = { dialPhone(context, user.phone) },
                        onDelete = { userToDelete = user }
                    )
                }
            }
        } else {
            // Raw Monospace Telegram Bot Text Viewer
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = adminMarkdown,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = {
                Text(if (language == AppLanguage.AMHARIC) "ተጠቃሚን ሰርዝ" else "Delete User")
            },
            text = {
                Text(
                    if (language == AppLanguage.AMHARIC) {
                        "እርግጠኛ ነዎት ${user.fullName} (${user.phone}) ከዳታቤዝ ውስጥ ይሰረዝ?"
                    } else {
                        "Are you sure you want to delete ${user.fullName} (${user.phone}) from the database?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUser(user)
                        userToDelete = null
                        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(if (language == AppLanguage.AMHARIC) "ሰርዝ" else "Delete", color = AnnaRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text(if (language == AppLanguage.AMHARIC) "ተመለስ" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricBadge(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun AdminUserCard(
    index: Int,
    user: TutorUser,
    language: AppLanguage,
    onCall: () -> Unit,
    onDelete: () -> Unit
) {
    val isTutor = user.isTutor

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index number pill
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AnnaNavy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📞 ${user.phone}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AnnaBlue
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isTutor) AnnaEmerald.copy(alpha = 0.15f) else AnnaGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = user.role,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTutor) AnnaEmerald else AnnaNavy,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Quick Call
            IconButton(
                onClick = onCall,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    tint = AnnaEmerald,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Delete
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AnnaRose,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
