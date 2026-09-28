package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatMessage

@Composable
fun SolutionRenderer(
    markdownText: String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val sections = parseSolutionSections(markdownText)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("solution_renderer_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Copy Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step-by-Step Learning Guide",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { clipboardManager.setText(AnnotatedString(markdownText)) },
                modifier = Modifier.testTag("copy_solution_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy solution",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section Cards
        sections.forEach { section ->
            when (section.type) {
                SectionType.UNDERSTAND -> {
                    SectionCard(
                        title = section.title,
                        icon = Icons.Default.Search,
                        badgeColor = Color(0xFF2563EB),
                        content = section.body
                    )
                }
                SectionType.METHOD -> {
                    SectionCard(
                        title = section.title,
                        icon = Icons.Default.Lightbulb,
                        badgeColor = Color(0xFF7C3AED),
                        content = section.body
                    )
                }
                SectionType.STEPS -> {
                    StepsCard(
                        title = section.title,
                        stepsText = section.body
                    )
                }
                SectionType.VERIFICATION -> {
                    SectionCard(
                        title = section.title,
                        icon = Icons.Default.CheckCircle,
                        badgeColor = Color(0xFF059669),
                        content = section.body
                    )
                }
                SectionType.FINAL_ANSWER -> {
                    FinalAnswerCard(
                        title = section.title,
                        content = section.body
                    )
                }
                SectionType.GENERAL -> {
                    SectionCard(
                        title = section.title,
                        icon = Icons.AutoMirrored.Filled.Help,
                        badgeColor = MaterialTheme.colorScheme.primary,
                        content = section.body
                    )
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            FormattedMathText(text = content)
        }
    }
}

@Composable
fun StepsCard(
    title: String,
    stepsText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "123",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Break steps into individual list items
            val rawLines = stepsText.lines().filter { it.isNotBlank() }
            val stepBlocks = mutableListOf<String>()
            var currentBlock = StringBuilder()

            for (line in rawLines) {
                if (line.trim().matches(Regex("^(\\d+\\.|-)\\s+.*"))) {
                    if (currentBlock.isNotEmpty()) {
                        stepBlocks.add(currentBlock.toString().trim())
                        currentBlock = StringBuilder()
                    }
                    currentBlock.append(line)
                } else {
                    if (currentBlock.isNotEmpty()) {
                        currentBlock.append("\n").append(line)
                    } else {
                        currentBlock.append(line)
                    }
                }
            }
            if (currentBlock.isNotEmpty()) {
                stepBlocks.add(currentBlock.toString().trim())
            }

            if (stepBlocks.isEmpty()) {
                FormattedMathText(text = stepsText)
            } else {
                stepBlocks.forEachIndexed { index, step ->
                    StepItemRow(stepIndex = index + 1, text = step)
                    if (index < stepBlocks.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StepItemRow(
    stepIndex: Int,
    text: String
) {
    val cleanText = text.replaceFirst(Regex("^(\\d+\\.|-)\\s*"), "").trim()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepIndex.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            FormattedMathText(text = cleanText)
        }
    }
}

@Composable
fun FinalAnswerCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    val cleanAnswer = content
        .replace("\\boxed{", "")
        .replace("}", "")
        .replace("$$", "")
        .trim()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, Color(0xFFD97706), RoundedCornerShape(14.dp)),
        color = Color(0xFFFEF3C7)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = title.ifBlank { "Final Answer" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF92400E)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cleanAnswer,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F),
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
fun FormattedMathText(
    text: String,
    modifier: Modifier = Modifier
) {
    // Clean up LaTeX formatting markers and display cleanly
    val lines = text.lines()
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { line ->
            val trimmed = line.trim()
            if (trimmed.startsWith("$$") && trimmed.endsWith("$$") && trimmed.length > 4) {
                val mathExpr = trimmed.removeSurrounding("$$").trim()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = mathExpr,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (trimmed.isNotBlank()) {
                val cleanedLine = trimmed
                    .replace("$", "")
                    .replace("\\quad", "  ")
                    .replace("\\times", "×")
                    .replace("\\cdot", "·")
                    .replace("\\pm", "±")
                    .replace("\\approx", "≈")
                    .replace("\\neq", "≠")
                    .replace("\\le", "≤")
                    .replace("\\ge", "≥")
                    .replace("\\text{", "")
                    .replace("\\implies", "⟹")
                    .replace("\\checkmark", "✓")
                Text(
                    text = cleanedLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun ChatHistoryView(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        messages.forEach { msg ->
            if (msg.sender == "user") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp, 14.dp, 2.dp, 14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp, 14.dp, 14.dp, 2.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Mathpad Tutor",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            FormattedMathText(text = msg.content)
                        }
                    }
                }
            }
        }

        if (isLoading) {
            Text(
                text = "Tutor is thinking…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

enum class SectionType {
    UNDERSTAND,
    METHOD,
    STEPS,
    VERIFICATION,
    FINAL_ANSWER,
    GENERAL
}

data class ParsedSection(
    val type: SectionType,
    val title: String,
    val body: String
)

fun parseSolutionSections(rawMarkdown: String): List<ParsedSection> {
    val sections = mutableListOf<ParsedSection>()
    val lines = rawMarkdown.lines()
    var currentType = SectionType.GENERAL
    var currentTitle = "Overview"
    val currentBody = StringBuilder()

    fun flush() {
        if (currentBody.isNotBlank()) {
            sections.add(ParsedSection(currentType, currentTitle, currentBody.toString().trim()))
            currentBody.clear()
        }
    }

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.startsWith("##")) {
            flush()
            val headerText = trimmed.removePrefix("##").trim()
            val lower = headerText.lowercase()
            when {
                lower.contains("understand") || lower.contains("objective") || lower.contains("সমস্যা") -> {
                    currentType = SectionType.UNDERSTAND
                    currentTitle = "Understand the problem"
                }
                lower.contains("method") || lower.contains("formula") || lower.contains("পদ্ধতি") || lower.contains("सूत्र") -> {
                    currentType = SectionType.METHOD
                    currentTitle = "Method & Formula"
                }
                lower.contains("step") || lower.contains("solution") || lower.contains("ধাপ") || lower.contains("चरण") -> {
                    currentType = SectionType.STEPS
                    currentTitle = "Step-by-step Solution"
                }
                lower.contains("verification") || lower.contains("check") || lower.contains("যাচাই") || lower.contains("जांच") -> {
                    currentType = SectionType.VERIFICATION
                    currentTitle = "Verification & Sanity Check"
                }
                lower.contains("final") || lower.contains("answer") || lower.contains("উত্তর") || lower.contains("उत्तर") -> {
                    currentType = SectionType.FINAL_ANSWER
                    currentTitle = "Final Answer"
                }
                else -> {
                    currentType = SectionType.GENERAL
                    currentTitle = headerText
                }
            }
        } else {
            currentBody.append(line).append("\n")
        }
    }
    flush()

    if (sections.isEmpty()) {
        sections.add(ParsedSection(SectionType.GENERAL, "Solution", rawMarkdown))
    }
    return sections
}
