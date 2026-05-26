package com.example.poststudy.domain.repository

import com.example.poststudy.domain.model.*
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun init()
    fun isUserRegistered(): Flow<Boolean>
    fun registerUser(username: String, password: String)
    fun validateUser(username: String, password: String): Flow<Boolean>
    fun validateUserPassword(password: String): Flow<Boolean>
    fun clearAllUsers()
    
    // Subjects
    fun getAllSubjects(): Flow<List<Subject>>
    fun addSubject(name: String): Flow<Int>
    fun updateSubject(subject: Subject)
    fun deleteSubject(id: Int)

    fun saveSettings(
        presentationPath: String, 
        testPath: String, 
        slideTimerMin: Int, 
        testTimerMin: Int, 
        mode: LessonMode, 
        sessionTitle: String? = null, 
        qCount: Int = 0,
        subjectId: Int = 1
    )
    fun getSettings(subjectId: Int = 1): Flow<Settings>
    
    fun getAllLessons(subjectId: Int): Flow<List<Lesson>>
    fun addLesson(lesson: Lesson)
    fun updateLesson(lesson: Lesson)
    fun deleteLesson(lessonId: Int)
    
    fun getAllExams(subjectId: Int): Flow<List<Exam>>
    fun addExam(exam: Exam)
    fun updateExam(exam: Exam)
    fun deleteExam(examId: Int)
    
    fun saveExamRecord(record: ExamRecord)
    fun getAllExamRecords(subjectId: Int? = null): Flow<List<ExamRecord>>
    fun deleteExamRecord(id: Int)
    fun clearAllExamRecords(subjectId: Int? = null)
    
    fun getAllGroups(subjectId: Int): Flow<List<Group>>
    fun addGroup(name: String, subjectId: Int): Flow<Int>
    fun updateGroup(group: Group)
    fun deleteGroup(id: Int)
    
    fun getStudentsByGroup(groupId: Int): Flow<List<Student>>
    fun addStudent(name: String, groupId: Int): Flow<Int>
    fun deleteStudent(id: Int)
    
    fun getStudentRecords(studentId: Int): Flow<List<ExamRecord>>
    fun getGroupRecords(groupId: Int, subjectId: Int? = null): Flow<List<ExamRecord>>
    fun getAllGroupsWithStats(subjectId: Int): Flow<List<Pair<Group, Int>>>
}
