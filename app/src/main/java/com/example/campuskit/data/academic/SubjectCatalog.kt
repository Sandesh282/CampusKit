package com.example.campuskit.data.academic

import com.example.campuskit.data.academic.local.entity.OfferingEntity
import com.example.campuskit.data.academic.local.entity.SubjectEntity
import com.example.campuskit.domain.academic.model.Program

/**
 * Static subject catalog per program/semester.
 * This seeds Room locally so subjects appear instantly
 * without requiring an API call.
 */
object SubjectCatalog {

    fun getSubjects(program: Program, semester: Int): List<SubjectEntity> {
        val key = "${program.name}_$semester"
        return catalog[key] ?: emptyList()
    }

    fun getOfferings(program: Program, semester: Int): List<OfferingEntity> {
        return getSubjects(program, semester).map { subject ->
            OfferingEntity(
                id = "${program.name}-$semester-${subject.code}",
                subjectCode = subject.code,
                program = program.name,
                semester = semester,
                isElective = false,
            )
        }
    }

    // ── Catalog Data ──
    private val catalog = mapOf(
        // ── CSE ──
        "CSE_1" to listOf(
            SubjectEntity("MA101", "Mathematics - I", "Calculus and Linear Algebra"),
            SubjectEntity("PH101", "Physics", "Engineering Physics"),
            SubjectEntity("CS101", "Programming Fundamentals", "C Programming"),
            SubjectEntity("EE101", "Basic Electrical Engineering", null),
            SubjectEntity("HS101", "English Communication", null),
        ),
        "CSE_2" to listOf(
            SubjectEntity("MA102", "Mathematics - II", "Differential Equations & Transforms"),
            SubjectEntity("CS102", "Data Structures", "Arrays, Trees, Graphs"),
            SubjectEntity("CS103", "Digital Logic Design", null),
            SubjectEntity("CH101", "Chemistry", "Engineering Chemistry"),
            SubjectEntity("ME101", "Engineering Graphics", null),
        ),
        "CSE_3" to listOf(
            SubjectEntity("MA201", "Discrete Mathematics", null),
            SubjectEntity("CS201", "Object Oriented Programming", "Java/C++"),
            SubjectEntity("CS202", "Computer Organization", null),
            SubjectEntity("CS203", "Database Management Systems", null),
            SubjectEntity("HS201", "Economics", null),
        ),
        "CSE_4" to listOf(
            SubjectEntity("MA202", "Probability & Statistics", null),
            SubjectEntity("CS204", "Operating Systems", null),
            SubjectEntity("CS205", "Design & Analysis of Algorithms", null),
            SubjectEntity("CS206", "Theory of Computation", null),
            SubjectEntity("CS207", "Software Engineering", null),
        ),
        "CSE_5" to listOf(
            SubjectEntity("CS301", "Computer Networks", null),
            SubjectEntity("CS302", "Compiler Design", null),
            SubjectEntity("CS303", "Machine Learning", null),
            SubjectEntity("CS304", "Artificial Intelligence", null),
            SubjectEntity("CS305", "Elective - I", null),
        ),
        "CSE_6" to listOf(
            SubjectEntity("CS306", "Information Security", null),
            SubjectEntity("CS307", "Cloud Computing", null),
            SubjectEntity("CS308", "Deep Learning", null),
            SubjectEntity("CS309", "Elective - II", null),
            SubjectEntity("CS310", "Minor Project", null),
        ),
        "CSE_7" to listOf(
            SubjectEntity("CS401", "Distributed Systems", null),
            SubjectEntity("CS402", "Elective - III", null),
            SubjectEntity("CS403", "Elective - IV", null),
            SubjectEntity("CS404", "Major Project - I", null),
        ),
        "CSE_8" to listOf(
            SubjectEntity("CS405", "Elective - V", null),
            SubjectEntity("CS406", "Major Project - II", null),
        ),
        // ── IT ──
        "IT_1" to listOf(
            SubjectEntity("MA101", "Mathematics - I", "Calculus and Linear Algebra"),
            SubjectEntity("PH101", "Physics", "Engineering Physics"),
            SubjectEntity("IT101", "Introduction to IT", null),
            SubjectEntity("EE101", "Basic Electrical Engineering", null),
            SubjectEntity("HS101", "English Communication", null),
        ),
        "IT_2" to listOf(
            SubjectEntity("MA102", "Mathematics - II", "Differential Equations & Transforms"),
            SubjectEntity("IT102", "Data Structures", null),
            SubjectEntity("IT103", "Digital Electronics", null),
            SubjectEntity("CH101", "Chemistry", null),
            SubjectEntity("ME101", "Engineering Graphics", null),
        ),
        "IT_3" to listOf(
            SubjectEntity("MA201", "Discrete Mathematics", null),
            SubjectEntity("IT201", "Object Oriented Programming", null),
            SubjectEntity("IT202", "Computer Architecture", null),
            SubjectEntity("IT203", "Database Systems", null),
            SubjectEntity("HS201", "Economics", null),
        ),
        "IT_4" to listOf(
            SubjectEntity("MA202", "Probability & Statistics", null),
            SubjectEntity("IT204", "Operating Systems", null),
            SubjectEntity("IT205", "Algorithms", null),
            SubjectEntity("IT206", "Web Technologies", null),
            SubjectEntity("IT207", "Software Engineering", null),
        ),
        "IT_5" to listOf(
            SubjectEntity("IT301", "Computer Networks", null),
            SubjectEntity("IT302", "Information Security", null),
            SubjectEntity("IT303", "Mobile App Development", null),
            SubjectEntity("IT304", "Data Analytics", null),
            SubjectEntity("IT305", "Elective - I", null),
        ),
        "IT_6" to listOf(
            SubjectEntity("IT306", "Cloud Computing", null),
            SubjectEntity("IT307", "IoT Systems", null),
            SubjectEntity("IT308", "DevOps", null),
            SubjectEntity("IT309", "Elective - II", null),
            SubjectEntity("IT310", "Minor Project", null),
        ),
        "IT_7" to listOf(
            SubjectEntity("IT401", "Big Data Analytics", null),
            SubjectEntity("IT402", "Elective - III", null),
            SubjectEntity("IT403", "Elective - IV", null),
            SubjectEntity("IT404", "Major Project - I", null),
        ),
        "IT_8" to listOf(
            SubjectEntity("IT405", "Elective - V", null),
            SubjectEntity("IT406", "Major Project - II", null),
        ),
        // ── CSAI ──
        "CSAI_1" to listOf(
            SubjectEntity("MA101", "Mathematics - I", null),
            SubjectEntity("PH101", "Physics", null),
            SubjectEntity("CS101", "Programming Fundamentals", null),
            SubjectEntity("AI101", "Intro to AI & Data Science", null),
            SubjectEntity("HS101", "English Communication", null),
        ),
        "CSAI_2" to listOf(
            SubjectEntity("MA102", "Mathematics - II", null),
            SubjectEntity("CS102", "Data Structures", null),
            SubjectEntity("AI102", "Linear Algebra for ML", null),
            SubjectEntity("CS103", "Digital Logic Design", null),
            SubjectEntity("HS102", "Professional Ethics", null),
        ),
        "CSAI_3" to listOf(
            SubjectEntity("MA201", "Discrete Mathematics", null),
            SubjectEntity("CS201", "Object Oriented Programming", null),
            SubjectEntity("AI201", "Machine Learning Foundations", null),
            SubjectEntity("CS203", "Database Management Systems", null),
            SubjectEntity("AI202", "Probability & Statistics for AI", null),
        ),
        "CSAI_4" to listOf(
            SubjectEntity("CS204", "Operating Systems", null),
            SubjectEntity("CS205", "Algorithms", null),
            SubjectEntity("AI203", "Deep Learning", null),
            SubjectEntity("AI204", "Natural Language Processing", null),
            SubjectEntity("AI205", "Computer Vision", null),
        ),
        "CSAI_5" to listOf(
            SubjectEntity("CS301", "Computer Networks", null),
            SubjectEntity("AI301", "Reinforcement Learning", null),
            SubjectEntity("AI302", "AI Ethics & Fairness", null),
            SubjectEntity("AI303", "Advanced ML", null),
            SubjectEntity("AI304", "Elective - I", null),
        ),
        "CSAI_6" to listOf(
            SubjectEntity("AI305", "Generative AI", null),
            SubjectEntity("AI306", "Robotics & Autonomous Systems", null),
            SubjectEntity("AI307", "Elective - II", null),
            SubjectEntity("AI308", "Elective - III", null),
            SubjectEntity("AI309", "Minor Project", null),
        ),
        "CSAI_7" to listOf(
            SubjectEntity("AI401", "AI for Healthcare", null),
            SubjectEntity("AI402", "Elective - IV", null),
            SubjectEntity("AI403", "Major Project - I", null),
        ),
        "CSAI_8" to listOf(
            SubjectEntity("AI404", "Elective - V", null),
            SubjectEntity("AI405", "Major Project - II", null),
        ),
        // ── CSB ──
        "CSB_1" to listOf(
            SubjectEntity("MA101", "Mathematics - I", null),
            SubjectEntity("PH101", "Physics", null),
            SubjectEntity("CS101", "Programming Fundamentals", null),
            SubjectEntity("BIO101", "Intro to Bioinformatics", null),
            SubjectEntity("HS101", "English Communication", null),
        ),
        "CSB_2" to listOf(
            SubjectEntity("MA102", "Mathematics - II", null),
            SubjectEntity("CS102", "Data Structures", null),
            SubjectEntity("BIO102", "Molecular Biology", null),
            SubjectEntity("CS103", "Digital Logic Design", null),
            SubjectEntity("CH101", "Biochemistry", null),
        ),
        "CSB_3" to listOf(
            SubjectEntity("MA201", "Discrete Mathematics", null),
            SubjectEntity("CS201", "Object Oriented Programming", null),
            SubjectEntity("BIO201", "Computational Biology", null),
            SubjectEntity("CS203", "Database Management Systems", null),
            SubjectEntity("BIO202", "Biostatistics", null),
        ),
        "CSB_4" to listOf(
            SubjectEntity("CS204", "Operating Systems", null),
            SubjectEntity("CS205", "Algorithms", null),
            SubjectEntity("BIO203", "Genomics & Proteomics", null),
            SubjectEntity("BIO204", "Systems Biology", null),
            SubjectEntity("CS206", "Machine Learning", null),
        ),
        "CSB_5" to listOf(
            SubjectEntity("CS301", "Computer Networks", null),
            SubjectEntity("BIO301", "Drug Discovery", null),
            SubjectEntity("BIO302", "Structural Bioinformatics", null),
            SubjectEntity("BIO303", "Elective - I", null),
            SubjectEntity("BIO304", "Elective - II", null),
        ),
        "CSB_6" to listOf(
            SubjectEntity("BIO305", "Medical Image Analysis", null),
            SubjectEntity("BIO306", "Bio-AI", null),
            SubjectEntity("BIO307", "Elective - III", null),
            SubjectEntity("BIO308", "Minor Project", null),
        ),
        "CSB_7" to listOf(
            SubjectEntity("BIO401", "Elective - IV", null),
            SubjectEntity("BIO402", "Elective - V", null),
            SubjectEntity("BIO403", "Major Project - I", null),
        ),
        "CSB_8" to listOf(
            SubjectEntity("BIO404", "Major Project - II", null),
        ),
    )
}
