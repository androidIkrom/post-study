package com.example.poststudy.domain.repository

import com.example.poststudy.data.network.SessionData
import com.example.poststudy.domain.model.*
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getLocalIpAddress(): Flow<String>
    fun startServer(
        port: Int = 8080, 
        getSession: () -> SessionData, 
        onRecordReceived: (ExamRecord) -> Unit
    )
    fun stopServer()
    
    fun fetchSession(ip: String, port: Int = 8080): Flow<SessionData?>
    fun fetchGroups(ip: String, subjectId: Int, port: Int = 8080): Flow<List<Group>>
    fun fetchStudents(ip: String, groupId: Int, port: Int = 8080): Flow<List<Student>>
    fun createStudentRemote(ip: String, name: String, groupId: Int, port: Int = 8080): Flow<Int?>
    fun submitResult(ip: String, port: Int = 8080, record: ExamRecord): Flow<Boolean>
}
