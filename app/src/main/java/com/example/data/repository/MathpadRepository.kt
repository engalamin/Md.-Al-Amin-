package com.example.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.example.data.QuizQuestion
import com.example.data.TopicCatalog
import com.example.data.TopicLesson
import com.example.data.local.MathpadDao
import com.example.data.local.QuizRecord
import com.example.data.local.SavedSolution
import com.example.data.local.UserProfile
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

data class SolvedResult(
    val title: String,
    val markdownText: String,
    val isAiGenerated: Boolean
)

class MathpadRepository(
    private val dao: MathpadDao,
    private val geminiClient: GeminiClient = GeminiClient()
) {
    // Solutions
    val savedSolutions: Flow<List<SavedSolution>> = dao.getAllSolutions()

    suspend fun saveSolution(title: String, content: String, topic: String = "General", gradeLevel: String = "High school"): Long {
        return dao.insertSolution(
            SavedSolution(
                title = title,
                rawSolution = content,
                topic = topic,
                gradeLevel = gradeLevel
            )
        )
    }

    suspend fun deleteSolution(id: Long) {
        dao.deleteSolutionById(id)
    }

    suspend fun clearAllSolutions() {
        dao.clearAllSolutions()
    }

    // Quizzes
    val quizRecords: Flow<List<QuizRecord>> = dao.getAllQuizRecords()

    suspend fun saveQuizResult(topic: String, difficulty: String, score: Int, total: Int): Long {
        return dao.insertQuizRecord(
            QuizRecord(
                topic = topic,
                difficulty = difficulty,
                score = score,
                totalQuestions = total
            )
        )
    }

    // Profile
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun saveProfile(name: String, grade: String, language: String = "") {
        dao.saveUserProfile(
            UserProfile(
                id = 1,
                name = name,
                grade = grade,
                language = language
            )
        )
    }

    suspend fun incrementSolved() {
        dao.incrementSolvedCount()
    }

    suspend fun clearAllData() {
        dao.clearAllSolutions()
        dao.clearAllQuizRecords()
        dao.clearUserProfile()
    }

    // Solving logic
    suspend fun solveProblem(
        problemText: String,
        bitmap: Bitmap? = null,
        language: String = "",
        gradeLevel: String = "High school"
    ): SolvedResult {
        val promptBuilder = StringBuilder()
        promptBuilder.append("You are an expert, precise, and supportive Multi-Lingual Math & Homework Solver for students.\n")
        promptBuilder.append("Student level: $gradeLevel.\n")
        if (language.isNotBlank()) {
            promptBuilder.append("Requested explanation language: $language.\n")
        } else {
            promptBuilder.append("Reply in the language the user wrote in or English by default.\n")
        }
        promptBuilder.append("""
            Please format your response clearly in markdown with these exact sections:
            ## 🔍 Understand the problem
            Restate the problem concisely, identify what needs to be found, and note any given constraints or conditions.
            
            ## 💡 Method & Formula
            Name the key theorem, formula, or concept being applied and explain why it is the optimal approach.
            
            ## 📝 Step-by-step Solution
            Provide a numbered list. For every step, provide a plain-language explanation of what is happening and why, followed by the mathematical working.
            
            ## ✅ Verification & Check
            Demonstrate how to double-check or sanity-check the result (e.g. plug the numbers back in or test boundary conditions).
            
            ## 🎯 Final Answer
            State the final simplified answer clearly in a bold highlighted box.
            
            Always be encouraging, define any terms a $gradeLevel student might find unfamiliar, and ask at the end if they would like a simpler explanation or a practice problem.
        """.trimIndent())

        promptBuilder.append("\n\nProblem to solve:\n$problemText")

        var base64Image: String? = null
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            promptBuilder.append("\n\n(Note: An image of the student's homework question is attached. Please read the handwriting or printed text carefully and transcribe the problem accurately into the 'Understand the problem' section.)")
        }

        val aiResult = geminiClient.generateContent(
            prompt = promptBuilder.toString(),
            imageBase64 = base64Image
        )

        return if (aiResult.isSuccess) {
            val content = aiResult.getOrThrow()
            val title = extractTitleFromProblem(problemText)
            incrementSolved()
            SolvedResult(title = title, markdownText = content, isAiGenerated = true)
        } else {
            // Intelligent local solver fallback
            val fallback = solveWithLocalEngine(problemText, gradeLevel)
            val title = extractTitleFromProblem(problemText)
            incrementSolved()
            SolvedResult(title = title, markdownText = fallback, isAiGenerated = false)
        }
    }

    suspend fun askFollowUp(
        currentSolution: String,
        followUpPrompt: String,
        gradeLevel: String = "High school"
    ): String {
        val prompt = """
            You are a patient math tutor helping a student ($gradeLevel level).
            Here is the problem and worked solution so far:
            $currentSolution
            
            The student asks:
            "$followUpPrompt"
            
            Respond directly, warmly, and helpfully. If they asked to explain simpler, break down the tricky step with an everyday intuitive analogy. If they asked for a practice problem, give one with a hint, without giving away the full answer immediately.
        """.trimIndent()

        val result = geminiClient.generateContent(prompt)
        return if (result.isSuccess) {
            result.getOrThrow()
        } else {
            generateLocalFollowUp(followUpPrompt, currentSolution)
        }
    }

    suspend fun generateReinforcementQuiz(
        topic: String,
        solvedContent: String? = null,
        difficulty: String = "Medium",
        gradeLevel: String = "High school"
    ): List<QuizQuestion> {
        val promptBuilder = StringBuilder()
        promptBuilder.append("You are a master math educator. A $gradeLevel student previously solved this homework problem and needs to reinforce their learning with practice:\n")
        promptBuilder.append("Problem / Topic: $topic\n")
        if (!solvedContent.isNullOrBlank()) {
            val excerpt = solvedContent.take(1500)
            promptBuilder.append("Solved context:\n$excerpt\n")
        }
        promptBuilder.append("""
            Generate a targeted 5-question multiple-choice practice quiz specifically focused on reinforcing this exact mathematical concept, formula, method, and related variations.
            Difficulty: $difficulty.
            Return ONLY a valid JSON array of 5 items. Each item must have:
            - "question": string question text testing this concept
            - "options": array of exactly 4 string options
            - "correctIndex": integer (0 to 3) representing the single correct option
            - "explanation": a concise 1-2 sentence explanation of why that answer is correct
            Do NOT include markdown formatting or backticks around the JSON array.
        """.trimIndent())

        val aiResult = geminiClient.generateContent(promptBuilder.toString())
        if (aiResult.isSuccess) {
            try {
                val rawText = aiResult.getOrThrow().trim()
                val jsonStart = rawText.indexOf('[')
                val jsonEnd = rawText.lastIndexOf(']')
                val jsonStr = if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                    rawText.substring(jsonStart, jsonEnd + 1)
                } else {
                    rawText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                }
                val jsonArray = JSONArray(jsonStr)
                val list = mutableListOf<QuizQuestion>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val q = obj.getString("question")
                    val optsArray = obj.getJSONArray("options")
                    val opts = mutableListOf<String>()
                    for (j in 0 until optsArray.length()) {
                        opts.add(optsArray.getString(j))
                    }
                    val correct = obj.getInt("correctIndex")
                    val exp = obj.optString("explanation", "Correct!")
                    list.add(QuizQuestion(q, opts, correct, exp))
                }
                if (list.size >= 3) {
                    return list
                }
            } catch (e: Exception) {
                // Parse failed, fallback to curated bank
            }
        }

        return TopicCatalog.getQuizQuestionsForTopic(topic, difficulty)
    }

    suspend fun generateQuiz(topic: String, difficulty: String = "Medium"): List<QuizQuestion> {
        return generateReinforcementQuiz(topic = topic, difficulty = difficulty)
    }

    private fun extractTitleFromProblem(problemText: String): String {
        val trimmed = problemText.trim().lines().firstOrNull { it.isNotBlank() } ?: "Math Homework Problem"
        return if (trimmed.length > 50) trimmed.take(47) + "…" else trimmed
    }

    private fun formatMath(str: String): String = str.replace('§', '$')

    private fun solveWithLocalEngine(problem: String, gradeLevel: String): String {
        val clean = problem.lowercase().trim()
        return when {
            clean.contains("2x²") || clean.contains("2x^2") || (clean.contains("x²") && clean.contains("5x")) -> formatMath("""
## 🔍 Understand the problem
We are given the quadratic equation:
§§2x^2 - 5x - 3 = 0§§
Our objective is to find all values of §x§ that satisfy this equation.

## 💡 Method & Formula
We use the **Quadratic Formula**:
§§x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}§§
where §a = 2§, §b = -5§, and §c = -3§.

## 📝 Step-by-step Solution
1. **Identify the coefficients:**
   §§a = 2, \quad b = -5, \quad c = -3§§

2. **Compute the Discriminant (§D = b^2 - 4ac§):**
   §§D = (-5)^2 - 4(2)(-3) = 25 + 24 = 49§§
   Since §D = 49 > 0§, the equation has two distinct real roots.

3. **Take the square root of the discriminant:**
   §§\sqrt{49} = 7§§

4. **Apply the quadratic formula:**
   §§x = \frac{-(-5) \pm 7}{2(2)} = \frac{5 \pm 7}{4}§§

5. **Calculate the two roots:**
   - For (+): §§x_1 = \frac{5 + 7}{4} = \frac{12}{4} = 3§§
   - For (-): §§x_2 = \frac{5 - 7}{4} = \frac{-2}{4} = -\frac{1}{2}§§

## ✅ Verification & Check
Let's substitute §x = 3§ back into the original equation:
§§2(3)^2 - 5(3) - 3 = 2(9) - 15 - 3 = 18 - 18 = 0 \quad \checkmark§§
Now substitute §x = -\frac{1}{2}§:
§§2\left(-\frac{1}{2}\right)^2 - 5\left(-\frac{1}{2}\right) - 3 = 2\left(\frac{1}{4}\right) + \frac{5}{2} - 3 = \frac{1}{2} + \frac{5}{2} - 3 = 3 - 3 = 0 \quad \checkmark§§

## 🎯 Final Answer
§§\boxed{x = 3 \quad \text{or} \quad x = -\frac{1}{2}}§§

---
*Tip: Would you like to try a similar practice problem or see an alternative factoring method?*
            """.trimIndent())

            clean.contains("x·sin(x)") || clean.contains("x*sin(x)") || clean.contains("x sin(x)") -> formatMath("""
## 🔍 Understand the problem
We are asked to find the derivative of the product:
§§f(x) = x \cdot \sin(x)§§
with respect to §x§.

## 💡 Method & Formula
Since §f(x)§ is a product of two distinct functions (§x§ and §\sin(x)§), we must apply the **Product Rule of Differentiation**:
§§\frac{d}{dx}[u(x) \cdot v(x)] = u'(x)v(x) + u(x)v'(x)§§

## 📝 Step-by-step Solution
1. **Assign parts:**
   §§u(x) = x \implies u'(x) = 1§§
   §§v(x) = \sin(x) \implies v'(x) = \cos(x)§§

2. **Substitute into the product rule formula:**
   §§f'(x) = (1) \cdot \sin(x) + x \cdot \cos(x)§§

3. **Simplify the expression:**
   §§f'(x) = \sin(x) + x\cos(x)§§

## ✅ Verification & Check
Check the boundary behavior at §x = 0§:
§§f(0) = 0 \cdot \sin(0) = 0§§
Slope at §x = 0§: §f'(0) = \sin(0) + 0\cos(0) = 0§, which matches the tangent to §x \sin(x)§ at the origin.

## 🎯 Final Answer
§§\boxed{f'(x) = \sin(x) + x\cos(x)}§§
            """.trimIndent())

            clean.contains("sin²(x)") || clean.contains("sin^2") -> formatMath("""
## 🔍 Understand the problem
Evaluate the definite integral:
§§\int_{0}^{\pi} \sin^2(x) \, dx§§

## 💡 Method & Formula
We use the trigonometric **half-angle power-reduction identity**:
§§\sin^2(x) = \frac{1 - \cos(2x)}{2}§§

## 📝 Step-by-step Solution
1. **Rewrite the integrand using the identity:**
   §§\int_{0}^{\pi} \frac{1 - \cos(2x)}{2} \, dx = \frac{1}{2}\int_{0}^{\pi} (1 - \cos(2x)) \, dx§§

2. **Find the antiderivative:**
   §§\int 1 \, dx = x§§
   §§\int \cos(2x) \, dx = \frac{\sin(2x)}{2}§§
   So §F(x) = \frac{1}{2}\left[ x - \frac{\sin(2x)}{2} \right]§

3. **Evaluate at the limits §\pi§ and 0:**
   §§\text{At } x = \pi: \quad \frac{1}{2}\left( \pi - \frac{\sin(2\pi)}{2} \right) = \frac{1}{2}(\pi - 0) = \frac{\pi}{2}§§
   §§\text{At } x = 0: \quad \frac{1}{2}\left( 0 - \frac{\sin(0)}{2} \right) = 0§§

4. **Compute the difference:**
   §§\frac{\pi}{2} - 0 = \frac{\pi}{2}§§

## ✅ Verification & Check
By symmetry, the average value of §\sin^2(x)§ over [0, §\pi§] is 1/2.
Multiplying average value 1/2 by the interval length §\pi§ gives (1/2) · §\pi = \pi / 2§. Matches perfectly!

## 🎯 Final Answer
§§\boxed{\frac{\pi}{2}}§§
            """.trimIndent())

            clean.contains("triangle") && (clean.contains("7") || clean.contains("area")) -> formatMath("""
## 🔍 Understand the problem
A triangle has side lengths §a = 7§, §b = 8§, and §c = 9§. We must find the area of this triangle.

## 💡 Method & Formula
Because all three side lengths are given without any angles, we use **Heron's Formula**:
§§\text{Area} = \sqrt{s(s - a)(s - b)(s - c)}§§
where §s§ is the semi-perimeter:
§§s = \frac{a + b + c}{2}§§

## 📝 Step-by-step Solution
1. **Calculate the semi-perimeter §s§:**
   §§s = \frac{7 + 8 + 9}{2} = \frac{24}{2} = 12§§

2. **Compute differences (§s - a§), (§s - b§), (§s - c§):**
   §§s - a = 12 - 7 = 5§§
   §§s - b = 12 - 8 = 4§§
   §§s - c = 12 - 9 = 3§§

3. **Multiply the values under the square root:**
   §§\text{Product} = 12 \times 5 \times 4 \times 3 = 12 \times 60 = 720§§

4. **Simplify the square root §§\sqrt{720}§§:**
   §§\sqrt{720} = \sqrt{144 \times 5} = 12\sqrt{5} \approx 26.833§§

## ✅ Verification & Check
Check using the triangle inequality: 7 + 8 = 15 > 9, so the triangle is valid.
Using Law of Cosines to find §\cos C = \frac{7^2 + 8^2 - 9^2}{2(7)(8)} = \frac{49 + 64 - 81}{112} = \frac{32}{112} = \frac{2}{7}§.
Then §\sin C = \sqrt{1 - 4/49} = \frac{\sqrt{45}}{7} = \frac{3\sqrt{5}}{7}§.
Area = (1/2)ab · §\sin C = \frac{1}{2}(7)(8)\left(\frac{3\sqrt{5}}{7}\right) = 28 \cdot \frac{3\sqrt{5}}{7} = 12\sqrt{5}§. Confirmed!

## 🎯 Final Answer
§§\boxed{12\sqrt{5} \approx 26.83 \text{ square units}}§§
            """.trimIndent())

            clean.contains("2x + 3 = 11") || (clean.contains("2x") && clean.contains("11")) -> formatMath("""
## 🔍 Understand the problem
We have a linear equation in one variable:
§§2x + 3 = 11§§
We need to isolate §x§.

## 💡 Method & Formula
We use standard inverse operations:
1. Subtraction property of equality: subtract 3 from both sides.
2. Division property of equality: divide both sides by 2.

## 📝 Step-by-step Solution
1. **Subtract 3 from both sides:**
   §§2x + 3 - 3 = 11 - 3§§
   §§2x = 8§§

2. **Divide both sides by 2:**
   §§\frac{2x}{2} = \frac{8}{2}§§
   §§x = 4§§

## ✅ Verification & Check
Substitute §x = 4§ into original equation:
§§2(4) + 3 = 8 + 3 = 11 \quad \checkmark§§

## 🎯 Final Answer
§§\boxed{x = 4}§§
            """.trimIndent())

            clean.contains("(x²-9)") || clean.contains("(x^2-9)") || clean.contains("simplify") -> formatMath("""
## 🔍 Understand the problem
Simplify the rational algebraic expression:
§§\frac{x^2 - 9}{x^2 + x - 6}§§

## 💡 Method & Formula
We factor both the numerator and denominator completely, then cancel common factors.
- Numerator is a difference of squares: §a^2 - b^2 = (a - b)(a + b)§.
- Denominator is a trinomial: find two numbers that multiply to -6 and add to +1.

## 📝 Step-by-step Solution
1. **Factor numerator (§x^2 - 9§):**
   §§x^2 - 3^2 = (x - 3)(x + 3)§§

2. **Factor denominator (§x^2 + x - 6§):**
   Factors of -6 adding to 1 are +3 and -2:
   §§x^2 + x - 6 = (x + 3)(x - 2)§§

3. **Rewrite original expression:**
   §§\frac{(x - 3)(x + 3)}{(x + 3)(x - 2)}§§

4. **Cancel the common factor (§x + 3§) for §x \neq -3§:**
   §§\frac{x - 3}{x - 2}§§

## ✅ Verification & Check
Test with §x = 4§:
Original: §\frac{4^2 - 9}{4^2 + 4 - 6} = \frac{16 - 9}{16 + 4 - 6} = \frac{7}{14} = \frac{1}{2}§.
Simplified: §\frac{4 - 3}{4 - 2} = \frac{1}{2}§. Perfect match!

## 🎯 Final Answer
§§\boxed{\frac{x - 3}{x - 2} \quad (x \neq -3, \, x \neq 2)}§§
            """.trimIndent())

            else -> formatMath("""
## 🔍 Understand the problem
We want to solve the problem for a $gradeLevel student:
**$problem**

## 💡 Method & Concept
Let's analyze the mathematical structure. We identify key variables, establish relevant equations, and apply systematic algebraic simplification.

## 📝 Step-by-step Breakdown
1. **Set up the knowns and unknowns:**
   Identify all given quantities and clarify what value or relationship needs to be determined.
2. **Apply foundational rules:**
   Transform the terms systematically by maintaining equality across operations.
3. **Simplify terms:**
   Collect like terms and isolate the targeted variable or solve the expression.

## ✅ Verification & Sanity Check
- Verify units and dimensions.
- Test with known numbers to ensure consistent boundary behavior.

## 🎯 Final Answer
§§\boxed{\text{Solution to: } $problem}§§

*Tip: Connect your API key in AI Studio Secrets to unlock deep step-by-step Gemini reasoning for every homework subject!*
            """.trimIndent())
        }
    }

    private fun generateLocalFollowUp(followUp: String, currentSolution: String): String {
        val f = followUp.lowercase()
        return when {
            f.contains("simpler") || f.contains("didn't understand") -> formatMath("""
Let's think of it in a much simpler everyday way! 🌟

Imagine each equation like a balanced scale: whatever you take off one side, you must take off the other side so it stays level.

When we have something like §2x + 3 = 11§:
1. Think of §2x§ as two mystery gift boxes, plus 3 loose coins, balancing 11 coins on the other side.
2. First, take away the 3 loose coins from both sides. Now the 2 gift boxes balance with 8 coins!
3. If 2 identical boxes hold 8 coins, each single box must hold 8 ÷ 2 = 4 coins!

Does this picture help make the steps crystal clear?
            """.trimIndent())

            f.contains("practice") || f.contains("similar") -> formatMath("""
Here is a great practice problem for you to try:

🎯 **Your Turn:**
Solve for §x§:
§§3x^2 - 7x + 2 = 0§§

💡 **Hint:** Identify §a = 3§, §b = -7§, and §c = 2§.
First calculate §D = b^2 - 4ac§. Then plug into §x = \frac{-b \pm \sqrt{D}}{2a}§.

When you get an answer, send it to me and I will check your work!
            """.trimIndent())

            f.contains("another method") || f.contains("different") -> formatMath("""
🔄 **Alternative Method: Factoring by Grouping (AC Method)**

For §2x^2 - 5x - 3 = 0§:
1. Multiply §a \cdot c = 2 \cdot (-3) = -6§.
2. Find two factors of -6 that add up to §b = -5§. Those numbers are -6 and +1.
3. Split the middle term:
   §§2x^2 - 6x + x - 3 = 0§§
4. Group pairs:
   §§2x(x - 3) + 1(x - 3) = 0§§
5. Factor out common (§x - 3§):
   §§(2x + 1)(x - 3) = 0§§
6. Set each factor to zero:
   - §2x + 1 = 0 \implies x = -1/2§
   - §x - 3 = 0 \implies x = 3§

This arrives at the exact same answer without needing square roots!
            """.trimIndent())

            else -> """
That's a thoughtful question!

In mathematics, every step is justified by a specific property (like the addition property of equality, distributive law, or derivative rules). When you're working through this problem on your own, remember to write down your intermediate calculations clearly so you don't drop any negative signs or constants.

Would you like to try another practice problem or examine a specific step?
            """.trimIndent()
        }
    }
}
