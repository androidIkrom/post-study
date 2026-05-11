package com.example.poststudy.data.local

import com.example.poststudy.domain.model.Lesson
import com.example.poststudy.domain.model.Settings
import com.example.poststudy.domain.model.ExamRecord
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 64)

    override val primaryKey = PrimaryKey(id)
}

object SettingsTable : Table("settings") {
    val id = integer("id").autoIncrement()
    val presentationPath = text("presentation_path")
    val testPath = text("test_path")
    val slideTimerSeconds = integer("slide_timer_seconds")
    val testTimerSeconds = integer("test_timer_seconds")

    override val primaryKey = PrimaryKey(id)
}

object LessonsTable : Table("lessons") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val presentationPath = text("presentation_path")
    val testPath = text("test_path")
    val slideTimerSeconds = integer("slide_timer_seconds")
    val testTimerSeconds = integer("test_timer_seconds")

    override val primaryKey = PrimaryKey(id)
}

object ExamRecordsTable : Table("exam_records") {
    val id = integer("id").autoIncrement()
    val studentName = varchar("student_name", 255)
    val lessonTitle = varchar("lesson_title", 255)
    val totalQuestions = integer("total_questions")
    val correctAnswers = integer("correct_answers")
    val wrongAnswers = integer("wrong_answers")
    val wrongDetails = text("wrong_details")
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}

object DatabaseHelper {
    fun init() {
        Database.connect("jdbc:sqlite:./poststudy.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Users, SettingsTable, LessonsTable, ExamRecordsTable)
            
            if (SettingsTable.selectAll().count() == 0L) {
                SettingsTable.insert {
                    it[SettingsTable.presentationPath] = ""
                    it[SettingsTable.testPath] = ""
                    it[SettingsTable.slideTimerSeconds] = 30
                    it[SettingsTable.testTimerSeconds] = 300
                }
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun isUserRegistered(): Boolean = transaction {
        Users.selectAll().count() > 0
    }

    fun registerUser(username: String, password: String) {
        transaction {
            Users.insert {
                it[Users.username] = username
                it[Users.password] = hashPassword(password)
            }
        }
    }

    fun validateUser(username: String, password: String): Boolean = transaction {
        Users.selectAll().where { (Users.username eq username) and (Users.password eq hashPassword(password)) }
            .count() > 0
    }

    fun saveSettings(presentationPath: String, testPath: String, slideTimer: Int, testTimer: Int) {
        transaction {
            SettingsTable.update({ SettingsTable.id eq 1 }) {
                it[SettingsTable.presentationPath] = presentationPath
                it[SettingsTable.testPath] = testPath
                it[SettingsTable.slideTimerSeconds] = slideTimer
                it[SettingsTable.testTimerSeconds] = testTimer
            }
        }
    }

    fun getSettings(): Settings = transaction {
        SettingsTable.selectAll().map { row ->
            Settings(
                id = row[SettingsTable.id],
                presentationPath = row[SettingsTable.presentationPath],
                testPath = row[SettingsTable.testPath],
                slideTimerSeconds = row[SettingsTable.slideTimerSeconds],
                testTimerSeconds = row[SettingsTable.testTimerSeconds]
            )
        }.firstOrNull() ?: Settings(1, "", "", 30, 300)
    }

    fun getAllLessons(): List<Lesson> = transaction {
        LessonsTable.selectAll().map { row ->
            Lesson(
                id = row[LessonsTable.id],
                title = row[LessonsTable.title],
                presentationPath = row[LessonsTable.presentationPath],
                testPath = row[LessonsTable.testPath],
                slideTimerSeconds = row[LessonsTable.slideTimerSeconds],
                testTimerSeconds = row[LessonsTable.testTimerSeconds]
            )
        }
    }

    fun addLesson(lesson: Lesson) {
        transaction {
            LessonsTable.insert {
                it[title] = lesson.title
                it[presentationPath] = lesson.presentationPath
                it[testPath] = lesson.testPath
                it[slideTimerSeconds] = lesson.slideTimerSeconds
                it[testTimerSeconds] = lesson.testTimerSeconds
            }
        }
    }

    fun updateLesson(lesson: Lesson) {
        transaction {
            LessonsTable.update({ LessonsTable.id eq lesson.id }) {
                it[title] = lesson.title
                it[presentationPath] = lesson.presentationPath
                it[testPath] = lesson.testPath
                it[slideTimerSeconds] = lesson.slideTimerSeconds
                it[testTimerSeconds] = lesson.testTimerSeconds
            }
        }
    }

    fun deleteLesson(lessonId: Int) {
        transaction {
            LessonsTable.deleteWhere { id eq lessonId }
        }
    }

    fun saveExamRecord(record: ExamRecord) {
        transaction {
            ExamRecordsTable.insert {
                it[studentName] = record.studentName
                it[lessonTitle] = record.lessonTitle
                it[totalQuestions] = record.totalQuestions
                it[correctAnswers] = record.correctAnswers
                it[wrongAnswers] = record.wrongAnswers
                it[wrongDetails] = record.wrongDetails
                it[timestamp] = record.timestamp
            }
        }
    }

    fun getAllExamRecords(): List<ExamRecord> = transaction {
        ExamRecordsTable.selectAll().orderBy(ExamRecordsTable.timestamp, SortOrder.DESC).map { row ->
            ExamRecord(
                id = row[ExamRecordsTable.id],
                studentName = row[ExamRecordsTable.studentName],
                lessonTitle = row[ExamRecordsTable.lessonTitle],
                totalQuestions = row[ExamRecordsTable.totalQuestions],
                correctAnswers = row[ExamRecordsTable.correctAnswers],
                wrongAnswers = row[ExamRecordsTable.wrongAnswers],
                wrongDetails = row[ExamRecordsTable.wrongDetails],
                timestamp = row[ExamRecordsTable.timestamp]
            )
        }
    }

    fun deleteExamRecord(id: Int) {
        transaction {
            ExamRecordsTable.deleteWhere { ExamRecordsTable.id eq id }
        }
    }

    fun clearAllExamRecords() {
        transaction {
            ExamRecordsTable.deleteAll()
        }
    }
}
