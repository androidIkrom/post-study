package com.example.poststudy.data.util

import com.example.poststudy.domain.model.Question
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream

object TestParser {

    fun parseTest(filePath: String): List<Question> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        val fullText = try {
            if (filePath.endsWith(".docx", ignoreCase = true)) {
                FileInputStream(file).use { fis ->
                    XWPFDocument(fis).paragraphs.joinToString("\n") { it.text }
                }
            } else if (filePath.endsWith(".doc", ignoreCase = true)) {
                FileInputStream(file).use { fis ->
                    HWPFDocument(fis).range.text()
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        return parseContent(fullText)
    }

    private fun parseContent(content: String): List<Question> {
        val questions = mutableListOf<Question>()
        val lines = content.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        var currentQuestionText = ""
        val currentOptions = mutableListOf<String>()
        var currentCorrectIndex = -1
        var isBuildingOption = false

        fun saveQuestion() {
            if (currentQuestionText.isNotBlank() && currentOptions.size >= 2 && currentCorrectIndex != -1) {
                questions.add(Question(currentQuestionText.trim(), currentOptions.toList(), currentCorrectIndex))
            }
        }

        for (line in lines) {
            val questionMatch = Regex("""^\d+\.(.+)""").find(line)
            val optionMatch = Regex("""^([a-hA-H]|[1-9])\)(.+)""").find(line)

            if (questionMatch != null) {
                saveQuestion()
                currentQuestionText = questionMatch.groupValues[1].trim()
                currentOptions.clear()
                currentCorrectIndex = -1
                isBuildingOption = false
            } else if (optionMatch != null) {
                isBuildingOption = true
                var rawOption = optionMatch.groupValues[2].trim()
                if (rawOption.endsWith("*")) {
                    currentCorrectIndex = currentOptions.size
                    rawOption = rawOption.removeSuffix("*").trim()
                }
                currentOptions.add(rawOption)
            } else {
                // Continued text from previous line
                if (isBuildingOption && currentOptions.isNotEmpty()) {
                    val lastIdx = currentOptions.size - 1
                    var updatedOption = currentOptions[lastIdx] + " " + line
                    if (updatedOption.endsWith("*")) {
                        currentCorrectIndex = lastIdx
                        updatedOption = updatedOption.removeSuffix("*").trim()
                    }
                    currentOptions[lastIdx] = updatedOption
                } else {
                    currentQuestionText += " " + line
                }
            }
        }

        saveQuestion()
        return questions
    }
}
