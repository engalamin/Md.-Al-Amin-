package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MathpadViewModel
import com.example.ui.ScreenTab
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MathpadViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val profile by viewModel.userProfile.collectAsStateWithLifecycle()

                BackHandler {
                    if (!viewModel.handleBack()) {
                        finish()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "M",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Mathpad",
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            actions = {
                                val initial = profile?.name?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "S"
                                IconButton(
                                    onClick = { viewModel.navigateTo(ScreenTab.PROFILE) },
                                    modifier = Modifier.testTag("avatar_button")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initial,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
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
                            modifier = Modifier.testTag("main_bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == ScreenTab.DASHBOARD,
                                onClick = { viewModel.navigateTo(ScreenTab.DASHBOARD) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == ScreenTab.DASHBOARD) Icons.Default.Calculate else Icons.Outlined.Calculate,
                                        contentDescription = "Solver"
                                    )
                                },
                                label = { Text("Solver") },
                                modifier = Modifier.testTag("nav_solver")
                            )

                            NavigationBarItem(
                                selected = currentScreen == ScreenTab.LEARN,
                                onClick = { viewModel.navigateTo(ScreenTab.LEARN) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == ScreenTab.LEARN) Icons.Default.AutoStories else Icons.Outlined.AutoStories,
                                        contentDescription = "Learn"
                                    )
                                },
                                label = { Text("Learn") },
                                modifier = Modifier.testTag("nav_learn")
                            )

                            NavigationBarItem(
                                selected = currentScreen == ScreenTab.QUIZ,
                                onClick = { viewModel.navigateTo(ScreenTab.QUIZ) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == ScreenTab.QUIZ) Icons.Default.Quiz else Icons.Outlined.Quiz,
                                        contentDescription = "Quiz"
                                    )
                                },
                                label = { Text("Quiz") },
                                modifier = Modifier.testTag("nav_quiz")
                            )

                            NavigationBarItem(
                                selected = currentScreen == ScreenTab.SAVED,
                                onClick = { viewModel.navigateTo(ScreenTab.SAVED) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == ScreenTab.SAVED) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Saved"
                                    )
                                },
                                label = { Text("Saved") },
                                modifier = Modifier.testTag("nav_saved")
                            )

                            NavigationBarItem(
                                selected = currentScreen == ScreenTab.PROFILE,
                                onClick = { viewModel.navigateTo(ScreenTab.PROFILE) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == ScreenTab.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
                                        contentDescription = "Profile"
                                    )
                                },
                                label = { Text("Profile") },
                                modifier = Modifier.testTag("nav_profile")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 680.dp)
                        ) {
                            when (currentScreen) {
                                ScreenTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                                ScreenTab.LEARN -> LearnScreen(viewModel = viewModel)
                                ScreenTab.QUIZ -> QuizScreen(viewModel = viewModel)
                                ScreenTab.SAVED -> SavedScreen(viewModel = viewModel)
                                ScreenTab.PROFILE, ScreenTab.SETTINGS -> ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
