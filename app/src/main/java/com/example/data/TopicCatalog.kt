package com.example.data

data class TopicLesson(
    val id: String,
    val category: String,
    val title: String,
    val summary: String,
    val formulas: List<String>,
    val workedExampleQuestion: String,
    val workedExampleSteps: List<String>,
    val workedExampleFinal: String,
    val commonMistakes: List<String>,
    val practicePrompt: String
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object TopicCatalog {
    val topics: List<TopicLesson> = listOf(
        TopicLesson(
            id = "quadratic_eq",
            category = "Algebra",
            title = "Quadratic Equations",
            summary = "Equations of the second degree where the highest exponent of the variable is 2. Standard form is ax² + bx + c = 0.",
            formulas = listOf(
                "Standard form: ax² + bx + c = 0",
                "Quadratic Formula: x = (-b ± √(b² - 4ac)) / (2a)",
                "Discriminant: D = b² - 4ac (D > 0: two real roots; D = 0: one real root; D < 0: complex roots)"
            ),
            workedExampleQuestion = "Solve 2x² - 5x - 3 = 0",
            workedExampleSteps = listOf(
                "Identify coefficients: a = 2, b = -5, c = -3.",
                "Calculate discriminant: D = (-5)² - 4(2)(-3) = 25 + 24 = 49.",
                "Apply the quadratic formula: x = (-(-5) ± √49) / (2 · 2) = (5 ± 7) / 4.",
                "Calculate first root: x₁ = (5 + 7) / 4 = 12 / 4 = 3.",
                "Calculate second root: x₂ = (5 - 7) / 4 = -2 / 4 = -1/2."
            ),
            workedExampleFinal = "x = 3  or  x = -1/2",
            commonMistakes = listOf(
                "Forgetting the negative sign in '-b' when b is already negative (e.g., -(-5) becomes +5).",
                "Dividing only the square root part by 2a instead of the whole numerator.",
                "Squaring negative numbers without parentheses: (-5)² is +25, not -25."
            ),
            practicePrompt = "Solve 3x² - 10x + 3 = 0"
        ),
        TopicLesson(
            id = "linear_eq",
            category = "Algebra",
            title = "Linear Equations & Systems",
            summary = "Equations where variables have degree 1, creating straight lines on coordinate graphs. Systems find the intersection point.",
            formulas = listOf(
                "Slope-intercept form: y = mx + b",
                "Point-slope form: y - y₁ = m(x - x₁)",
                "Slope: m = (y₂ - y₁) / (x₂ - x₁)"
            ),
            workedExampleQuestion = "Solve the system: 2x + y = 9 and x - y = 3",
            workedExampleSteps = listOf(
                "Notice that y has opposite signs in both equations (+y and -y).",
                "Add the two equations together: (2x + x) + (y - y) = 9 + 3.",
                "Simplify: 3x = 12, which gives x = 4.",
                "Substitute x = 4 into x - y = 3: 4 - y = 3, so y = 1.",
                "Verify in the first equation: 2(4) + 1 = 8 + 1 = 9 (Correct!)."
            ),
            workedExampleFinal = "(x, y) = (4, 1)",
            commonMistakes = listOf(
                "Forgetting to distribute negative signs when subtracting equations.",
                "Calculating slope as Δx / Δy instead of Δy / Δx (Rise over Run)."
            ),
            practicePrompt = "Solve: 3x + 2y = 16 and x - 2y = 0"
        ),
        TopicLesson(
            id = "derivatives",
            category = "Calculus",
            title = "Derivatives & Rate of Change",
            summary = "The derivative measures the instantaneous rate of change of a function, representing the slope of the tangent line.",
            formulas = listOf(
                "Power Rule: d/dx [xⁿ] = n · xⁿ⁻¹",
                "Product Rule: (f · g)' = f'g + fg'",
                "Quotient Rule: (f / g)' = (f'g - fg') / g²",
                "Chain Rule: d/dx [f(g(x))] = f'(g(x)) · g'(x)"
            ),
            workedExampleQuestion = "Find the derivative of f(x) = x · sin(x)",
            workedExampleSteps = listOf(
                "Identify product of two functions: u(x) = x and v(x) = sin(x).",
                "Differentiate u(x): u'(x) = 1.",
                "Differentiate v(x): v'(x) = cos(x).",
                "Apply Product Rule: f'(x) = u'v + uv'.",
                "Substitute: f'(x) = 1 · sin(x) + x · cos(x) = sin(x) + x·cos(x)."
            ),
            workedExampleFinal = "f'(x) = sin(x) + x·cos(x)",
            commonMistakes = listOf(
                "Differentiating a product as f'(x) · g'(x) instead of using the Product Rule.",
                "Forgetting the inner derivative when applying the Chain Rule.",
                "Mixing up derivative of sin(x) [= cos(x)] with derivative of cos(x) [= -sin(x)]."
            ),
            practicePrompt = "Find d/dx of x² · eˣ"
        ),
        TopicLesson(
            id = "integrals",
            category = "Calculus",
            title = "Integrals & Area Under Curves",
            summary = "Integration is the reverse process of differentiation and computes total accumulation or the area beneath a curve.",
            formulas = listOf(
                "Power Rule: ∫ xⁿ dx = (xⁿ⁺¹)/(n + 1) + C  (for n ≠ -1)",
                "Exponential: ∫ eˣ dx = eˣ + C",
                "Fundamental Theorem of Calculus: ∫ₐᵇ f(x) dx = F(b) - F(a)"
            ),
            workedExampleQuestion = "Evaluate ∫ from 0 to 2 of (3x² + 2x) dx",
            workedExampleSteps = listOf(
                "Find anti-derivative of each term: ∫ 3x² dx = 3(x³/3) = x³.",
                "Find anti-derivative of second term: ∫ 2x dx = 2(x²/2) = x².",
                "Anti-derivative function is F(x) = x³ + x².",
                "Apply limits [0, 2]: F(2) - F(0).",
                "Calculate F(2) = 2³ + 2² = 8 + 4 = 12.",
                "Calculate F(0) = 0³ + 0² = 0.",
                "Compute difference: 12 - 0 = 12."
            ),
            workedExampleFinal = "12",
            commonMistakes = listOf(
                "Forgetting the constant of integration (+ C) for indefinite integrals.",
                "Evaluating limits backwards as F(a) - F(b) instead of F(b) - F(a)."
            ),
            practicePrompt = "Evaluate ∫ from 1 to 3 of (2x + 1) dx"
        ),
        TopicLesson(
            id = "pythagoras_geom",
            category = "Geometry",
            title = "Triangles & Pythagoras",
            summary = "Fundamental geometric principles governing right triangles and general planar polygons.",
            formulas = listOf(
                "Pythagorean Theorem: a² + b² = c² (for right triangles)",
                "Area of Triangle: A = ½ · base · height",
                "Heron's Formula: A = √(s(s - a)(s - b)(s - c)) where semiperimeter s = (a + b + c) / 2"
            ),
            workedExampleQuestion = "A triangle has sides 7, 8, 9. Find its area.",
            workedExampleSteps = listOf(
                "Notice all three sides are known (a = 7, b = 8, c = 9). Use Heron's formula.",
                "Compute semiperimeter: s = (7 + 8 + 9) / 2 = 24 / 2 = 12.",
                "Calculate the side differences: (s - a) = 12 - 7 = 5, (s - b) = 12 - 8 = 4, (s - c) = 12 - 9 = 3.",
                "Multiply terms under root: 12 · 5 · 4 · 3 = 12 · 60 = 720.",
                "Simplify square root: √720 = √(144 · 5) = 12√5 ≈ 26.83."
            ),
            workedExampleFinal = "Area = 12√5 ≈ 26.83 sq units",
            commonMistakes = listOf(
                "Applying a² + b² = c² to non-right triangles.",
                "Forgetting to divide the perimeter by 2 to get the semiperimeter in Heron's formula."
            ),
            practicePrompt = "Find the hypotenuse of a right triangle with legs 5 and 12."
        ),
        TopicLesson(
            id = "trig_basics",
            category = "Trigonometry",
            title = "Sine, Cosine & Tangent",
            summary = "Trigonometric ratios relate the angles of a right triangle to the ratios of its side lengths (SOH-CAH-TOA).",
            formulas = listOf(
                "sin(θ) = Opposite / Hypotenuse",
                "cos(θ) = Adjacent / Hypotenuse",
                "tan(θ) = Opposite / Adjacent = sin(θ) / cos(θ)",
                "Pythagorean Identity: sin²(θ) + cos²(θ) = 1"
            ),
            workedExampleQuestion = "If sin(θ) = 3/5 and θ is acute, find cos(θ) and tan(θ).",
            workedExampleSteps = listOf(
                "Recall Pythagorean Identity: sin²(θ) + cos²(θ) = 1.",
                "Substitute sin(θ) = 3/5: (3/5)² + cos²(θ) = 1.",
                "Calculate: 9/25 + cos²(θ) = 1, so cos²(θ) = 1 - 9/25 = 16/25.",
                "Take square root (positive since θ is acute): cos(θ) = 4/5.",
                "Compute tangent: tan(θ) = sin(θ) / cos(θ) = (3/5) / (4/5) = 3/4."
            ),
            workedExampleFinal = "cos(θ) = 4/5,  tan(θ) = 3/4",
            commonMistakes = listOf(
                "Confusing opposite with adjacent side in reference to angle θ.",
                "Using Degree mode on a calculator when radians are requested, or vice versa."
            ),
            practicePrompt = "If tan(θ) = 1 for an acute angle θ, what is θ in degrees?"
        ),
        TopicLesson(
            id = "fractions_arith",
            category = "Arithmetic",
            title = "Fractions & Percentages",
            summary = "Foundational arithmetic for rational numbers, common denominators, and proportional scaling.",
            formulas = listOf(
                "Addition/Subtraction: a/b ± c/d = (ad ± bc) / bd",
                "Multiplication: (a/b) · (c/d) = ac / bd",
                "Division: (a/b) ÷ (c/d) = (a/b) · (d/c) = ad / bc",
                "Percentage: (Part / Whole) · 100%"
            ),
            workedExampleQuestion = "Evaluate 2/3 + 3/5 - 1/2",
            workedExampleSteps = listOf(
                "Find the Least Common Denominator (LCD) of 3, 5, and 2: LCD = 30.",
                "Convert 2/3: (2 · 10) / (3 · 10) = 20/30.",
                "Convert 3/5: (3 · 6) / (5 · 6) = 18/30.",
                "Convert 1/2: (1 · 15) / (2 · 15) = 15/30.",
                "Combine numerators: (20 + 18 - 15) / 30 = 23/30."
            ),
            workedExampleFinal = "23/30",
            commonMistakes = listOf(
                "Adding numerators and denominators straight across: a/b + c/d ≠ (a+c)/(b+d).",
                "Forgetting to flip the second fraction when dividing."
            ),
            practicePrompt = "Evaluate (3/4) ÷ (2/5)"
        ),
        TopicLesson(
            id = "stats_prob",
            category = "Statistics",
            title = "Mean, Median & Probability",
            summary = "Measures of central tendency summarize distributions; probability quantifies the likelihood of events.",
            formulas = listOf(
                "Mean: x̄ = (∑ x) / n",
                "Median: Middle value of ordered dataset (or average of two middle values)",
                "Probability: P(Event) = (Favorable outcomes) / (Total possible outcomes)"
            ),
            workedExampleQuestion = "Find the mean and median of: 4, 8, 3, 9, 6, 8, 4",
            workedExampleSteps = listOf(
                "Sort the data in ascending order: 3, 4, 4, 6, 8, 8, 9. (Total items n = 7).",
                "Calculate Mean: Sum = 3 + 4 + 4 + 6 + 8 + 8 + 9 = 42. Mean = 42 / 7 = 6.",
                "Find Median: The 4th item in ordered list is 6.",
                "Both mean and median equal 6."
            ),
            workedExampleFinal = "Mean = 6,  Median = 6",
            commonMistakes = listOf(
                "Finding the median before sorting the numbers in ascending order.",
                "Treating probability as an absolute number > 1 instead of between 0 and 1 (or 0% - 100%)."
            ),
            practicePrompt = "What is the probability of rolling a sum of 7 with two standard 6-sided dice?"
        )
    )

    fun getQuizQuestionsForTopic(topicQuery: String, difficulty: String = "Medium"): List<QuizQuestion> {
        val q = topicQuery.lowercase()
        return when {
            q.contains("quad") || q.contains("algebra") -> listOf(
                QuizQuestion(
                    question = "What is the discriminant of the quadratic equation 2x² - 4x + 2 = 0?",
                    options = listOf("0", "16", "-16", "32"),
                    correctIndex = 0,
                    explanation = "Discriminant D = b² - 4ac = (-4)² - 4(2)(2) = 16 - 16 = 0. This indicates exactly one real root."
                ),
                QuizQuestion(
                    question = "What are the solutions to x² - 9 = 0?",
                    options = listOf("x = ±3", "x = 3 only", "x = 9", "x = ±9"),
                    correctIndex = 0,
                    explanation = "x² = 9 leads to x = ±√9 = ±3."
                ),
                QuizQuestion(
                    question = "In standard form ax² + bx + c = 0, what does the vertex x-coordinate equal?",
                    options = listOf("-b / (2a)", "b / (2a)", "-c / a", "√(b² - 4ac)"),
                    correctIndex = 0,
                    explanation = "The axis of symmetry and vertex x-coordinate is x = -b / (2a)."
                ),
                QuizQuestion(
                    question = "If x² + 5x + 6 = 0, factoring yields:",
                    options = listOf("(x + 2)(x + 3) = 0", "(x - 2)(x - 3) = 0", "(x + 1)(x + 6) = 0", "(x - 1)(x - 6) = 0"),
                    correctIndex = 0,
                    explanation = "Two numbers that multiply to 6 and add to 5 are 2 and 3: (x + 2)(x + 3) = 0."
                ),
                QuizQuestion(
                    question = "If the discriminant of ax² + bx + c = 0 is negative, how many real roots exist?",
                    options = listOf("0 real roots", "1 real root", "2 real roots", "Infinitely many"),
                    correctIndex = 0,
                    explanation = "When D < 0, the square root involves an imaginary unit, giving two complex roots and 0 real roots."
                )
            )
            q.contains("deriv") || q.contains("calc") -> listOf(
                QuizQuestion(
                    question = "What is d/dx of 5x³ - 4x + 7?",
                    options = listOf("15x² - 4", "15x³ - 4", "15x²", "5x² - 4"),
                    correctIndex = 0,
                    explanation = "Using the power rule: d/dx(5x³) = 15x², d/dx(-4x) = -4, and constant 7 differentiates to 0."
                ),
                QuizQuestion(
                    question = "What is the derivative of e²ˣ?",
                    options = listOf("2e²ˣ", "e²ˣ", "2x·e²ˣ", "eˣ"),
                    correctIndex = 0,
                    explanation = "By chain rule, d/dx[e²ˣ] = e²ˣ · d/dx(2x) = 2e²ˣ."
                ),
                QuizQuestion(
                    question = "What is d/dx of ln(x) for x > 0?",
                    options = listOf("1/x", "x", "eˣ", "1 / x²"),
                    correctIndex = 0,
                    explanation = "The standard derivative of natural logarithm ln(x) is 1/x."
                ),
                QuizQuestion(
                    question = "If position is s(t) = t² + 4t, what is the instantaneous velocity at t = 3?",
                    options = listOf("10", "21", "6", "13"),
                    correctIndex = 0,
                    explanation = "Velocity v(t) = s'(t) = 2t + 4. At t = 3, v(3) = 2(3) + 4 = 10."
                ),
                QuizQuestion(
                    question = "What is the derivative of cos(x)?",
                    options = listOf("-sin(x)", "sin(x)", "-cos(x)", "tan(x)"),
                    correctIndex = 0,
                    explanation = "d/dx[cos(x)] = -sin(x)."
                )
            )
            q.contains("integ") -> listOf(
                QuizQuestion(
                    question = "What is ∫ 4x³ dx?",
                    options = listOf("x⁴ + C", "12x² + C", "4x⁴ + C", "x³ + C"),
                    correctIndex = 0,
                    explanation = "∫ 4x³ dx = 4 · (x⁴ / 4) + C = x⁴ + C."
                ),
                QuizQuestion(
                    question = "Evaluate the definite integral ∫ from 0 to 3 of 2x dx:",
                    options = listOf("9", "6", "18", "12"),
                    correctIndex = 0,
                    explanation = "Antiderivative of 2x is x². Evaluated from 0 to 3: 3² - 0² = 9."
                ),
                QuizQuestion(
                    question = "What is ∫ (1/x) dx for x > 0?",
                    options = listOf("ln(x) + C", "-1/x² + C", "x⁻² + C", "eˣ + C"),
                    correctIndex = 0,
                    explanation = "Standard integral: ∫ (1/x) dx = ln|x| + C."
                ),
                QuizQuestion(
                    question = "Integration by parts formula is:",
                    options = listOf("∫ u dv = uv - ∫ v du", "∫ u dv = uv + ∫ v du", "∫ u dv = u'v + uv'", "∫ u dv = (u·v) / 2"),
                    correctIndex = 0,
                    explanation = "Integration by parts comes from the product rule: ∫ u dv = uv - ∫ v du."
                ),
                QuizQuestion(
                    question = "Evaluate ∫ from 0 to 1 of eˣ dx:",
                    options = listOf("e - 1", "e", "1", "e + 1"),
                    correctIndex = 0,
                    explanation = "Antiderivative is eˣ. At limits: e¹ - e⁰ = e - 1."
                )
            )
            q.contains("trig") || q.contains("triangle") -> listOf(
                QuizQuestion(
                    question = "What is the value of sin²(45°) + cos²(45°)?",
                    options = listOf("1", "0", "0.5", "√2"),
                    correctIndex = 0,
                    explanation = "For ANY angle θ, the fundamental Pythagorean identity states sin²(θ) + cos²(θ) = 1."
                ),
                QuizQuestion(
                    question = "In a 30°-60°-90° triangle with hypotenuse 10, what is the side opposite the 30° angle?",
                    options = listOf("5", "5√3", "10√3", "2.5"),
                    correctIndex = 0,
                    explanation = "The side opposite 30° is half the hypotenuse: 10 / 2 = 5."
                ),
                QuizQuestion(
                    question = "What is tan(45°)?",
                    options = listOf("1", "0", "√3", "1/√3"),
                    correctIndex = 0,
                    explanation = "At 45°, opposite and adjacent sides are equal, so tan(45°) = 1."
                ),
                QuizQuestion(
                    question = "A right triangle has legs 6 and 8. The hypotenuse is:",
                    options = listOf("10", "14", "12", "7"),
                    correctIndex = 0,
                    explanation = "By Pythagorean theorem: c = √(6² + 8²) = √(36 + 64) = √100 = 10."
                ),
                QuizQuestion(
                    question = "What is the period of the basic sine function f(x) = sin(x)?",
                    options = listOf("2π", "π", "π / 2", "4π"),
                    correctIndex = 0,
                    explanation = "The sine function repeats its pattern every full circle, which is 2π radians (360°)."
                )
            )
            else -> listOf(
                QuizQuestion(
                    question = "What is 15% of 80?",
                    options = listOf("12", "15", "10", "14"),
                    correctIndex = 0,
                    explanation = "0.15 · 80 = 12."
                ),
                QuizQuestion(
                    question = "Solve for x: 3x + 7 = 22",
                    options = listOf("x = 5", "x = 4", "x = 6", "x = 3"),
                    correctIndex = 0,
                    explanation = "3x = 22 - 7 = 15; x = 15 / 3 = 5."
                ),
                QuizQuestion(
                    question = "What is the median of 3, 7, 9, 12, 15?",
                    options = listOf("9", "7", "9.2", "12"),
                    correctIndex = 0,
                    explanation = "The numbers are already sorted. The middle (3rd) value is 9."
                ),
                QuizQuestion(
                    question = "Simplify (x² - 16) / (x - 4) for x ≠ 4:",
                    options = listOf("x + 4", "x - 4", "x + 2", "4"),
                    correctIndex = 0,
                    explanation = "Factor numerator as difference of squares: (x - 4)(x + 4) / (x - 4) = x + 4."
                ),
                QuizQuestion(
                    question = "What is 2/3 + 1/4?",
                    options = listOf("11/12", "3/7", "3/12", "8/12"),
                    correctIndex = 0,
                    explanation = "Common denominator is 12: 8/12 + 3/12 = 11/12."
                )
            )
        }
    }
}
