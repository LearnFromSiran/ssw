package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.db.AppDatabase
import com.example.db.AppRepository
import com.example.db.UserProfile
import com.example.model.*
import com.example.ui.SSWViewModel
import com.example.ui.SSWViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            tts = TextToSpeech(this, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Initialize state, Room database, and repository
        val database = AppDatabase.getDatabase(this)
        val repository = AppRepository(database.userDao())

        setContent {
            MyApplicationTheme {
                val sswViewModel: SSWViewModel = viewModel(
                    factory = SSWViewModelFactory(repository)
                )

                SSWAppMainScreen(
                    viewModel = sswViewModel,
                    onSpeak = { text, isJapanese ->
                        speak(text, isJapanese)
                    }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.JAPANESE
        }
    }

    private fun speak(text: String, isJapanese: Boolean) {
        tts?.let {
            try {
                it.language = if (isJapanese) Locale.JAPANESE else Locale.ENGLISH
                it.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}

// Custom Warm Palette representing Japanese textbook colors: Coral, Cream, and Soft Pink
val StartGradient = Color(0xFFFFECEF)
val EndGradient = Color(0xFFFFF6F0)
val ThemePink = Color(0xFFFF5E7E)
val ThemePeach = Color(0xFFFF9E7D)
val DarkText = Color(0xFF332D2D)
val SoftGray = Color(0xFFF7F5F4)
val DarkPurpleAccent = Color(0xFF4A3E3D)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SSWAppMainScreen(viewModel: SSWViewModel, onSpeak: (String, Boolean) -> Unit = { _, _ -> }) {
    val userProfileState by viewModel.userProfile.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf("home") }

    // If profile is empty, show the beautiful Welcome & Language setup screen first
    if (userProfileState == null) {
        WelcomeSetupScreen(onSaveProfile = { name, language ->
            viewModel.saveUserProfile(name, language)
        })
    } else {
        val user = userProfileState!!
        Scaffold(
            bottomBar = {
                SSWBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("app_scaffold")
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(StartGradient, EndGradient)
                        )
                    )
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    "home" -> StudyDashboardScreen(viewModel = viewModel, user = user, onNavigateToTab = { currentTab = it }, onSpeak = onSpeak)
                    "vocab" -> VocabStudyScreen(viewModel = viewModel, onNavigateToTab = { currentTab = it }, onSpeak = onSpeak)
                    "quiz" -> QuizModuleScreen(viewModel = viewModel)
                    "profile" -> ProfileSettingsScreen(viewModel = viewModel, user = user)
                }
            }
        }
    }
}

// --- Tab Item representation ---
data class BottomNavItem(val id: String, val label: String, val iconSelected: ImageVector, val iconUnselected: ImageVector)

@Composable
fun SSWBottomNavigationBar(currentTab: String, onTabSelected: (String) -> Unit) {
    val items = listOf(
        BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("vocab", "Vocab", Icons.Filled.List, Icons.Outlined.List),
        BottomNavItem("quiz", "Quiz", Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow),
        BottomNavItem("profile", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = Color.White.copy(alpha = 0.95f),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .testTag("bottom_nav_bar")
    ) {
        items.forEach { item ->
            val selected = currentTab == item.id
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.id) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.iconSelected else item.iconUnselected,
                        contentDescription = item.label,
                        tint = if (selected) ThemePink else DarkPurpleAccent.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) ThemePink else DarkPurpleAccent.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = ThemePink.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("nav_item_${item.id}")
            )
        }
    }
}

// --- welcome / profile launcher setup ---
@Composable
fun WelcomeSetupScreen(onSaveProfile: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("English") }

    val languages = listOf("English", "Filipino/Tagalog", "Nepalese", "Indonesian", "Vietnamese", "Burmese")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StartGradient, EndGradient)
                )
            )
            .padding(24.dp)
            .testTag("welcome_setup_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(24.dp)
                .shadow(1.dp, shape = RoundedCornerShape(24.dp))
        ) {
            // Cute launcher circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(ThemePink, ThemePeach))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "App logo star decoration",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SSW Nursing Care",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Specified Skilled Worker Study Companion",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkPurpleAccent.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                placeholder = { Text("Enter your name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("username_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Language picker
            Text(
                text = "Select output language for AI explanations:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = DarkText,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Simple language chips list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    languages.chunked(3).forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            chunk.forEach { lang ->
                                val isSelected = selectedLang == lang
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ThemePink else SoftGray)
                                        .clickable { selectedLang = lang }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lang,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else DarkPurpleAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val finalName = if (name.trim().isEmpty()) "Aspirant" else name.trim()
                    onSaveProfile(finalName, selectedLang)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ThemePink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_button")
            ) {
                Text(
                    text = "Let's Study!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// --- Home Study Dashboard ---
@Composable
fun StudyDashboardScreen(viewModel: SSWViewModel, user: UserProfile, onNavigateToTab: (String) -> Unit, onSpeak: (String, Boolean) -> Unit = { _, _ -> }) {
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()
    val favoritesSet by viewModel.favoriteWords.collectAsStateWithLifecycle()
    var selectedChapter by remember { mutableStateOf<StudyChapter?>(null) }

    // Body Mechanics Simulator States
    var selectedScenario by remember { mutableStateOf("Transfer Bed ⇆ Wheelchair") }
    var baseOfSupportWide by remember { mutableStateOf(true) }
    var centerOfGravityLow by remember { mutableStateOf(true) }
    var patientDistanceClose by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_column"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Premium App Hero Banner with Custom Illustration
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(24.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_care_banner),
                        contentDescription = "Care House Cherry Blossom Greeting Header Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Beautiful dark gradient mask overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(ThemePink, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "EXAM PREP ACTIVE 🌸",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "SSW Care Study Guide",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Interactive Japanese Core Caregiving Course",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            // Profile Greeting Card with Japanese patterns style
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(colors = listOf(ThemePink, ThemePeach))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Konnichiwa, ${user.name}! 👋",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target status: Japanese 介護 SSW Exam",
                                style = MaterialTheme.typography.bodySmall,
                                color = ThemePeach,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Technical concepts, local simulators and mock test components will automatically translate in ${user.nativeLanguage}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkPurpleAccent.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = SoftGray,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Favorite, contentDescription = "Saved Terms", tint = ThemePink, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Bookmarks",
                                    fontSize = 10.sp,
                                    color = DarkPurpleAccent.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "${favoritesSet.size} words",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DarkText
                                )
                            }
                        }

                        Surface(
                            color = SoftGray,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Star, contentDescription = "Quiz attempts", tint = ThemePeach, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Exam Runs",
                                    fontSize = 10.sp,
                                    color = DarkPurpleAccent.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "${quizHistory.size} times",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DarkText
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Motivational Daily Habit Streak Tracker Widget ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔥", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Weekly Consistency Streak",
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "4 Days Active",
                            fontWeight = FontWeight.Bold,
                            color = ThemePink,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(ThemePink.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Habit Circles Mon-Sun row representation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        val activeDays = listOf(true, true, true, true, false, false, false)

                        days.forEachIndexed { idx, day ->
                            val isActive = activeDays[idx]
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isActive) ThemePink else SoftGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isActive) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = day,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkPurpleAccent.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                                if (isActive) {
                                    Text(
                                        text = day,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ThemePink,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "“Passing the SSW exam is about cumulative daily actions. Perfect consistency guarantees perfect results!”",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkPurpleAccent.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }
        }

        // --- Interactive Safety & Body Mechanics Clinical Lab Simulator (Ch. 3) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "🛡️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Safety & Body Mechanics Lab",
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "Practical Chapter 3: Toggle the postures below to run force mechanics simulation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkPurpleAccent.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Scenario Selection
                    Text(
                        text = "1. Clinical Context Scenario:",
                        fontWeight = FontWeight.Bold,
                        color = DarkPurpleAccent,
                        fontSize = 11.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val scenarios = listOf("Transfer Bed ⇆ Wheelchair", "Relieving Bedspots (Senuki)")
                        scenarios.forEach { sc ->
                            val isChosen = selectedScenario == sc
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) DarkPurpleAccent else SoftGray)
                                    .clickable { selectedScenario = sc }
                                    .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sc,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) Color.White else DarkPurpleAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "2. Adjust Postures and Force Parameters:",
                        fontWeight = FontWeight.Bold,
                        color = DarkPurpleAccent,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Base of support toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Base of Support (Widen feet)", fontSize = 12.sp, color = DarkText)
                        Switch(
                            checked = baseOfSupportWide,
                            onCheckedChange = { baseOfSupportWide = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ThemePink, checkedTrackColor = ThemePink.copy(alpha = 0.2f))
                        )
                    }

                    // Center of gravity toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Center of Gravity (Bend knees)", fontSize = 12.sp, color = DarkText)
                        Switch(
                            checked = centerOfGravityLow,
                            onCheckedChange = { centerOfGravityLow = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ThemePink, checkedTrackColor = ThemePink.copy(alpha = 0.2f))
                        )
                    }

                    // Keep patient close Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Proximity to Patient (Keep close)", fontSize = 12.sp, color = DarkText)
                        Switch(
                            checked = patientDistanceClose,
                            onCheckedChange = { patientDistanceClose = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ThemePink, checkedTrackColor = ThemePink.copy(alpha = 0.2f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live diagnostic feedback
                    val allGood = baseOfSupportWide && centerOfGravityLow && patientDistanceClose
                    val bgDiag = if (allGood) Color(0xFFE8F6EE) else Color(0xFFFFF2F2)
                    val borderDiagColor = if (allGood) Color(0xFF2E8B57) else ThemePink
                    val textDiagColor = if (allGood) Color(0xFF226E40) else Color(0xFFAC2626)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgDiag)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (allGood) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = "Diagnostic icon",
                                    tint = borderDiagColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                defianceFeedbackHeader(allGood, borderDiagColor)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (allGood) {
                                    "Your posture lowers fatigue by utilizing the large muscles of your thighs while stabilizing both the patient and yourself. Ideal for exam performance!"
                                } else {
                                    "Critically high back-injury risk! Adjust your position: " + 
                                    (if (!baseOfSupportWide) "• Widen feet to expand Support Base. " else "") +
                                    (if (!centerOfGravityLow) "• Lower center of gravity by bending knees. " else "") +
                                    (if (!patientDistanceClose) "• Bring caregiver and user bodies and gravity lines close to minimize torso twist." else "")
                                },
                                fontSize = 11.sp,
                                color = textDiagColor,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigator Header
        item {
            Text(
                text = "Course Quick Actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Study Vocab button
                Card(
                    colors = CardDefaults.cardColors(containerColor = ThemePink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab("vocab") }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.List, contentDescription = "Vocab", tint = Color.White)
                        Text(
                            text = "Flashcards",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Quiz button
                Card(
                    colors = CardDefaults.cardColors(containerColor = ThemePeach),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab("quiz") }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Quiz", tint = Color.White)
                        Text(
                            text = "Take Quiz",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Active Textbook Chapters Header
        item {
            Text(
                text = "Course Study Guide Chapters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }

        if (selectedChapter == null) {
            items(StudyData.chapters) { chapter ->
                val emojiBadge = when(chapter.chapterNumber) {
                    1 -> "⭐"
                    2 -> "⚖️"
                    3 -> "🛡️"
                    4 -> "🩺"
                    else -> "🍲"
                }
                val difficulty = when(chapter.chapterNumber) {
                    1, 2 -> "🟢 Essential"
                    3, 4 -> "🟡 Core Skills"
                    else -> "🔴 Advanced Practice"
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, shape = RoundedCornerShape(16.dp))
                        .clickable { selectedChapter = chapter }
                        .testTag("chapter_card_${chapter.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge representing Part Number
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ThemePink.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emojiBadge,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ch.${chapter.chapterNumber} • ${difficulty}",
                                    fontSize = 11.sp,
                                    color = ThemePeach,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(ThemePink.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "⏱️ 5m read",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ThemePink
                                    )
                                }
                            }
                            Text(
                                text = chapter.title,
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = chapter.part,
                                fontSize = 10.sp,
                                color = DarkPurpleAccent.copy(alpha = 0.6f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "View Details",
                            tint = DarkPurpleAccent.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            // --- Multi-Step Interactive Course Slide Player (M3 Easy Reader Redesign) ---
            item {
                val chapter = selectedChapter!!
                
                // Active slide pagination step (0 to 4)
                var currentSlideIndex by remember(chapter.id) { mutableStateOf(0) }
                
                // Track reading mastery checklist local progress
                val checkedPoints = remember(chapter.id) { mutableStateMapOf<Int, Boolean>() }
                val completedCount = checkedPoints.values.count { it }
                val totalCount = chapter.bulletPoints.size
                val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0F
                val allChecked = totalCount > 0 && completedCount == totalCount

                // Typography & Reading Preferences
                val fontSizes = listOf(14.sp, 17.sp, 20.sp, 24.sp)
                var fontSizeIndex by remember { mutableStateOf(1) } // Default to 17sp
                val readerFontSize = fontSizes[fontSizeIndex]

                var fontFamilyName by remember { mutableStateOf("Sans") } // Sans, Serif, Mono
                val readerFontFamily = when(fontFamilyName) {
                    "Serif" -> androidx.compose.ui.text.font.FontFamily.Serif
                    "Mono" -> androidx.compose.ui.text.font.FontFamily.Monospace
                    else -> androidx.compose.ui.text.font.FontFamily.SansSerif
                }

                var readerThemeName by remember { mutableStateOf("Ivory") } // Ivory, Light, Dark
                val (readerBgColor, readerTextColor, readerAccentColor, readerContainerColor) = when(readerThemeName) {
                    "Light" -> listOf(Color(0xFFFFFFFF), Color(0xFF1C1C1E), ThemePink, Color(0xFFF2F2F7))
                    "Dark" -> listOf(Color(0xFF1E1C1F), Color(0xFFF3EDF7), ThemePeach, Color(0xFF2C282D))
                    else -> listOf(Color(0xFFFCF6EA), Color(0xFF2C1E14), Color(0xFFE2A44B), Color(0xFFF4EAD2)) // Ivory Cozy Book Paper
                }

                // Interactive local chapter exam state
                var selectedOptionIndex by remember(chapter.id) { mutableStateOf<Int?>(null) }
                var slideQuizSubmitted by remember(chapter.id) { mutableStateOf(false) }

                val (slideQuestion, slideOptions, slideCorrectIndex, slideExplanation) = when(chapter.chapterNumber) {
                    1 -> listOf(
                        "What is the primary objective of modern Japanese caregiving (介護)?",
                        listOf(
                            "Doing all daily activities for the user to keep them fully resting",
                            "Supporting their autonomy (自立支援) and preserving personal dignity",
                            "Enforcing institutional schedules as fast as possible without talking",
                            "Assuming complete control over their choices for their own safety"
                        ),
                        1,
                        "Excellent! Japanese Caregiving (Kaigo) is founded upon Autonomy Support (自立支援) - helping users do what they can themselves using their available assets - and preserving their human dignity (尊厳保持)."
                    )
                    2 -> listOf(
                        "When communicating with a resident who has severe hearing loss, what is the best practice?",
                        listOf(
                            "Shout directly into their ear in a high-pitch voice",
                            "Positions yourself behind them so they avoid seeing your mouth",
                            "Face them directly at eye level, speak slowly in a lower tone, and let them see your lips",
                            "Stop speaking entirely and communicate solely in written logs"
                        ),
                        2,
                        "Excellent! Facing the user, keeping eye contact, and speaking slowly in a lower voice pitch allows speech aids or visual lip-reading to compensate for age-related hearing loss."
                    )
                    3 -> listOf(
                        "To minimize physical strain and back injury when transferring a resident, you should:",
                        listOf(
                            "Lock your knees, tilt from your waist, and keep the resident's torso at a distance",
                            "Widen your support base, bend your knees to lower your center of gravity, and bring the user close",
                            "Lift entirely with your wrists while aggressively twisting your trunk",
                            "Squeeze your ankles together and pull as fast as possible"
                        ),
                        1,
                        "Excellent! Widening your feet base, bending your knees (lowering center of gravity), and keeping the user's weight close maximizes your skeletal leverage and shifts work to your strong leg/thigh muscles."
                    )
                    4 -> listOf(
                        "During morning facial grooming, you touch the resident's forehead and find it hot. They are shivering and lethargic. What is the immediate correct action?",
                        listOf(
                            "Splash cold water on face to wake them up",
                            "Tell them to walk around more to build up physical heat",
                            "Measure their temperature and immediately report the chills & fever to nursing staff",
                            "Assume they are just tired and leave them to sleep alone"
                        ),
                        2,
                        "Excellent! Sudden chills/shivering accompanied by high skin temperature signals an acute fever onset. Taking vitals immediately and notifying nurse clinicians ensures supreme resident safety."
                    )
                    else -> listOf(
                        "To protect a resident from sudden pressure spikes (Heat Shock) during bath care, you must:",
                        listOf(
                            "Keep dressing rooms cold while setting bath temperatures above 45°C",
                            "Pre-heat the dressing room and keep the bathing airspace comfortable and warm",
                            "Splash ice-cold water over their shoulders first to wake up blood vessels",
                            "Lock them in the room for an hour to adjust to steam humidity"
                        ),
                        1,
                        "Excellent! Heat Shock is triggered by sharp environmental temperature changes. Pre-heating undressing zones and keeping baths at 39°C stabilizes blood pressure and prevents heart/circulatory shocks."
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, shape = RoundedCornerShape(24.dp))
                        .testTag("chapter_detail_view")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        
                        // Header metadata & close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(ThemePink.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎓", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CHAPTER ${chapter.chapterNumber} WORKBOOK",
                                    color = ThemePink,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            IconButton(
                                onClick = { selectedChapter = null },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("close_chapter_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close Reader",
                                    tint = DarkPurpleAccent.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Dots Tracker
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SoftGray, shape = RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val slideNames = listOf("目標", "English", "日本語", "Check", "Exam")
                            slideNames.forEachIndexed { idx, name ->
                                val isActive = currentSlideIndex == idx
                                val isVisited = idx < currentSlideIndex
                                val dotBg = if (isActive) ThemePink else if (isVisited) ThemePeach.copy(alpha = 0.6f) else Color.White
                                val borderStroke = if (isActive) BorderStroke(1.5.dp, ThemePink) else BorderStroke(1.dp, SoftGray)
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 3.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(dotBg)
                                        .clickable { 
                                            currentSlideIndex = idx
                                            // Reset quiz state on changing chapters/slides
                                            if (idx != 4) {
                                                selectedOptionIndex = null
                                                slideQuizSubmitted = false
                                            }
                                        }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isActive) Color.White else DarkPurpleAccent.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // --- Dynamic Book Reader Setting Controls (Visible on Slide 1, 2, 3) ---
                        if (currentSlideIndex in 0..2) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SoftGray.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🛠️ Easy-Reader Controls:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkPurpleAccent)
                                        
                                        // Font Selector
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf("Sans", "Serif", "Mono").forEach { f ->
                                                val sel = fontFamilyName == f
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (sel) ThemePeach else Color.White)
                                                        .clickable { fontFamilyName = f }
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(text = f, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else DarkPurpleAccent)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Font size adjuster
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            IconButton(
                                                onClick = { if (fontSizeIndex > 0) fontSizeIndex-- },
                                                modifier = Modifier.size(24.dp).background(Color.White, CircleShape)
                                            ) {
                                                Text(text = "A-", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DarkPurpleAccent)
                                            }
                                            Text(
                                                text = "${readerFontSize.value.toInt()}sp",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkText
                                            )
                                            IconButton(
                                                onClick = { if (fontSizeIndex < fontSizes.lastIndex) fontSizeIndex++ },
                                                modifier = Modifier.size(24.dp).background(Color.White, CircleShape)
                                            ) {
                                                Text(text = "A+", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DarkPurpleAccent)
                                            }
                                        }

                                        // Theme select circles
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            val themes = listOf("Ivory" to Color(0xFFFCF6EA), "Light" to Color(0xFFFFFFFF), "Dark" to Color(0xFF1E1C1F))
                                            themes.forEach { (tName, tColor) ->
                                                val isSel = readerThemeName == tName
                                                val border = if (isSel) BorderStroke(1.5.dp, ThemePink) else BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clip(CircleShape)
                                                        .background(tColor)
                                                        .clickable { readerThemeName = tName }
                                                        .shadow(1.dp, CircleShape)
                                                        .background(tColor)
                                                        .border(border.width, border.brush, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // --- SLIDE CONTAINER ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(readerBgColor, shape = RoundedCornerShape(16.dp))
                                .border(1.dp, readerTextColor.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            when (currentSlideIndex) {
                                // --- SLIDE 1: GOAL OVERVIEW (目標) ---
                                0 -> {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Unit Goals (学習目標)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = readerTextColor.copy(alpha = 0.6f)
                                            )
                                            
                                            // Title Pronunciation speaker helper
                                            IconButton(
                                                onClick = { onSpeak(chapter.title, true) },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(readerTextColor.copy(alpha = 0.1f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.PlayArrow,
                                                    contentDescription = "Speak Title",
                                                    tint = readerTextColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        Text(
                                            text = chapter.title,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontSize = (readerFontSize.value + 4.sp.value).sp,
                                                fontFamily = readerFontFamily,
                                                color = readerTextColor,
                                                lineHeight = (readerFontSize.value * 1.4).sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = chapter.part,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ThemePeach
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "Clinical Relevance Case:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = readerTextColor.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "Every clinical action is tied directly to safeguarding aged souls. This chapter describes crucial care methods to maintain hygiene, comfort, and physical safety of your clients. Click Next to read details in double-font depth.",
                                            fontSize = (readerFontSize.value - 3.sp.value).sp,
                                            color = readerTextColor.copy(alpha = 0.7f),
                                            lineHeight = (readerFontSize.value * 1.3).sp
                                        )
                                    }
                                }

                                // --- SLIDE 2: ENGLISH CONTENT GUIDE (英語概略) ---
                                1 -> {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "English Reading Guide:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = readerTextColor.copy(alpha = 0.7f)
                                            )
                                            // English Audio Reader
                                            IconButton(
                                                onClick = { onSpeak(chapter.contentEnglish, false) },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(readerTextColor.copy(alpha = 0.1f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.PlayArrow,
                                                    contentDescription = "Speak English",
                                                    tint = readerTextColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = chapter.contentEnglish,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = readerFontSize,
                                                fontFamily = readerFontFamily,
                                                color = readerTextColor,
                                                lineHeight = (readerFontSize.value * 1.5).sp
                                            )
                                        )
                                    }
                                }

                                // --- SLIDE 3: JAPANESE SOURCE TEXT WITH COLLATERAL GLOSSARY ---
                                2 -> {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Japanese Training Text:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = readerTextColor.copy(alpha = 0.7f)
                                            )
                                            // Japanese TTS Audio Reader
                                            IconButton(
                                                onClick = { onSpeak(chapter.contentJapanese, true) },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(readerTextColor.copy(alpha = 0.1f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.PlayArrow,
                                                    contentDescription = "Speak Japanese",
                                                    tint = readerTextColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Japanese Paragraph Text (Slightly larger for Kanji readability)
                                        Text(
                                            text = chapter.contentJapanese,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = (readerFontSize.value + 2.sp.value).sp,
                                                fontFamily = readerFontFamily,
                                                color = readerTextColor,
                                                lineHeight = (readerFontSize.value * 1.6).sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Divider(color = readerTextColor.copy(alpha = 0.15f))
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "💡 Interactive Core Glossary Terminology:",
                                            fontWeight = FontWeight.Bold,
                                            color = readerTextColor,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Tap any word to hear native audio and read full situational usage:",
                                            fontSize = 10.sp,
                                            color = readerTextColor.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )

                                        val matchedTerms = StudyData.vocabularies.filter { v ->
                                            chapter.title.contains(v.word) || 
                                            chapter.contentEnglish.contains(v.translation) ||
                                            chapter.contentJapanese.contains(v.word)
                                        }.take(4)

                                        if (matchedTerms.isEmpty()) {
                                            Text(
                                                text = "No direct vocab matches on this page. Check Vocab tab for more.",
                                                fontSize = 10.sp,
                                                color = readerTextColor.copy(alpha = 0.5f)
                                            )
                                        } else {
                                            matchedTerms.forEach { vocab ->
                                                var expanded by remember { mutableStateOf(false) }
                                                Card(
                                                    colors = CardDefaults.cardColors(containerColor = readerContainerColor),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                        .clickable { expanded = !expanded }
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = "${vocab.word} • ${vocab.pronunciation}",
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 13.sp,
                                                                    color = readerTextColor
                                                                )
                                                                Text(
                                                                    text = "Meaning: ${vocab.translation} (${vocab.romaji})",
                                                                    fontSize = 11.sp,
                                                                    color = ThemePink,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                            
                                                            Row {
                                                                IconButton(
                                                                    onClick = { onSpeak(vocab.word, true) },
                                                                    modifier = Modifier.size(28.dp).background(Color.White, CircleShape)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Filled.PlayArrow,
                                                                        contentDescription = "Speak Word",
                                                                        tint = ThemePink,
                                                                        modifier = Modifier.size(14.dp)
                                                                    )
                                                                }
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Icon(
                                                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                                    contentDescription = "Expand info",
                                                                    tint = readerTextColor.copy(alpha = 0.6f)
                                                                )
                                                            }
                                                        }
                                                        if (expanded) {
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            Text(
                                                                text = "Situation Explanations: ${vocab.explanation}",
                                                                fontSize = 11.sp,
                                                                color = readerTextColor.copy(alpha = 0.8f),
                                                                style = MaterialTheme.typography.bodySmall,
                                                                lineHeight = 14.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // --- SLIDE 4: CHAPTER MASTER CHECKLIST ---
                                3 -> {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Operational Checkpoints:",
                                                fontWeight = FontWeight.Bold,
                                                color = DarkText,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${completedCount}/${totalCount} Completed",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ThemePink
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        LinearProgressIndicator(
                                            progress = progressFraction,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(CircleShape),
                                            color = ThemePink,
                                            trackColor = SoftGray
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        chapter.bulletPoints.forEachIndexed { index, point ->
                                            val isChecked = checkedPoints[index] ?: false
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = if (isChecked) Color(0xFFF1F8F4) else Color.White),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, if (isChecked) Color(0xFFC3E6CB) else SoftGray),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clickable { checkedPoints[index] = !isChecked }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Checkbox(
                                                        checked = isChecked,
                                                        onCheckedChange = { checkedPoints[index] = it },
                                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E8B57))
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = point,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = if (isChecked) Color(0xFF1E5D36) else DarkText,
                                                        fontWeight = FontWeight.Bold,
                                                        lineHeight = 16.sp,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }

                                        if (allChecked) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Surface(
                                                color = Color(0xFFE8F6EE),
                                                border = BorderStroke(1.dp, Color(0xFF2E8B57)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text("🏆 ALL CHAPTER COMPLIANCE CHECKED!", fontWeight = FontWeight.Bold, color = Color(0xFF226E40), fontSize = 12.sp)
                                                    Text("Fantastic accuracy! You have parsed every safety requirement. Click Next to prove your knowledge in the final mastery checkpoint!", fontSize = 11.sp, color = Color(0xFF2A7C49), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 2.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                // --- SLIDE 5: MINI CHAPTER MASTER CHECKPOINT EXAM ---
                                4 -> {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .background(ThemePeach.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "🔥 MASTERY CHECKPOINT",
                                                color = ThemePeach,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = slideQuestion as String,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkText,
                                            lineHeight = 20.sp
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        val options = slideOptions as List<String>
                                        val correct = slideCorrectIndex as Int
                                        val explanationText = slideExplanation as String

                                        options.forEachIndexed { optIdx, option ->
                                            val isSelected = selectedOptionIndex == optIdx
                                            val containerColor = if (slideQuizSubmitted) {
                                                if (optIdx == correct) Color(0xFFE8F6EE)
                                                else if (isSelected) Color(0xFFFFF2F2)
                                                else Color.White
                                            } else {
                                                if (isSelected) ThemePink.copy(alpha = 0.1f) else Color.White
                                            }

                                            val borderColor = if (slideQuizSubmitted) {
                                                if (optIdx == correct) Color(0xFF2E8B57)
                                                else if (isSelected) ThemePink
                                                else SoftGray
                                            } else {
                                                if (isSelected) ThemePink else SoftGray
                                            }

                                            val labelColor = if (slideQuizSubmitted) {
                                                if (optIdx == correct) Color(0xFF226E40)
                                                else if (isSelected) Color(0xFFAC2626)
                                                else DarkText
                                            } else {
                                                if (isSelected) ThemePink else DarkText
                                            }

                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = containerColor),
                                                border = BorderStroke(1.5.dp, borderColor),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clickable { if (!slideQuizSubmitted) selectedOptionIndex = optIdx }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) ThemePink else SoftGray),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = ('A'.code + optIdx).toChar().toString(),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color.White else DarkPurpleAccent
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = option,
                                                        fontSize = 12.sp,
                                                        lineHeight = 16.sp,
                                                        color = labelColor,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (!slideQuizSubmitted) {
                                            Button(
                                                onClick = { if (selectedOptionIndex != null) slideQuizSubmitted = true },
                                                enabled = selectedOptionIndex != null,
                                                colors = ButtonDefaults.buttonColors(containerColor = ThemePink),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Submit Answer Check", fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        } else {
                                            val isCorrect = selectedOptionIndex == correct
                                            Surface(
                                                color = if (isCorrect) Color(0xFFE8F6EE) else Color(0xFFFFF2F2),
                                                border = BorderStroke(1.dp, if (isCorrect) Color(0xFF2E8B57) else ThemePink),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                            contentDescription = "feedback icon",
                                                            tint = if (isCorrect) Color(0xFF2D6A4F) else ThemePink,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = if (isCorrect) "EXCELLENT WORK! 🎉" else "ANSWER DISMANTLED! ❌",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isCorrect) Color(0xFF2D6A4F) else Color(0xFFAC2626)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = explanationText,
                                                        fontSize = 11.sp,
                                                        lineHeight = 15.sp,
                                                        color = if (isCorrect) Color(0xFF2D6A4F) else Color(0xFFAC2626)
                                                    )
                                                    
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Button(
                                                        onClick = {
                                                            selectedOptionIndex = null
                                                            slideQuizSubmitted = false
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = SoftGray),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Retry Lesson Checkpoint", color = DarkPurpleAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // --- BOTTOM SLIDE NAVIGATION BAR ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { 
                                    if (currentSlideIndex > 0) {
                                        currentSlideIndex-- 
                                    } else {
                                        selectedChapter = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftGray),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (currentSlideIndex == 0) "◀ Exit Book" else "◀ Back Step",
                                    color = DarkPurpleAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (currentSlideIndex < 4) {
                                        currentSlideIndex++
                                    } else {
                                        selectedChapter = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ThemePink),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Text(
                                    text = if (currentSlideIndex == 4) "Finish Chapter! 🏅" else "Next Step ▶",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun defianceFeedbackHeader(allGood: Boolean, color: Color) {
    Text(
        text = if (allGood) "PERFECT MECHANICS! ✅" else "MECHANICS WARNING! ❌",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

// --- Vocabulary List Study Tool Flip card elements ---
@Composable
fun VocabStudyScreen(viewModel: SSWViewModel, onNavigateToTab: (String) -> Unit, onSpeak: (String, Boolean) -> Unit = { _, _ -> }) {
    val searchState by viewModel.vocabSearchQuery.collectAsStateWithLifecycle()
    val activeCategoryState by viewModel.selectedVocabCategory.collectAsStateWithLifecycle()
    val vocabList by viewModel.filteredVocabularies.collectAsStateWithLifecycle()
    val favoriteSet by viewModel.favoriteWords.collectAsStateWithLifecycle()

    var isDeckView by remember { mutableStateOf(true) }
    var activeDeckIndex by remember { mutableStateOf(0) }
    var activeDeckFlipped by remember { mutableStateOf(false) }

    var flippedWordId by remember { mutableStateOf<Int?>(null) }
    var detailVocabItem by remember { mutableStateOf<Vocabulary?>(null) }

    val categories = listOf("All", "Body Parts", "Body Positions", "Symptoms", "Care Actions")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("vocab_screen_layout")
    ) {
        Text(
            text = "SSW Technical Vocabulary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Text(
            text = "Study core Japanese clinical terminology essential for care home safety and passing the exam.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkPurpleAccent.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Control Tabs for Flashcard Deck vs Glossary list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SoftGray, shape = RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDeckView) ThemePink else Color.Transparent)
                    .clickable { isDeckView = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📇 ", fontSize = 14.sp)
                    Text(
                        text = "Anki Flashcards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isDeckView) Color.White else DarkPurpleAccent
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (!isDeckView) ThemePink else Color.Transparent)
                    .clickable { isDeckView = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔍 ", fontSize = 14.sp)
                    Text(
                        text = "Glossary Index",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (!isDeckView) Color.White else DarkPurpleAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar (Available for both views)
        OutlinedTextField(
            value = searchState,
            onValueChange = { 
                viewModel.updateSearchQuery(it)
                activeDeckIndex = 0 // reset deck to first index on filter
                activeDeckFlipped = false
            },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search icon") },
            placeholder = { Text("Search spelling, pronunciation, romaji...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vocab_search_field"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = ThemePink,
                unfocusedIndicatorColor = DarkPurpleAccent.copy(alpha = 0.2f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val selected = activeCategoryState == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) ThemePink else Color.White)
                        .clickable { 
                            viewModel.updateSelectedCategory(category)
                            activeDeckIndex = 0 // reset deck
                            activeDeckFlipped = false
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else DarkPurpleAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- RENDER SELECTED VIEW ---
        if (isDeckView) {
            val filteredSize = vocabList.size
            if (filteredSize == 0) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching words found.",
                        color = DarkPurpleAccent.copy(alpha = 0.6f)
                    )
                }
            } else {
                val safeIndex = activeDeckIndex.coerceIn(0, filteredSize - 1)
                val activeVocab = vocabList[safeIndex]
                val isFavorite = favoriteSet.contains(activeVocab.id)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Deck Stack Look decoration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background stack card 2
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ThemePeach.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .offset(y = 12.dp)
                                .shadow(1.dp, shape = RoundedCornerShape(24.dp))
                        ) {}

                        // Background stack card 1
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ThemePink.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .offset(y = 6.dp)
                                .shadow(2.dp, shape = RoundedCornerShape(24.dp))
                        ) {}

                        // Main active focus card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.5.dp, if (activeDeckFlipped) ThemePink else SoftGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(24.dp))
                                .clickable { activeDeckFlipped = !activeDeckFlipped }
                                .testTag("spotlight_card_${activeVocab.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                            ) {
                                // Card Category & actions header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(ThemePeach.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = activeVocab.category,
                                            fontSize = 9.sp,
                                            color = ThemePeach,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Text To Speech audio output trig
                                        IconButton(
                                            onClick = { onSpeak(activeVocab.word, true) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Pronounce Word",
                                                tint = ThemePink,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(2.dp))

                                        // Detailed popup shortcut
                                        IconButton(
                                            onClick = { detailVocabItem = activeVocab },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Info,
                                                contentDescription = "Info",
                                                tint = ThemePink,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Favorite button
                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(activeVocab.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = "Save check",
                                                tint = ThemePink,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Active Card Flip Layout
                                Crossfade(targetState = activeDeckFlipped, label = "ankiFlip") { flipped ->
                                    if (!flipped) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = activeVocab.word,
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkText,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Surface(
                                                color = SoftGray,
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(
                                                    text = "Pronunciation: ${activeVocab.pronunciation}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DarkPurpleAccent,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = "Romaji: ${activeVocab.romaji}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = activeVocab.translation,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = ThemePink,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Text(
                                                text = activeVocab.explanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DarkText,
                                                lineHeight = 16.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Hint footers
                                Text(
                                    text = if (activeDeckFlipped) "Tap to hide translation ↺" else "Tap card face to flip ↺",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ThemePink.copy(alpha = 0.8f),
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation bar & word counter
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (safeIndex > 0) {
                                    activeDeckIndex = safeIndex - 1
                                    activeDeckFlipped = false
                                }
                            },
                            enabled = safeIndex > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftGray, disabledContainerColor = SoftGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "◀ Prev", color = if (safeIndex > 0) DarkPurpleAccent else Color.Gray, fontWeight = FontWeight.Bold)
                        }

                        // Progress Indicator
                        Text(
                            text = "${safeIndex + 1} of ${filteredSize}",
                            fontWeight = FontWeight.Black,
                            color = DarkPurpleAccent,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
                                if (safeIndex < filteredSize - 1) {
                                    activeDeckIndex = safeIndex + 1
                                    activeDeckFlipped = false
                                }
                            },
                            enabled = safeIndex < filteredSize - 1,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftGray, disabledContainerColor = SoftGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Next ▶", color = if (safeIndex < filteredSize - 1) DarkPurpleAccent else Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Word Items List (Glossary Index Mode)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (vocabList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matching words found in search filter.",
                                color = DarkPurpleAccent.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(vocabList) { vocab ->
                        val isFlipped = flippedWordId == vocab.id
                        val isFavorite = favoriteSet.contains(vocab.id)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, shape = RoundedCornerShape(20.dp))
                                .clickable {
                                    flippedWordId = if (isFlipped) null else vocab.id
                                }
                                .testTag("vocab_card_${vocab.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(ThemePeach.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = vocab.category,
                                            fontSize = 10.sp,
                                            color = ThemePeach,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Explanation detailing pop-up
                                        IconButton(
                                            onClick = { detailVocabItem = vocab },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("vocab_detail_btn_${vocab.id}")
                                        ) {
                                            Icon(
                                                Icons.Filled.Info,
                                                contentDescription = "View term details",
                                                tint = ThemePink.copy(alpha = 0.8f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Bookmark/Favorite button
                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(vocab.id) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("vocab_favorite_btn_${vocab.id}")
                                        ) {
                                            Icon(
                                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = "Saved state",
                                                tint = if (isFavorite) ThemePink else DarkPurpleAccent.copy(alpha = 0.5f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Switch face between Kanji vs Definition depending on Flip
                                Crossfade(targetState = isFlipped, label = "cardFlip") { flipped ->
                                    if (!flipped) {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = vocab.word,
                                                style = MaterialTheme.typography.headlineLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkText
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            
                                            // Furigana-like pronunciation guide
                                            Surface(
                                                color = SoftGray,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Kana: ${vocab.pronunciation}  •  Romaji: ${vocab.romaji}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = DarkPurpleAccent,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Touch to flip and see English ↺",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ThemePink.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
                                    } else {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = vocab.translation,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = ThemePink
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = vocab.explanation,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = DarkText,
                                                lineHeight = 20.sp
                                            )
                                            
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Touch to flip back ↺",
                                                fontSize = 10.sp,
                                                color = DarkPurpleAccent.copy(alpha = 0.4f),
                                                modifier = Modifier.align(Alignment.End)
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
    }

    // Detail Vocab explanations pop-up AlertDialog overlay
    if (detailVocabItem != null) {
        val vocab = detailVocabItem!!
        AlertDialog(
            onDismissRequest = { detailVocabItem = null },
            title = {
                Text(
                    text = "${vocab.word} (${vocab.pronunciation})",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "English Definition:",
                        fontWeight = FontWeight.Bold,
                        color = ThemePink,
                        fontSize = 12.sp
                    )
                    Text(text = vocab.translation, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Caregiving Context Explanation:",
                        fontWeight = FontWeight.Bold,
                        color = ThemePink,
                        fontSize = 12.sp
                    )
                    Text(text = vocab.explanation, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ThemePeach.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Study Tip: Pronounce carefully in furigana, memorize the romaji spelling, and link with the correct posture/care principles!",
                            fontSize = 11.sp,
                            color = DarkPurpleAccent.copy(alpha = 0.8f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { detailVocabItem = null },
                    modifier = Modifier.testTag("vocab_detail_close")
                ) {
                    Text("Close", color = ThemePink)
                }
            }
        )
    }
}

// --- Practice Exam / Interactive Quiz Module ---
@Composable
fun QuizModuleScreen(viewModel: SSWViewModel) {
    val activeIndex by viewModel.activeQuizQuestionIndex.collectAsStateWithLifecycle()
    val selectedAns by viewModel.selectedAnswerIndex.collectAsStateWithLifecycle()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsStateWithLifecycle()
    val correctCount by viewModel.quizCorrectAnswersCount.collectAsStateWithLifecycle()
    val isFinished by viewModel.isQuizFinished.collectAsStateWithLifecycle()

    val questionsList = viewModel.currentQuizQuestions
    val maxQuestions = questionsList.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("quiz_screen_layout")
    ) {
        if (!isFinished) {
            val question = questionsList[activeIndex]

            // Premium Header with Title and Current Progress Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SSW Testing Console",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText
                    )
                    Text(
                        text = "Clinical Competency Exam",
                        style = MaterialTheme.typography.bodySmall,
                        color = ThemePeach,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(ThemePink.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Q. ${activeIndex + 1} / $maxQuestions",
                        fontWeight = FontWeight.Black,
                        color = ThemePink,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Progress Dots representing question records index
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until maxQuestions) {
                    val isActive = i == activeIndex
                    val isPast = i < activeIndex
                    val dotColor = when {
                        isActive -> ThemePink
                        isPast -> Color(0xFF81C784) // Green for completed ones
                        else -> SoftGray
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // If it is observational scenario question, render BEAUTIFUL chat logs alternating dialogue
                if (question.questionType == QuizType.CONVERSATIONAL && question.dialogs != null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ThemePeach.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "💬 CASE DIALOGUE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = ThemePeach,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            question.dialogs.forEach { dialog ->
                                val isPatient = dialog.speaker.contains("Tanaka") || dialog.speaker.contains("User") || dialog.speaker.contains("Patient")
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalAlignment = if (isPatient) Alignment.Start else Alignment.End
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 260.dp)
                                            .background(
                                                color = if (isPatient) ThemePeach.copy(alpha = 0.15f) else SoftGray,
                                                shape = RoundedCornerShape(
                                                    topStart = 16.dp,
                                                    topEnd = 16.dp,
                                                    bottomStart = if (isPatient) 0.dp else 16.dp,
                                                    bottomEnd = if (isPatient) 16.dp else 0.dp
                                                )
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = dialog.speaker,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isPatient) ThemePeach else DarkPurpleAccent
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = dialog.textJa,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkText
                                            )
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Text(
                                                text = dialog.textEn,
                                                fontSize = 11.sp,
                                                color = DarkPurpleAccent.copy(alpha = 0.8f),
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quiz Question Text Box (Large card)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.5.dp, SoftGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, shape = RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("❓", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SCENARIO QUESTION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ThemePink
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = question.questionJa,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.questionEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkPurpleAccent.copy(alpha = 0.8f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Interactive Options List
                items(question.options.size) { index ->
                    val optionText = question.options[index]
                    val isSelected = selectedAns == index
                    val isCorrectIdx = question.correctAnswerIndex == index

                    val containerColor = when {
                        isSubmitted && isCorrectIdx -> Color(0xFFE8F5E9)      // Right target choice -> emerald translucent
                        isSubmitted && isSelected && !isCorrectIdx -> Color(0xFFFFEBEE) // Clicked item was wrong -> cherry translucent
                        isSelected -> ThemePink.copy(alpha = 0.1f)             // Active draft selection
                        else -> Color.White
                    }

                    val borderColor = when {
                        isSubmitted && isCorrectIdx -> Color(0xFF4CAF50)
                        isSubmitted && isSelected && !isCorrectIdx -> Color(0xFFEF5350)
                        isSelected -> ThemePink
                        else -> SoftGray
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, shape = RoundedCornerShape(16.dp))
                            .clickable(enabled = !isSubmitted) { viewModel.selectQuizAnswer(index) }
                            .testTag("quiz_option_$index")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSubmitted && isCorrectIdx -> Color(0xFF4CAF50)
                                            isSubmitted && isSelected && !isCorrectIdx -> Color(0xFFEF5350)
                                            isSelected -> ThemePink
                                            else -> SoftGray
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected || isSubmitted) Color.White else DarkPurpleAccent
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = DarkText,
                                modifier = Modifier.weight(1f)
                            )

                            // Status tags on submitted answers
                            if (isSubmitted) {
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isCorrectIdx) {
                                    Text("✓ Clinical Match", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                } else if (isSelected) {
                                    Text("✗ Safety Alert", color = Color(0xFFC62828), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Comprehensive Explanatory Analysis card
                if (isSubmitted) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.5.dp, ThemePeach.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, shape = RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💡", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SSW JAPAN CLINICAL ANALYSIS",
                                        fontWeight = FontWeight.Black,
                                        color = ThemePeach,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = question.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkText,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Buttons Bar
            Spacer(modifier = Modifier.height(12.dp))

            if (!isSubmitted) {
                Button(
                    onClick = { viewModel.submitQuizAnswer() },
                    colors = ButtonDefaults.buttonColors(containerColor = ThemePink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_button"),
                    enabled = selectedAns != null,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Submit Answer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            } else {
                Button(
                    onClick = { viewModel.nextQuizQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = ThemePink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_question_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (activeIndex == questionsList.lastIndex) "View Report Certificate ➔" else "Next Question ➔",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

        } else {
            // HIGH-FIDELITY STUDY CLINICAL REPORT CARD
            val passRate = (correctCount.toFloat() / maxQuestions.toFloat() * 100).toInt()
            val passed = passRate >= 60

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("quiz_finished_view"),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, if (passed) Color(0xFF81C784) else ThemePink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, shape = RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge seal
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(if (passed) Color(0xFFE8F5E9) else ThemePink.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (passed) "🏅" else "📋",
                                fontSize = 42.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (passed) "SSW COMPLIANCE PASS!" else "CONTINUE PRACTICE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (passed) Color(0xFF2E7D32) else ThemePink,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Nursing Care Competency Certificate",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Score Gauge Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SoftGray, shape = RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("SCORE RATE", fontSize = 10.sp, color = DarkPurpleAccent, fontWeight = FontWeight.Bold)
                                Text("$passRate%", fontSize = 32.sp, fontWeight = FontWeight.Black, color = DarkText)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ACCURACY", fontSize = 10.sp, color = DarkPurpleAccent, fontWeight = FontWeight.Bold)
                                Text("$correctCount / $maxQuestions", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ThemePink)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Japanese Official Note
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ThemePeach.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (passed) "介護特定技能試験 合格基準達成" else "合格基準: 60%以上の正答率が必要です",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPurpleAccent,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (passed) 
                                        "Congratulations! You've matched Japan's nursing home safety guidelines accurately. Keep studying to guarantee a high score!" 
                                        else "Clinical care standards in Japan require 60% or more. Ask our AI Tutor to clarify any tricky questions!",
                                    fontSize = 11.sp,
                                    color = DarkPurpleAccent.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Restart Button
                        Button(
                            onClick = { viewModel.restartQuiz() },
                            colors = ButtonDefaults.buttonColors(containerColor = ThemePink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("restart_quiz_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Restart Evaluation", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- AI Tutor Interface with fully conversational messaging ---
@Composable
fun AiTutorScreen(viewModel: SSWViewModel, user: UserProfile) {
    // Deprecated
}

@Composable
fun BypassedAiTutorScreen(viewModel: SSWViewModel, user: UserProfile) {
    val loading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val tutorHistory by viewModel.aiTutorHistory.collectAsStateWithLifecycle()

    var userMessageText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tutor_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI SSW Care Tutor 🤖",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = "Providing instant support in ${user.nativeLanguage} with technical care guidance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkPurpleAccent.copy(alpha = 0.7f)
                )
            }

            TextButton(
                onClick = { viewModel.clearTutorChat() },
                modifier = Modifier.background(ThemePink.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
            ) {
                Text("Clear Chat", color = ThemePink, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chats messaging list or helpful suggestions
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (tutorHistory.isEmpty()) {
                // Renders helpful study quick buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Good day! Tap an SSW clinical focus area to begin:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Prompt Card 1
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .shadow(1.dp, shape = RoundedCornerShape(16.dp))
                            .clickable { viewModel.askForMockQuiz() }
                            .testTag("prompt_generate_quiz")
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📝", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Generate a Mock Exam Question", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
                                Text("Try a sudden practice scenario mapped directly to criteria.", fontSize = 11.sp, color = DarkPurpleAccent.copy(alpha = 0.6f))
                            }
                        }
                    }

                    // Prompt Card 2
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .shadow(1.dp, shape = RoundedCornerShape(16.dp))
                            .clickable { viewModel.askAiTutor("Explain how the 'Dakken Chakkan' (Undress unaffected, dress affected) rule works during clothing changes.") }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🧥", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Explain the 'Dakken Chakkan' rule", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
                                Text("Critical clothing procedure commonly tested in skills exam.", fontSize = 11.sp, color = DarkPurpleAccent.copy(alpha = 0.6f))
                            }
                        }
                    }

                    // Prompt Card 3
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .shadow(1.dp, shape = RoundedCornerShape(16.dp))
                            .clickable { viewModel.askAiTutor("Explain what 'Goen' (Aspiration) and 'Jokusou' (Pressure sores) are and how to prevent them.") }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💧", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Explain 'Goen' and 'Jokusou' prevention", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
                                Text("Key concepts of safety, aspiration risks, and skin protection.", fontSize = 11.sp, color = DarkPurpleAccent.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tutorHistory) { chat ->
                        val isUser = chat.second
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 290.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 20.dp,
                                            topEnd = 20.dp,
                                            bottomStart = if (isUser) 20.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 20.dp
                                        )
                                    )
                                    .background(
                                        if (isUser) ThemePink else SoftGray
                                    )
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = chat.first,
                                    color = if (isUser) Color.White else DarkText,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    if (loading) {
                        item {
                            Surface(
                                color = SoftGray,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        color = ThemePink,
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI Care Tutor is explaining...",
                                        fontSize = 11.sp,
                                        color = DarkPurpleAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .shadow(1.dp, shape = RoundedCornerShape(24.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userMessageText,
                onValueChange = { userMessageText = it },
                placeholder = { Text("Ask your tutor anything...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (userMessageText.isNotBlank() && !loading) {
                        viewModel.askAiTutor(userMessageText)
                        userMessageText = ""
                        keyboardController?.hide()
                    }
                })
            )

            IconButton(
                onClick = {
                    viewModel.askAiTutor(userMessageText)
                    userMessageText = ""
                    keyboardController?.hide()
                },
                modifier = Modifier.testTag("chat_send_button"),
                enabled = userMessageText.isNotBlank() && !loading
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send message",
                    tint = if (userMessageText.isNotBlank()) ThemePink else DarkPurpleAccent.copy(alpha = 0.4f)
                )
            }
        }
    }
}

// --- Profile & Language Settings Screen ---
@Composable
fun ProfileSettingsScreen(viewModel: SSWViewModel, user: UserProfile) {
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()

    var editingName by remember { mutableStateOf(user.name) }
    var selectedLanguage by remember { mutableStateOf(user.nativeLanguage) }

    val languages = listOf("English", "Filipino/Tagalog", "Nepalese", "Indonesian", "Vietnamese", "Burmese")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SSW Student Hub",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText
                    )
                    Text(
                        text = "Manage your study profile and review certifications.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkPurpleAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text("🌸", fontSize = 28.sp)
            }
        }

        // Beautiful Tactile Avatar Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftGray),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ThemePink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (editingName.isNotBlank()) editingName.trim().take(1).uppercase() else "A",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = if (editingName.isNotBlank()) editingName else "Foreign Student",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "L1 Native Language Guide: ",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = selectedLanguage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ThemePeach
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, SoftGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Edit Profile Settings",
                        fontWeight = FontWeight.Bold,
                        color = ThemePink,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        label = { Text("Student Nickname / Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Primary Translation Output Language:",
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Our server AI tutor will explain Japanese clinical context in this tongue.",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Premium chunked grid of translator chips instead of radios
                    languages.chunked(2).forEach { languageRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            languageRow.forEach { lang ->
                                val isSelected = selectedLanguage == lang
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) ThemePink else SoftGray)
                                        .clickable { selectedLanguage = lang }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (if (isSelected) "✓ " else "") + lang,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else DarkPurpleAccent
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.saveUserProfile(editingName, selectedLanguage) },
                        colors = ButtonDefaults.buttonColors(containerColor = ThemePink),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_save_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Configuration", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quiz History Records Statistics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, SoftGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historical Scorecard",
                            fontWeight = FontWeight.Bold,
                            color = ThemePink,
                            fontSize = 14.sp
                        )

                        if (quizHistory.isNotEmpty()) {
                            TextButton(onClick = { viewModel.clearQuizRecordLogs() }) {
                                Text("Clear History", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (quizHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SoftGray, shape = RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No evaluations registered yet. Take our Practice Quiz to start recording certification metrics!",
                                color = DarkPurpleAccent.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        quizHistory.forEach { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📝", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Evaluation Score: ${record.correctCount} / ${record.totalQuestions}",
                                            fontWeight = FontWeight.Bold,
                                            color = DarkText,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Accuracy: ${(record.correctCount.toFloat() / record.totalQuestions.toFloat() * 100).toInt()}%",
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                val pass = (record.correctCount.toFloat() / record.totalQuestions.toFloat() * 100) >= 60
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (pass) Color(0xFFE8F5E9) else ThemePink.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (pass) "PASS" else "FAIL",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (pass) Color(0xFF2E7D32) else ThemePink
                                    )
                                }
                            }
                            Divider(color = SoftGray)
                        }
                    }
                }
            }
        }
    }
}
