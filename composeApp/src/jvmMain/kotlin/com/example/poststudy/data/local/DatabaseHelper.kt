package com.example.poststudy.data.local

import com.example.poststudy.domain.model.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest

object Users : Table("users_v2") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 64)
    override val primaryKey = PrimaryKey(id)
}

object SubjectsTable : IntIdTable("subjects_v1") {
    val name = varchar("name", 255)
}

object SettingsTable : Table("settings_v5") {
    val id = integer("id").autoIncrement()
    val presentationPath = text("presentation_path")
    val testPath = text("test_path")
    val slideTimerMinutes = integer("slide_timer_minutes")
    val testTimerMinutes = integer("test_timer_minutes")
    val mode = enumerationByName("mode", 20, LessonMode::class).default(LessonMode.ReAppropriation)
    val activeSessionTitle = varchar("active_session_title", 255).default("")
    val questionsPerStudent = integer("questions_per_student").default(0)
    val subjectId = reference("subject_id", SubjectsTable).default(org.jetbrains.exposed.dao.id.EntityID(1, SubjectsTable))
    override val primaryKey = PrimaryKey(id)
}

object LessonsTable : Table("lessons_v3") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val presentationPath = text("presentation_path")
    val testPath = text("test_path")
    val slideTimerMinutes = integer("slide_timer_minutes")
    val testTimerMinutes = integer("test_timer_minutes")
    val mode = enumerationByName("mode", 20, LessonMode::class).default(LessonMode.ReAppropriation)
    val subjectId = reference("subject_id", SubjectsTable).default(org.jetbrains.exposed.dao.id.EntityID(1, SubjectsTable))
    override val primaryKey = PrimaryKey(id)
}

object ExamsTable : Table("exams_v2") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val testPath = text("test_path")
    val testTimerMinutes = integer("test_timer_minutes")
    val questionsPerStudent = integer("questions_per_student")
    val subjectId = reference("subject_id", SubjectsTable).default(org.jetbrains.exposed.dao.id.EntityID(1, SubjectsTable))
    override val primaryKey = PrimaryKey(id)
}

object ExamRecordsTable : Table("exam_records_v5") {
    val id = integer("id").autoIncrement()
    val studentName = varchar("student_name", 255)
    val lessonTitle = varchar("lesson_title", 255)
    val totalQuestions = integer("total_questions")
    val correctAnswers = integer("correct_answers")
    val wrongAnswers = integer("wrong_answers")
    val wrongDetails = text("wrong_details")
    val timeSpentSeconds = integer("time_spent_seconds").default(0)
    val timestamp = long("timestamp")
    val groupId = integer("group_id").nullable()
    val studentId = integer("student_id").nullable()
    val subjectId = reference("subject_id", SubjectsTable).nullable()
    override val primaryKey = PrimaryKey(id)
}

object GroupsTable : IntIdTable("groups_v2") {
    val name = varchar("name", 255)
    val subjectId = reference("subject_id", SubjectsTable).default(org.jetbrains.exposed.dao.id.EntityID(1, SubjectsTable))
}

object StudentsTable : IntIdTable("students_v1") {
    val name = varchar("name", 255)
    val groupId = reference("group_id", GroupsTable)
}

object DatabaseHelper {
    fun init() {
        val dbPath = System.getProperty("user.home") + "/.breakpoint/breakpoint_v2.db"
        val dbFile = java.io.File(dbPath)
        if (!dbFile.parentFile.exists()) {
            dbFile.parentFile.mkdirs()
        }
        
        Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")
        
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Users, SubjectsTable, SettingsTable, LessonsTable, 
                ExamsTable, ExamRecordsTable, GroupsTable, StudentsTable
            )
            initializeDefaults()
        }
    }

    private fun initializeDefaults() = transaction {
        if (SubjectsTable.selectAll().count() == 0L) {
            SubjectsTable.insertAndGetId {
                it[name] = "Asosiy fan"
            }
        }

        val mainSubjectId = SubjectsTable.selectAll().first()[SubjectsTable.id]

        if (GroupsTable.selectAll().count() == 0L) {
            GroupsTable.insert {
                it[name] = "Guruh 1"
                it[subjectId] = mainSubjectId
            }
        }

        if (SettingsTable.selectAll().where { SettingsTable.subjectId eq mainSubjectId }.count() == 0L) {
            SettingsTable.insert {
                it[presentationPath] = ""
                it[testPath] = ""
                it[slideTimerMinutes] = 5
                it[testTimerMinutes] = 30
                it[mode] = LessonMode.ReAppropriation
                it[activeSessionTitle] = ""
                it[questionsPerStudent] = 0
                it[subjectId] = mainSubjectId
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

    fun validateUserPassword(password: String): Boolean = transaction {
        Users.selectAll().where { Users.password eq hashPassword(password) }.count() > 0
    }

    fun clearAllUsers() = transaction {
        Users.deleteAll()
    }

    // --- Subjects API ---
    fun getAllSubjects(): List<Subject> = transaction {
        SubjectsTable.selectAll().map { Subject(it[SubjectsTable.id].value, it[SubjectsTable.name]) }
    }

    fun addSubject(name: String): Int = transaction {
        val id = SubjectsTable.insertAndGetId { it[SubjectsTable.name] = name }.value
        // Initialize settings for new subject
        SettingsTable.insert {
            it[presentationPath] = ""
            it[testPath] = ""
            it[slideTimerMinutes] = 5
            it[testTimerMinutes] = 30
            it[mode] = LessonMode.ReAppropriation
            it[activeSessionTitle] = ""
            it[questionsPerStudent] = 0
            it[subjectId] = org.jetbrains.exposed.dao.id.EntityID(id, SubjectsTable)
        }
        id
    }

    fun updateSubject(subject: Subject) = transaction {
        SubjectsTable.update({ SubjectsTable.id eq subject.id }) { it[name] = subject.name }
    }

    fun deleteSubject(id: Int) = transaction {
        // Cascading deletes would be nice but manual is safer here for SQLite via Exposed
        LessonsTable.deleteWhere { subjectId eq id }
        ExamsTable.deleteWhere { subjectId eq id }
        ExamRecordsTable.deleteWhere { subjectId eq id }
        
        val groupIds = GroupsTable.selectAll().where { GroupsTable.subjectId eq id }.map { it[GroupsTable.id] }
        StudentsTable.deleteWhere { groupId inList groupIds }
        GroupsTable.deleteWhere { subjectId eq id }
        SettingsTable.deleteWhere { subjectId eq id }
        
        SubjectsTable.deleteWhere { SubjectsTable.id eq id }
    }

    fun saveSettings(presentationPath: String, testPath: String, slideTimerMin: Int, testTimerMin: Int, mode: LessonMode, sessionTitle: String? = null, qCount: Int = 0, subjectId: Int = 1) {
        transaction {
            SettingsTable.update({ SettingsTable.subjectId eq subjectId }) {
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

    fun getSettings(subjectId: Int = 1): Settings = transaction {
        SettingsTable.selectAll().where { SettingsTable.subjectId eq subjectId }.map { row ->
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

    fun getAllLessons(subjectId: Int): List<Lesson> = transaction {
        LessonsTable.selectAll().where { LessonsTable.subjectId eq subjectId }.map { row ->
            Lesson(
                id = row[LessonsTable.id],
                title = row[LessonsTable.title],
                presentationPath = row[LessonsTable.presentationPath],
                testPath = row[LessonsTable.testPath],
                slideTimerSeconds = row[LessonsTable.slideTimerMinutes] * 60,
                testTimerSeconds = row[LessonsTable.testTimerMinutes] * 60,
                mode = row[LessonsTable.mode],
                subjectId = row[LessonsTable.subjectId].value
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
                it[subjectId] = org.jetbrains.exposed.dao.id.EntityID(lesson.subjectId, SubjectsTable)
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

    fun getAllExams(subjectId: Int): List<Exam> = transaction {
        ExamsTable.selectAll().where { ExamsTable.subjectId eq subjectId }.map { row ->
            Exam(
                id = row[ExamsTable.id],
                title = row[ExamsTable.title],
                testPath = row[ExamsTable.testPath],
                testTimerSeconds = row[ExamsTable.testTimerMinutes] * 60,
                questionsPerStudent = row[ExamsTable.questionsPerStudent],
                subjectId = row[ExamsTable.subjectId].value
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
                it[subjectId] = org.jetbrains.exposed.dao.id.EntityID(exam.subjectId, SubjectsTable)
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
                it[groupId] = record.groupId
                it[studentId] = record.studentId
                it[subjectId] = record.subjectId?.let { sId -> org.jetbrains.exposed.dao.id.EntityID(sId, SubjectsTable) }
            }
        }
    }

    fun getAllExamRecords(subjectId: Int? = null): List<ExamRecord> = transaction {
        val query = if (subjectId != null) {
            ExamRecordsTable.selectAll().where { ExamRecordsTable.subjectId eq subjectId }
        } else {
            ExamRecordsTable.selectAll()
        }
        
        query.orderBy(ExamRecordsTable.timestamp, SortOrder.DESC).map { row ->
            ExamRecord(
                id = row[ExamRecordsTable.id],
                studentName = row[ExamRecordsTable.studentName],
                lessonTitle = row[ExamRecordsTable.lessonTitle],
                totalQuestions = row[ExamRecordsTable.totalQuestions],
                correctAnswers = row[ExamRecordsTable.correctAnswers],
                wrongAnswers = row[ExamRecordsTable.wrongAnswers],
                wrongDetails = row[ExamRecordsTable.wrongDetails],
                timeSpentSeconds = row[ExamRecordsTable.timeSpentSeconds],
                timestamp = row[ExamRecordsTable.timestamp],
                groupId = row[ExamRecordsTable.groupId],
                studentId = row[ExamRecordsTable.studentId],
                subjectId = row[ExamRecordsTable.subjectId]?.value
            )
        }
    }

    fun deleteExamRecord(id: Int) {
        transaction {
            ExamRecordsTable.deleteWhere { ExamRecordsTable.id eq id }
        }
    }

    fun clearAllExamRecords(subjectId: Int? = null) = transaction {
        if (subjectId != null) {
            ExamRecordsTable.deleteWhere { ExamRecordsTable.subjectId eq subjectId }
        } else {
            ExamRecordsTable.deleteAll()
        }
    }

    // --- Groups API ---

    fun getAllGroups(subjectId: Int): List<Group> = transaction {
        GroupsTable.selectAll().where { GroupsTable.subjectId eq subjectId }.map { Group(it[GroupsTable.id].value, it[GroupsTable.name], it[GroupsTable.subjectId].value) }
    }

    fun addGroup(name: String, subjectId: Int): Int = transaction {
        GroupsTable.insertAndGetId { 
            it[GroupsTable.name] = name 
            it[GroupsTable.subjectId] = org.jetbrains.exposed.dao.id.EntityID(subjectId, SubjectsTable)
        }.value
    }

    fun updateGroup(group: Group) = transaction {
        GroupsTable.update({ GroupsTable.id eq group.id }) { it[name] = group.name }
    }

    fun deleteGroup(id: Int) = transaction {
        StudentsTable.deleteWhere { groupId eq id }
        GroupsTable.deleteWhere { GroupsTable.id eq id }
    }

    // --- Students API ---

    fun getStudentsByGroup(groupId: Int): List<Student> = transaction {
        StudentsTable.selectAll().where { StudentsTable.groupId eq groupId }
            .map { Student(it[StudentsTable.id].value, it[StudentsTable.name], it[StudentsTable.groupId].value) }
    }

    fun addStudent(name: String, groupId: Int): Int = transaction {
        StudentsTable.insertAndGetId {
            it[StudentsTable.name] = name
            it[StudentsTable.groupId] = groupId
        }.value
    }

    fun deleteStudent(id: Int) = transaction {
        ExamRecordsTable.deleteWhere { studentId eq id }
        StudentsTable.deleteWhere { StudentsTable.id eq id }
    }

    fun getStudentRecords(studentId: Int): List<ExamRecord> = transaction {
        ExamRecordsTable.selectAll().where { ExamRecordsTable.studentId eq studentId }
            .orderBy(ExamRecordsTable.timestamp, SortOrder.DESC)
            .map { row ->
                ExamRecord(
                    id = row[ExamRecordsTable.id],
                    studentName = row[ExamRecordsTable.studentName],
                    lessonTitle = row[ExamRecordsTable.lessonTitle],
                    totalQuestions = row[ExamRecordsTable.totalQuestions],
                    correctAnswers = row[ExamRecordsTable.correctAnswers],
                    wrongAnswers = row[ExamRecordsTable.wrongAnswers],
                    wrongDetails = row[ExamRecordsTable.wrongDetails],
                    timeSpentSeconds = row[ExamRecordsTable.timeSpentSeconds],
                    timestamp = row[ExamRecordsTable.timestamp],
                    groupId = row[ExamRecordsTable.groupId],
                    studentId = row[ExamRecordsTable.studentId],
                    subjectId = row[ExamRecordsTable.subjectId]?.value
                )
            }
    }

    fun getGroupRecords(groupId: Int, subjectId: Int? = null): List<ExamRecord> = transaction {
        val query = if (subjectId != null) {
            ExamRecordsTable.selectAll().where { (ExamRecordsTable.groupId eq groupId) and (ExamRecordsTable.subjectId eq subjectId) }
        } else {
            ExamRecordsTable.selectAll().where { ExamRecordsTable.groupId eq groupId }
        }
        
        query.orderBy(ExamRecordsTable.timestamp, SortOrder.DESC)
            .map { row ->
                ExamRecord(
                    id = row[ExamRecordsTable.id],
                    studentName = row[ExamRecordsTable.studentName],
                    lessonTitle = row[ExamRecordsTable.lessonTitle],
                    totalQuestions = row[ExamRecordsTable.totalQuestions],
                    correctAnswers = row[ExamRecordsTable.correctAnswers],
                    wrongAnswers = row[ExamRecordsTable.wrongAnswers],
                    wrongDetails = row[ExamRecordsTable.wrongDetails],
                    timeSpentSeconds = row[ExamRecordsTable.timeSpentSeconds],
                    timestamp = row[ExamRecordsTable.timestamp],
                    groupId = row[ExamRecordsTable.groupId],
                    studentId = row[ExamRecordsTable.studentId],
                    subjectId = row[ExamRecordsTable.subjectId]?.value
                )
            }
    }

    fun getAllGroupsWithStats(subjectId: Int): List<Pair<Group, Int>> = transaction {
        val groups = getAllGroups(subjectId)
        groups.map { group ->
            val records = getGroupRecords(group.id, subjectId)
            val avg = if (records.isNotEmpty()) {
                records.map { (it.correctAnswers.toFloat() / it.totalQuestions * 100).toInt() }.average().toInt()
            } else 0
            group to avg
        }
    }
}
