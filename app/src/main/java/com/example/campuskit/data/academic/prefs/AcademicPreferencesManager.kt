package com.example.campuskit.data.academic.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.campuskit.domain.academic.model.Program
import com.example.campuskit.domain.academic.model.Semester
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.academicDataStore: DataStore<Preferences> by preferencesDataStore(name = "academic_prefs")

data class AcademicPreferences(
    val program: Program,
    val semester: Semester
)

@Singleton
class AcademicPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PROGRAM_KEY = stringPreferencesKey("academic_program")
    private val SEMESTER_KEY = intPreferencesKey("academic_semester")

    val preferencesFlow: Flow<AcademicPreferences> = context.academicDataStore.data.map { preferences ->
        val programString = preferences[PROGRAM_KEY] ?: Program.UNKNOWN.name
        val program = try {
            Program.valueOf(programString)
        } catch (e: IllegalArgumentException) {
            Program.UNKNOWN
        }
        val semester = preferences[SEMESTER_KEY] ?: 1

        AcademicPreferences(program, semester)
    }

    suspend fun updateProgram(program: Program) {
        context.academicDataStore.edit { preferences ->
            preferences[PROGRAM_KEY] = program.name
        }
    }

    suspend fun updateSemester(semester: Semester) {
        context.academicDataStore.edit { preferences ->
            preferences[SEMESTER_KEY] = semester
        }
    }
}
