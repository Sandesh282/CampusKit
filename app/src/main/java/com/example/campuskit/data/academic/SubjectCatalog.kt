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

    fun getAllCatalogSubjectNames(): Set<String> {
        return catalog.values.flatten().map { it.name.lowercase() }.toSet()
    }

    // ── Catalog Data ──
    private val catalog = mapOf(
        // ── IT (Information Technology) ──
        "IT_1" to listOf(
            SubjectEntity("IT101", "Computational Thinking through Programming", null),
            SubjectEntity("IT102", "Database Management System", null),
            SubjectEntity("IT103", "System Programming & Scripting", null),
            SubjectEntity("IT104", "Web Design & Application Development I", null),
            SubjectEntity("HS101", "Professional Communication I", null),
            SubjectEntity("SP101", "Sports I", null),
        ),
        "IT_2" to listOf(
            SubjectEntity("IT201", "Data Structures", null),
            SubjectEntity("IT202", "Object Oriented Programming & System Design", null),
            SubjectEntity("IT203", "Computer Organization & Architecture", null),
            SubjectEntity("IT204", "Web Design & Application Development II", null),
            SubjectEntity("HS102", "Professional Communication II", null),
            SubjectEntity("SP102", "Sports II", null),
        ),
        "IT_3" to listOf(
            SubjectEntity("IT301", "Software Engineering", null),
            SubjectEntity("IT302", "Theory of Automata", null),
            SubjectEntity("IT303", "Data Communications", null),
            SubjectEntity("MA301", "Probability and Statistics for CS", null),
            SubjectEntity("IT304", "Design Analysis and Algorithm", null),
            SubjectEntity("CC301", "Competitive Coding I", null),
            SubjectEntity("SP103", "Sports III", null),
        ),
        "IT_4" to listOf(
            SubjectEntity("IT401", "Compiler Design", null),
            SubjectEntity("MA401", "Mathematics for CS I (Discrete Mathematics)", null),
            SubjectEntity("IT402", "Operating System", null),
            SubjectEntity("IT403", "Computer Networks", null),
            SubjectEntity("IT404", "Advanced Programming Language", null),
            SubjectEntity("CC401", "Competitive Coding II", null),
        ),
        "IT_5" to listOf(
            SubjectEntity("IT501", "Foundations of Cryptography", null),
            SubjectEntity("IT502", "Cloud Computing", null),
            SubjectEntity("IT503", "Computer Graphics", null),
            SubjectEntity("IT504", "Soft Computing", null),
            SubjectEntity("ELE501", "Elective I", null),
            SubjectEntity("CC501", "Competitive Coding III", null),
        ),
        "IT_6" to listOf(
            SubjectEntity("IT601", "Techno Entrepreneurship", null),
            SubjectEntity("ELE601", "Elective II", null),
            SubjectEntity("PRJ601", "Mini Project II", null),
            SubjectEntity("TRN601", "Industrial Training / Internship", null),
        ),
        "IT_7" to listOf(
            SubjectEntity("ELE701", "Elective III", null),
            SubjectEntity("ELE702", "Elective IV", null),
            SubjectEntity("HS701", "Professional Ethics / Advanced Competitive Coding", null),
            SubjectEntity("PRJ701", "Mini Project III", null),
        ),
        "IT_8" to listOf(
            SubjectEntity("PRJ801", "Major Project", null),
        ),

        // ── CS (Computer Science) ──
        "CSE_1" to listOf(
            SubjectEntity("CS101", "Computational Thinking through Programming", null),
            SubjectEntity("CS102", "Database Management System", null),
            SubjectEntity("CS103", "System Programming & Scripting", null),
            SubjectEntity("CS104", "Web Design & Application Development I", null),
            SubjectEntity("HS101", "Professional Communication I", null),
            SubjectEntity("SP101", "Sports I", null),
        ),
        "CSE_2" to listOf(
            SubjectEntity("CS201", "Data Structures", null),
            SubjectEntity("CS202", "Object Oriented Programming & System Design", null),
            SubjectEntity("CS203", "Computer Organization & Architecture", null),
            SubjectEntity("CS204", "Web Design & Application Development II", null),
            SubjectEntity("HS102", "Professional Communication II", null),
            SubjectEntity("SP102", "Sports II", null),
        ),
        "CSE_3" to listOf(
            SubjectEntity("CS301", "Software Engineering", null),
            SubjectEntity("CS302", "Theory of Automata", null),
            SubjectEntity("CS303", "Data Communications", null),
            SubjectEntity("MA301", "Probability and Statistics for CS", null),
            SubjectEntity("CS304", "Design Analysis and Algorithm", null),
            SubjectEntity("CC301", "Competitive Coding I", null),
            SubjectEntity("SP103", "Sports III", null),
        ),
        "CSE_4" to listOf(
            SubjectEntity("CS401", "Compiler Design", null),
            SubjectEntity("MA401", "Mathematics for CS I (Discrete Mathematics)", null),
            SubjectEntity("CS402", "Operating System", null),
            SubjectEntity("CS403", "Computer Networks", null),
            SubjectEntity("CS404", "Advanced Programming Language", null),
            SubjectEntity("CC401", "Competitive Coding II", null),
        ),
        "CSE_5" to listOf(
            SubjectEntity("CS501", "Foundations of Cryptography", null),
            SubjectEntity("CS502", "Machine Learning", null),
            SubjectEntity("CS503", "Computer Graphics", null),
            SubjectEntity("ELE501", "Elective I", null),
            SubjectEntity("MA501", "Mathematics for CS II (Linear Algebra + Calculus)", null),
            SubjectEntity("CC501", "Competitive Coding III", null),
        ),
        "CSE_6" to listOf(
            SubjectEntity("CS601", "Techno Entrepreneurship", null),
            SubjectEntity("ELE601", "Elective II", null),
            SubjectEntity("PRJ601", "Mini Project II", null),
            SubjectEntity("TRN601", "Industrial Training / Internship", null),
        ),
        "CSE_7" to listOf(
            SubjectEntity("ELE701", "Elective III", null),
            SubjectEntity("ELE702", "Elective IV", null),
            SubjectEntity("HS701", "Professional Ethics / Advanced Competitive Coding", null),
            SubjectEntity("PRJ701", "Mini Project III", null),
        ),
        "CSE_8" to listOf(
            SubjectEntity("PRJ801", "Major Project", null),
        ),

        // ── CSAI (Computer Science + Artificial Intelligence) ──
        "CSAI_1" to listOf(
            SubjectEntity("AI101", "Computational Thinking through Programming", null),
            SubjectEntity("AI102", "Database Management System", null),
            SubjectEntity("AI103", "System Programming & Scripting", null),
            SubjectEntity("AI104", "Web Design & Application Development I", null),
            SubjectEntity("HS101", "Professional Communication I", null),
            SubjectEntity("SP101", "Sports I", null),
        ),
        "CSAI_2" to listOf(
            SubjectEntity("AI201", "Data Structures", null),
            SubjectEntity("AI202", "Object Oriented Programming & System Design", null),
            SubjectEntity("AI203", "Computer Organization & Architecture", null),
            SubjectEntity("AI204", "Web Design & Application Development II", null),
            SubjectEntity("HS102", "Professional Communication II", null),
            SubjectEntity("SP102", "Sports II", null),
        ),
        "CSAI_3" to listOf(
            SubjectEntity("AI301", "Software Engineering", null),
            SubjectEntity("AI302", "Theory of Automata", null),
            SubjectEntity("AI303", "Data Communications", null),
            SubjectEntity("MA301", "Probability and Statistics for CS", null),
            SubjectEntity("AI304", "Design Analysis and Algorithm", null),
            SubjectEntity("CC301", "Competitive Coding I", null),
            SubjectEntity("SP103", "Sports III", null),
        ),
        "CSAI_4" to listOf(
            SubjectEntity("AI401", "Compiler Design", null),
            SubjectEntity("MA401", "Mathematics for CS I (Discrete Mathematics)", null),
            SubjectEntity("AI402", "Operating System", null),
            SubjectEntity("AI403", "Computer Networks", null),
            SubjectEntity("AI404", "Advanced Programming Language", null),
            SubjectEntity("CC401", "Competitive Coding II", null),
        ),
        "CSAI_5" to listOf(
            SubjectEntity("AI501", "Machine Learning", null),
            SubjectEntity("AI502", "Artificial Intelligence", null),
            SubjectEntity("ELE501", "Elective I", null),
            SubjectEntity("MA501", "Mathematics for CS II (Linear Algebra + Calculus)", null),
            SubjectEntity("AI503", "Computer Graphics", null),
            SubjectEntity("CC501", "Competitive Coding III", null),
        ),
        "CSAI_6" to listOf(
            SubjectEntity("AI601", "Deep Learning", null),
            SubjectEntity("AI602", "Techno Entrepreneurship", null),
            SubjectEntity("PRJ601", "Mini Project II", null),
            SubjectEntity("TRN601", "Industrial Training / Internship", null),
        ),
        "CSAI_7" to listOf(
            SubjectEntity("ELE701", "Elective II", null),
            SubjectEntity("ELE702", "Elective III", null),
            SubjectEntity("HS701", "Professional Ethics / Advanced Competitive Coding", null),
            SubjectEntity("PRJ701", "Mini Project III", null),
        ),
        "CSAI_8" to listOf(
            SubjectEntity("PRJ801", "Major Project", null),
        ),

        // ── CSB (Computer Science + Business) ──
        "CSB_1" to listOf(
            SubjectEntity("CB101", "Computational Thinking through Programming", null),
            SubjectEntity("CB102", "Database Management System", null),
            SubjectEntity("CB103", "System Programming & Scripting", null),
            SubjectEntity("CB104", "Web Design & Application Development I", null),
            SubjectEntity("HS101", "Professional Communication I", null),
            SubjectEntity("SP101", "Sports I", null),
        ),
        "CSB_2" to listOf(
            SubjectEntity("CB201", "Data Structures", null),
            SubjectEntity("CB202", "Object Oriented Programming & System Design", null),
            SubjectEntity("CB203", "Computer Organization & Architecture", null),
            SubjectEntity("CB204", "Web Design & Application Development II", null),
            SubjectEntity("HS102", "Professional Communication II", null),
            SubjectEntity("SP102", "Sports II", null),
        ),
        "CSB_3" to listOf(
            SubjectEntity("CB301", "Software Engineering", null),
            SubjectEntity("CB302", "Theory of Automata", null),
            SubjectEntity("CB303", "Data Communications", null),
            SubjectEntity("MA301", "Probability and Statistics for CS", null),
            SubjectEntity("CB304", "Design Analysis and Algorithm", null),
            SubjectEntity("CC301", "Competitive Coding I", null),
            SubjectEntity("SP103", "Sports III", null),
        ),
        "CSB_4" to listOf(
            SubjectEntity("CB401", "Compiler Design", null),
            SubjectEntity("MA401", "Mathematics for CS I (Discrete Mathematics)", null),
            SubjectEntity("CB402", "Operating System", null),
            SubjectEntity("CB403", "Computer Networks", null),
            SubjectEntity("CB404", "Advanced Programming Language", null),
            SubjectEntity("CC401", "Competitive Coding II", null),
        ),
        "CSB_5" to listOf(
            SubjectEntity("CB501", "Business Economics", null),
            SubjectEntity("CB502", "Machine Learning", null),
            SubjectEntity("ELE501", "Elective I", null),
            SubjectEntity("CB503", "Business Decision Making", null),
            SubjectEntity("CB504", "People Management", null),
            SubjectEntity("CC501", "Competitive Coding III", null),
        ),
        "CSB_6" to listOf(
            SubjectEntity("CB601", "Techno Entrepreneurship", null),
            SubjectEntity("ELE601", "Elective II", null),
            SubjectEntity("PRJ601", "Mini Project II", null),
            SubjectEntity("TRN601", "Industrial Training / Internship", null),
        ),
        "CSB_7" to listOf(
            SubjectEntity("ELE701", "Elective III", null),
            SubjectEntity("ELE702", "Elective IV", null),
            SubjectEntity("HS701", "Professional Ethics / Advanced Competitive Coding", null),
            SubjectEntity("PRJ701", "Mini Project III", null),
        ),
        "CSB_8" to listOf(
            SubjectEntity("PRJ801", "Major Project", null),
        )
    )
}
