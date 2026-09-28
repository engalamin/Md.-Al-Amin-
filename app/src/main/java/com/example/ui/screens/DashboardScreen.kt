package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.MathpadViewModel
import com.example.ui.components.CameraCaptureDialog
import com.example.ui.components.ChatHistoryView
import com.example.ui.components.SolutionRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MathpadViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val problemText by viewModel.problemText.collectAsStateWithLifecycle()
    val attachedBitmap by viewModel.attachedBitmap.collectAsStateWithLifecycle()
    val isSolving by viewModel.isSolving.collectAsStateWithLifecycle()
    val solutionResult by viewModel.solutionResult.collectAsStateWithLifecycle()
    val isCurrentSaved by viewModel.isCurrentSaved.collectAsStateWithLifecycle()
    val followUpList by viewModel.followUpList.collectAsStateWithLifecycle()
    val isFollowUpLoading by viewModel.isFollowUpLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val studentGrade by viewModel.studentGrade.collectAsStateWithLifecycle()

    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val savedSolutions by viewModel.savedSolutions.collectAsStateWithLifecycle()
    val quizRecords by viewModel.quizRecords.collectAsStateWithLifecycle()

    var followUpInput by remember { mutableStateOf("") }
    var languageExpanded by remember { mutableStateOf(false) }
    var gradeExpanded by remember { mutableStateOf(false) }
    var showCameraDialog by remember { mutableStateOf(false) }

    if (showCameraDialog) {
        CameraCaptureDialog(
            onDismiss = { showCameraDialog = false },
            onImageCaptured = { bitmap ->
                viewModel.setAttachedBitmap(bitmap)
            }
        )
    }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = try {
                if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            } catch (e: Exception) {
                null
            }
            viewModel.setAttachedBitmap(bitmap)
        }
    }

    val exampleProblems = listOf(
        "Solve 2x² − 5x − 3 = 0",
        "Find d/dx of x·sin(x)",
        "∫ from 0 to π of sin²(x) dx",
        "A triangle has sides 7, 8, 9. Find its area.",
        "Simplify (x²-9)/(x²+x-6)",
        "2x + 3 = 11"
    )

    val languages = listOf(
        "" to "Auto-detect language",
        "English" to "English",
        "Bangla (বাংলা)" to "Bangla (বাংলা)",
        "Hindi (हिन्दी)" to "Hindi (हिन्दी)",
        "Spanish (Español)" to "Spanish (Español)",
        "French (Français)" to "French (Français)",
        "Arabic (العربية)" to "Arabic (العربية)",
        "Chinese (中文)" to "Chinese (中文)",
        "German (Deutsch)" to "German (Deutsch)"
    )

    val gradeLevels = listOf(
        "Primary school",
        "Middle school",
        "High school",
        "College / University"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Quick Stats
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val studentName = profile?.name?.ifBlank { null } ?: "Student"
                Text(
                    text = "Hi, $studentName! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Type your homework problem or snap a photo. Get a clear step-by-step solution you can truly master.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_row"),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = "${profile?.solvedCount ?: 0}",
                    label = "Solved"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = "${savedSolutions.size}",
                    label = "Saved"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = "${quizRecords.size}",
                    label = "Quizzes"
                )
                val bestScore = if (quizRecords.isNotEmpty()) {
                    val maxPct = quizRecords.maxOf { (it.score.toFloat() / it.totalQuestions * 100).toInt() }
                    "$maxPct%"
                } else "–"
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = bestScore,
                    label = "Best Quiz"
                )
            }
        }

        // Hero Graphic Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_math_hero),
                        contentDescription = "Mathpad Study Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.88f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.65f)) {
                            Text(
                                text = "Learn Smartly",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Understand every formula, verify answers, and practice similar problems.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Problem Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("problem_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Your Problem",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = problemText,
                        onValueChange = { viewModel.setProblemText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("problem_text_input"),
                        placeholder = {
                            Text("Type an algebra, calculus, or geometry question, or attach a photo below…")
                        },
                        trailingIcon = {
                            if (problemText.isNotBlank()) {
                                IconButton(onClick = { viewModel.setProblemText("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear input")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Photo Attachment Area
                    if (attachedBitmap != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                bitmap = attachedBitmap!!.asImageBitmap(),
                                contentDescription = "Attached homework photo",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Handwritten problem photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ready to analyze handwriting & formulas",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { showCameraDialog = true },
                                modifier = Modifier.testTag("retake_camera_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Retake photo", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = { viewModel.setAttachedBitmap(null) },
                                modifier = Modifier.testTag("remove_photo_button")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove attached photo", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Level & Language options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Grade Level Dropdown
                        ExposedDropdownMenuBox(
                            expanded = gradeExpanded,
                            onExpandedChange = { gradeExpanded = !gradeExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = studentGrade,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Level") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = gradeExpanded,
                                onDismissRequest = { gradeExpanded = false }
                            ) {
                                gradeLevels.forEach { grade ->
                                    DropdownMenuItem(
                                        text = { Text(grade) },
                                        onClick = {
                                            viewModel.setStudentGrade(grade)
                                            gradeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Language Dropdown
                        ExposedDropdownMenuBox(
                            expanded = languageExpanded,
                            onExpandedChange = { languageExpanded = !languageExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            val displayLang = languages.firstOrNull { it.first == selectedLanguage }?.second ?: "Auto-detect"
                            OutlinedTextField(
                                value = displayLang,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Language") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = languageExpanded,
                                onDismissRequest = { languageExpanded = false }
                            ) {
                                languages.forEach { (code, name) ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            viewModel.setLanguage(code)
                                            languageExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Input Actions: CameraX Snap & Gallery Picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCameraDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("camera_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Take Photo")
                        }

                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("attach_photo_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery")
                        }
                    }

                    // Primary Solve Button
                    Button(
                        onClick = { viewModel.solve() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("solve_problem_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSolving && (problemText.isNotBlank() || attachedBitmap != null)
                    ) {
                        if (isSolving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Solving with AI…")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Solve Problem", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Error Message if any
                    errorMessage?.let { err ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Example chips
                    Column {
                        Text(
                            text = "Or try an example:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            exampleProblems.forEach { ex ->
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.setProblemText(ex) },
                                    label = { Text(ex, fontSize = 12.sp) },
                                    shape = CircleShape
                                )
                            }
                        }
                    }
                }
            }
        }

        // Solution Section (when available)
        solutionResult?.let { res ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solution_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = res.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (res.isAiGenerated) "✨ AI Step-by-Step Reasoner" else "⚡ Fast Mathpad Engine",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Save Button
                            IconButton(
                                onClick = { viewModel.saveCurrentSolution() },
                                modifier = Modifier.testTag("save_solution_button")
                            ) {
                                Icon(
                                    imageVector = if (isCurrentSaved) Icons.Default.BookmarkAdded else Icons.Default.Bookmark,
                                    contentDescription = "Save solution",
                                    tint = if (isCurrentSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Solution Formatter
                        SolutionRenderer(markdownText = res.markdownText)

                        // Reinforcement Practice Quiz Banner
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reinforce_quiz_banner"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            ),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Quiz,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Reinforce Learning with AI Quiz",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Generate a 5-question practice quiz based on this problem to test your mastery.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                                Button(
                                    onClick = {
                                        viewModel.startReinforcementQuiz(
                                            topic = res.title,
                                            solvedContent = res.markdownText
                                        )
                                    },
                                    modifier = Modifier.testTag("launch_reinforcement_quiz_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Start Quiz", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Interactive Follow-up Tools Toolbar
                        Text(
                            text = "Deepen Your Understanding:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.startReinforcementQuiz(
                                        topic = res.title,
                                        solvedContent = res.markdownText
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("quiz_chip_button")
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("5-Question Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.askFollowUp("Give me a similar practice problem to test myself. Do not solve it yet.")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("practice_button")
                            ) {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Practice Problem", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.askFollowUp("I didn't quite understand. Please explain the same solution in much simpler terms with smaller, clearer steps.")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("simpler_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Explain Simpler", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.askFollowUp("Show a completely different mathematical method to solve the original problem.")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("alternative_method_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Another Method", fontSize = 12.sp)
                            }
                        }

                        // Follow-up chat history
                        if (followUpList.isNotEmpty() || isFollowUpLoading) {
                            ChatHistoryView(
                                messages = followUpList,
                                isLoading = isFollowUpLoading
                            )
                        }

                        // Follow-up Question Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = followUpInput,
                                onValueChange = { followUpInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("follow_up_input"),
                                placeholder = { Text("Ask about any step or send your work…", fontSize = 13.sp) },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (followUpInput.isNotBlank()) {
                                        viewModel.askFollowUp(followUpInput)
                                        followUpInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .testTag("send_follow_up_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send question",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // New Problem button
                        OutlinedButton(
                            onClick = { viewModel.clearProblem() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_problem_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Solve Another Problem")
                        }
                    }
                }
            }
        }

        // Copyright Footer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Copyright by Engr. Al Amin 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
