package com.example.poststudy.data.network

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.poststudy.di.AppContainer
import com.example.poststudy.domain.model.*
import com.google.gson.Gson
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

data class SessionData(
    val title: String,
    val questions: List<Question>,
    val slideTimerSeconds: Int,
    val testTimerSeconds: Int,
    val mode: LessonMode,
    val subjectId: Int,
    val encodedSlides: List<String> = emptyList()
)

object NetworkManager {
    private var server: HttpServer? = null
    private val gson = Gson()

    fun getLocalIpAddress(): String {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            val candidateAddresses = mutableListOf<String>()

            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                // Skip inactive, loopback or virtual interfaces (like Docker/VPN)
                if (iface.isLoopback || !iface.isUp || iface.isVirtual) continue

                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (addr is java.net.Inet4Address) {
                        val ip = addr.hostAddress
                        // 1. Prioritize private LAN ranges (192.168.x, 10.x, 172.x)
                        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) {
                            return ip
                        }
                        // 2. Keep other physical interface IPs (like 197.x.x.x) as candidates
                        candidateAddresses.add(ip)
                    }
                }
            }
            // Return the first candidate if no standard private IP was found
            candidateAddresses.firstOrNull() ?: "127.0.0.1"
        } catch (e: Exception) {
            "127.0.0.1"
        }
    }

    fun startServer(
        port: Int = 8080, getSession: () -> SessionData, onRecordReceived: (ExamRecord) -> Unit
    ) {
        if (server != null) return

        try {
            // Bind to 0.0.0.0 to listen on all available network interfaces (LAN, WiFi, etc.)
            server = HttpServer.create(InetSocketAddress("0.0.0.0", port), 0).apply {
                createContext("/session") { exchange ->
                    try {
                        val response = gson.toJson(getSession())
                        val bytes = response.toByteArray(StandardCharsets.UTF_8)
                        exchange.responseHeaders.add("Content-Type", "application/json")
                        exchange.sendResponseHeaders(200, bytes.size.toLong())
                        exchange.responseBody.use { it.write(bytes) }
                    } catch (e: Exception) {
                        println("Error serving session: ${e.message}")
                        e.printStackTrace()
                        exchange.sendResponseHeaders(500, -1)
                    }
                }

                createContext("/groups") { exchange ->
                    try {
                        val query = exchange.requestURI.query
                        val subjectId = query?.split("=")?.get(1)?.toIntOrNull() ?: 1
                        val groups = runBlocking { AppContainer.localRepository.getAllGroups(subjectId).first() }
                        val response = gson.toJson(groups)
                        val bytes = response.toByteArray(StandardCharsets.UTF_8)
                        exchange.responseHeaders.add("Content-Type", "application/json")
                        exchange.sendResponseHeaders(200, bytes.size.toLong())
                        exchange.responseBody.use { it.write(bytes) }
                    } catch (e: Exception) {
                        exchange.sendResponseHeaders(500, -1)
                    }
                }

                createContext("/students") { exchange ->
                    try {
                        val query = exchange.requestURI.query
                        val groupId = query?.split("&")?.find { it.startsWith("groupId=") }?.split("=")?.get(1)?.toIntOrNull() ?: -1
                        val students = runBlocking { AppContainer.localRepository.getStudentsByGroup(groupId).first() }
                        val response = gson.toJson(students)
                        val bytes = response.toByteArray(StandardCharsets.UTF_8)
                        exchange.responseHeaders.add("Content-Type", "application/json")
                        exchange.sendResponseHeaders(200, bytes.size.toLong())
                        exchange.responseBody.use { it.write(bytes) }
                    } catch (e: Exception) {
                        exchange.sendResponseHeaders(500, -1)
                    }
                }

                createContext("/create-student") { exchange ->
                    try {
                        if ("POST" == exchange.requestMethod) {
                            val body = exchange.requestBody.bufferedReader(StandardCharsets.UTF_8).readText()
                            val studentReq = gson.fromJson(body, Student::class.java)
                            val id = runBlocking { AppContainer.localRepository.addStudent(studentReq.name, studentReq.groupId).first() }
                            val response = id.toString()
                            exchange.sendResponseHeaders(200, response.length.toLong())
                            exchange.responseBody.use { it.write(response.toByteArray()) }
                        } else exchange.sendResponseHeaders(405, -1)
                    } catch (e: Exception) {
                        exchange.sendResponseHeaders(500, -1)
                    }
                }

                createContext("/submit") { exchange ->
                    try {
                        if ("POST" == exchange.requestMethod) {
                            val body = exchange.requestBody.bufferedReader(StandardCharsets.UTF_8)
                                .readText()
                            val record = gson.fromJson(body, ExamRecord::class.java)
                            onRecordReceived(record)
                            val response = "OK"
                            exchange.sendResponseHeaders(200, response.length.toLong())
                            exchange.responseBody.use { it.write(response.toByteArray()) }
                        } else {
                            exchange.sendResponseHeaders(405, -1)
                        }
                    } catch (e: Exception) {
                        println("Error receiving submission: ${e.message}")
                        e.printStackTrace()
                        exchange.sendResponseHeaders(500, -1)
                    }
                }
                executor = java.util.concurrent.Executors.newCachedThreadPool()
                start()
                println("Server started on 0.0.0.0:$port")
            }
        } catch (e: Exception) {
            println("FAILED TO START SERVER: ${e.message}")
            e.printStackTrace()
        }
    }

    fun stopServer() {
        server?.stop(0)
        server = null
    }

    fun fetchSession(ip: String, port: Int = 8080): SessionData? {
        return try {
            val url = java.net.URL("http://$ip:$port/session")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 10000 // 10s
            connection.readTimeout = 30000    // 30s for large slide payloads
            connection.requestMethod = "GET"
            if (connection.responseCode == 200) {
                val text = connection.inputStream.bufferedReader().readText()
                gson.fromJson(text, SessionData::class.java)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchGroups(ip: String, subjectId: Int, port: Int = 8080): List<Group> {
        return try {
            val url = java.net.URL("http://$ip:$port/groups?subjectId=$subjectId")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.requestMethod = "GET"
            if (connection.responseCode == 200) {
                val text = connection.inputStream.bufferedReader().readText()
                val type = object : com.google.gson.reflect.TypeToken<List<Group>>() {}.type
                gson.fromJson(text, type)
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun fetchStudents(ip: String, groupId: Int, port: Int = 8080): List<Student> {
        return try {
            val url = java.net.URL("http://$ip:$port/students?groupId=$groupId")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.requestMethod = "GET"
            if (connection.responseCode == 200) {
                val text = connection.inputStream.bufferedReader().readText()
                val type = object : com.google.gson.reflect.TypeToken<List<Student>>() {}.type
                gson.fromJson(text, type)
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun createStudentRemote(ip: String, name: String, groupId: Int, port: Int = 8080): Int? {
        return try {
            val url = java.net.URL("http://$ip:$port/create-student")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.requestMethod = "POST"
            connection.doOutput = true
            val body = gson.toJson(Student(0, name, groupId))
            connection.outputStream.use { it.write(body.toByteArray()) }
            if (connection.responseCode == 200) {
                connection.inputStream.bufferedReader().readText().toIntOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun submitResult(ip: String, port: Int = 8080, record: ExamRecord): Boolean {
        return try {
            val url = java.net.URL("http://$ip:$port/submit")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            val body = gson.toJson(record)
            connection.outputStream.use { it.write(body.toByteArray(StandardCharsets.UTF_8)) }
            connection.responseCode == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
