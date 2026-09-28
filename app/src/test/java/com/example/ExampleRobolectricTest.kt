package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TopicCatalog
import com.example.ui.components.parseSolutionSections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Mathpad", appName)
    }

    @Test
    fun `topic catalog contains rich lessons and quizzes`() {
        val topics = TopicCatalog.topics
        assertTrue("Catalog should contain topics", topics.isNotEmpty())
        val algebra = topics.find { it.category == "Algebra" }
        assertTrue("Algebra topic exists", algebra != null)

        val quizQuestions = TopicCatalog.getQuizQuestionsForTopic("Quadratic Equations")
        assertEquals(5, quizQuestions.size)
        assertTrue(quizQuestions.all { it.options.size == 4 })
    }

    @Test
    fun `solution parser extracts sections properly`() {
        val sampleMarkdown = """
            ## 🔍 Understand the problem
            Given 2x + 3 = 11. Find x.
            
            ## 💡 Method & Formula
            Inverse operations.
            
            ## 📝 Step-by-step Solution
            1. Subtract 3: 2x = 8
            2. Divide 2: x = 4
            
            ## 🎯 Final Answer
            x = 4
        """.trimIndent()

        val parsed = parseSolutionSections(sampleMarkdown)
        assertTrue(parsed.size >= 4)
        assertTrue(parsed.any { it.title.contains("Understand", ignoreCase = true) })
        assertTrue(parsed.any { it.title.contains("Final Answer", ignoreCase = true) })
    }

    @Test
    fun `viewmodel handles attached camera bitmap`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.MathpadViewModel(app)
        val dummyBitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
        vm.setAttachedBitmap(dummyBitmap)
        assertEquals(dummyBitmap, vm.attachedBitmap.value)

        vm.clearProblem()
        assertEquals(null, vm.attachedBitmap.value)
    }

    @Test
    fun `reinforcement quiz from previously solved topic produces 5 questions`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.MathpadViewModel(app)

        vm.startReinforcementQuiz("Quadratic Equations", "2x^2 - 5x - 3 = 0")
        assertEquals("Quadratic Equations", vm.quizTopic.value)
        assertEquals(com.example.ui.ScreenTab.QUIZ, vm.currentScreen.value)

        val questions = TopicCatalog.getQuizQuestionsForTopic("Quadratic Equations")
        assertEquals(5, questions.size)
        assertTrue(questions.all { it.options.size == 4 })
        assertTrue(questions.all { it.correctIndex in 0..3 })
        assertTrue(questions.all { it.explanation.isNotBlank() })
    }
}
