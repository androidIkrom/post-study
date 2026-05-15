package com.example.poststudy.data.local

import com.example.poststudy.domain.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest

// We rename the tables or add a suffix if we want to "reset" due to schema mismatch easily in SQLite 
// Or we can just drop and recreate for development.
object Users : Table("users_v2") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 64)

    override val primaryKey = PrimaryKey(id)
}

object SettingsTable : Table("settings_v4") {
    val id = integer("id").autoIncrement()
    val presentationPath = text("presentation_path")
    val testPath = text("test_path")
    val slideTimerMinutes = integer("slide_timer_minutes")
    val testTimerMinutes = integer("test_timer_minutes")
    val mode = enumerationByName("mode", 20, LessonMode::class).default(LessonMode.ReAppropriation)
    val activeSessionTitle = varchar("active_session_title", 255).default("")
    val questionsPerStudent = integer("questions_per_student").default(0)

    override val primaryKey = PrimaryKey(id)
}

object LessonsTable : Table("lessons_v2") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val presentationPath = text("presentation_path")
    val testPath = text("test_path")
    val slideTimerMinutes = integer("slide_timer_minutes") // Changed to minutes
    val testTimerMinutes = integer("test_timer_minutes")   // Changed to minutes
    val mode = enumerationByName("mode", 20, LessonMode::class).default(LessonMode.ReAppropriation)

    override val primaryKey = PrimaryKey(id)
}

object ExamsTable : Table("exams_v1") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val testPath = text("test_path")
    val testTimerMinutes = integer("test_timer_minutes")
    val questionsPerStudent = integer("questions_per_student")

    override val primaryKey = PrimaryKey(id)
}

object ExamRecordsTable : Table("exam_records_v3") {
    val id = integer("id").autoIncrement()
    val studentName = varchar("student_name", 255)
    val lessonTitle = varchar("lesson_title", 255)
    val totalQuestions = integer("total_questions")
    val correctAnswers = integer("correct_answers")
    val wrongAnswers = integer("wrong_answers")
    val wrongDetails = text("wrong_details")
    val timeSpentSeconds = integer("time_spent_seconds").default(0)
    val timestamp = long("timestamp") // Stores full date/time

    override val primaryKey = PrimaryKey(id)
}

object DatabaseHelper {
    fun init() {
        val dbPath = System.getProperty("user.home") + "/.poststudy/poststudy_v3.db"
        val dbFile = java.io.File(dbPath)
        if (!dbFile.parentFile.exists()) {
            dbFile.parentFile.mkdirs()
        }
        
        Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Users, SettingsTable, LessonsTable, ExamsTable, ExamRecordsTable)
            
            if (SettingsTable.selectAll().count() == 0L) {
                SettingsTable.insert {
                    it[SettingsTable.presentationPath] = ""
                    it[SettingsTable.testPath] = ""
                    it[SettingsTable.slideTimerMinutes] = 5
                    it[SettingsTable.testTimerMinutes] = 30
                    it[SettingsTable.mode] = LessonMode.ReAppropriation
                    it[SettingsTable.activeSessionTitle] = ""
                    it[SettingsTable.questionsPerStudent] = 0
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

    fun clearAllUsers() = transaction {
        Users.deleteAll()
    }

    fun saveSettings(presentationPath: String, testPath: String, slideTimerMin: Int, testTimerMin: Int, mode: LessonMode, sessionTitle: String? = null, qCount: Int = 0) {
        transaction {
            SettingsTable.update({ SettingsTable.id eq 1 }) {
                it[SettingsTable.presentationPath] = presentationPath
                it[SettingsTable.testPath] = testPath
                it[SettingsTable.slideTimerMinutes] = slideTimerMin
                it[SettingsTable.testTimerMinutes] = testTimerMin
                it[SettingsTable.mode] = mode
                if (sessionTitle != null) {
                    it[SettingsTable.activeSessionTitle] = sessionTitle
                }
                it[SettingsTable.questionsPerStudent] = qCount
            }
        }
    }

    fun getSettings(): Settings = transaction {
        SettingsTable.selectAll().map { row ->
            Settings(
                id = row[SettingsTable.id],
                presentationPath = row[SettingsTable.presentationPath],
                testPath = row[SettingsTable.testPath],
                slideTimerSeconds = row[SettingsTable.slideTimerMinutes] * 60,
                testTimerSeconds = row[SettingsTable.testTimerMinutes] * 60,
                mode = row[SettingsTable.mode],
                activeSessionTitle = row[SettingsTable.activeSessionTitle],
                questionsPerStudent = row[SettingsTable.questionsPerStudent]
            )
        }.firstOrNull() ?: Settings(1, "", "", 300, 1800, LessonMode.ReAppropriation, "", 0)
    }

    fun getAllLessons(): List<Lesson> = transaction {
        LessonsTable.selectAll().map { row ->
            Lesson(
                id = row[LessonsTable.id],
                title = row[LessonsTable.title],
                presentationPath = row[LessonsTable.presentationPath],
                testPath = row[LessonsTable.testPath],
                slideTimerSeconds = row[LessonsTable.slideTimerMinutes] * 60,
                testTimerSeconds = row[LessonsTable.testTimerMinutes] * 60,
                mode = row[LessonsTable.mode]
            )
        }
    }

    fun addLesson(lesson: Lesson) {
        transaction {
            LessonsTable.insert {
                it[title] = lesson.title
                it[presentationPath] = lesson.presentationPath
                it[testPath] = lesson.testPath
                it[slideTimerMinutes] = lesson.slideTimerSeconds / 60
                it[testTimerMinutes] = lesson.testTimerSeconds / 60
                it[mode] = lesson.mode
            }
        }
    }

    fun updateLesson(lesson: Lesson) {
        transaction {
            LessonsTable.update({ LessonsTable.id eq lesson.id }) {
                it[title] = lesson.title
                it[presentationPath] = lesson.presentationPath
                it[testPath] = lesson.testPath
                it[slideTimerMinutes] = lesson.slideTimerSeconds / 60
                it[testTimerMinutes] = lesson.testTimerSeconds / 60
                it[mode] = lesson.mode
            }
        }
    }

    fun deleteLesson(lessonId: Int) {
        transaction {
            LessonsTable.deleteWhere { id eq lessonId }
        }
    }

    fun getAllExams(): List<Exam> = transaction {
        ExamsTable.selectAll().map { row ->
            Exam(
                id = row[ExamsTable.id],
                title = row[ExamsTable.title],
                testPath = row[ExamsTable.testPath],
                testTimerSeconds = row[ExamsTable.testTimerMinutes] * 60,
                questionsPerStudent = row[ExamsTable.questionsPerStudent]
            )
        }
    }

    fun addExam(exam: Exam) {
        transaction {
            ExamsTable.insert {
                it[title] = exam.title
                it[testPath] = exam.testPath
                it[testTimerMinutes] = exam.testTimerSeconds / 60
                it[questionsPerStudent] = exam.questionsPerStudent
            }
        }
    }

    fun updateExam(exam: Exam) {
        transaction {
            ExamsTable.update({ ExamsTable.id eq exam.id }) {
                it[title] = exam.title
                it[testPath] = exam.testPath
                it[testTimerMinutes] = exam.testTimerSeconds / 60
                it[questionsPerStudent] = exam.questionsPerStudent
            }
        }
    }

    fun deleteExam(examId: Int) {
        transaction {
            ExamsTable.deleteWhere { id eq examId }
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
                it[timeSpentSeconds] = record.timeSpentSeconds
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
                timeSpentSeconds = row[ExamRecordsTable.timeSpentSeconds],
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
