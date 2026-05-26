package com.example.poststudy.data.repository

import com.example.poststudy.data.network.NetworkManager
import com.example.poststudy.data.network.SessionData
import com.example.poststudy.domain.model.*
import com.example.poststudy.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkRepositoryImpl : NetworkRepository {
    override fun getLocalIpAddress(): Flow<String> = flow {
        emit(NetworkManager.getLocalIpAddress())
    }

    override fun startServer(
        port: Int,
        getSession: () -> SessionData,
        onRecordReceived: (ExamRecord) -> Unit
    ) {
        NetworkManager.startServer(port, getSession, onRecordReceived)
    }

    override fun stopServer() {
        NetworkManager.stopServer()
    }

    override fun fetchSession(ip: String, port: Int): Flow<SessionData?> = flow {
        emit(NetworkManager.fetchSession(ip, port))
    }

    override fun fetchGroups(ip: String, subjectId: Int, port: Int): Flow<List<Group>> = flow {
        emit(NetworkManager.fetchGroups(ip, subjectId, port))
    }

    override fun fetchStudents(ip: String, groupId: Int, port: Int): Flow<List<Student>> = flow {
        emit(NetworkManager.fetchStudents(ip, groupId, port))
    }

    override fun createStudentRemote(ip: String, name: String, groupId: Int, port: Int): Flow<Int?> = flow {
        emit(NetworkManager.createStudentRemote(ip, name, groupId, port))
    }

    override fun submitResult(ip: String, port: Int, record: ExamRecord): Flow<Boolean> = flow {
        emit(NetworkManager.submitResult(ip, port, record))
    }
}
