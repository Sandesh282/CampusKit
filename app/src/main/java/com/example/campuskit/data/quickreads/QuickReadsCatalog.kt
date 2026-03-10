package com.example.campuskit.data.quickreads

/**
 * Represents a curated article/blog link for the Quick Reads feature.
 *
 * @param id unique identifier
 * @param title article title
 * @param source platform name (e.g. "Medium", "GitHub", "Dev.to")
 * @param url external link
 * @param category grouping tag for UI sections
 */
data class QuickRead(
    val id: String,
    val title: String,
    val source: String,
    val url: String,
    val category: String,
)

/** Static catalog of curated reads, mirroring StudentHub's blogs.json + extras. */
object QuickReadsCatalog {

    fun getAll(): List<QuickRead> = listOf(
        // — Coding —
        QuickRead("1", "Understanding JavaScript Closures", "Medium", "https://medium.com/some-article", "Coding"),
        QuickRead("2", "Building a REST API with Django Rest Framework", "GitHub", "https://github.com/some-repo", "Coding"),
        QuickRead("3", "CSS Grid vs Flexbox: When to Use What", "Dev.to", "https://dev.to/some-article", "Coding"),
        QuickRead("4", "Understanding OAuth 2.0 for Authentication", "GitHub", "https://github.com/oauth-guide", "Coding"),

        // — AI & ML —
        QuickRead("5", "Machine Learning Basics: A Beginner's Guide", "Medium", "https://medium.com/ml-basics", "AI & ML"),
        QuickRead("6", "Intro to Neural Networks", "Medium", "https://medium.com/neural-networks", "AI & ML"),

        // — College Life —
        QuickRead("7", "How to Ace Your Semester Exams", "Blog", "https://example.com/exam-tips", "College Life"),
        QuickRead("8", "Time Management for Engineering Students", "Blog", "https://example.com/time-mgmt", "College Life"),

        // — Open Source —
        QuickRead("9", "Your First Open Source Contribution", "GitHub", "https://github.com/firstcontributions", "Open Source"),
        QuickRead("10", "How to Write Good Git Commit Messages", "Dev.to", "https://dev.to/git-commits", "Open Source"),
    )

    fun getCategories(): List<String> = getAll().map { it.category }.distinct()
}
