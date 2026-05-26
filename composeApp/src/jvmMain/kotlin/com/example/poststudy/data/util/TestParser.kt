package com.example.poststudy.data.util

import com.example.poststudy.domain.model.ParseResult
import com.example.poststudy.domain.model.Question
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream

object TestParser {

    fun parseTest(filePath: String): ParseResult {
        val file = File(filePath)
        if (!file.exists()) return ParseResult(error = "Fayl topilmadi: $filePath")

        val fullText = try {
            if (filePath.endsWith(".docx", ignoreCase = true)) {
                FileInputStream(file).use { fis ->
                    val doc = XWPFDocument(fis)
                    val extractor = XWPFWordExtractor(doc)
                    // XWPFWordExtractor includes automatic numbering text
                    extractor.text
                }
            } else if (filePath.endsWith(".doc", ignoreCase = true)) {
                FileInputStream(file).use { fis ->
                    val extractor = WordExtractor(fis)
                    // WordExtractor for legacy .doc also includes numbering
                    extractor.text
                }
            } else {
                return ParseResult(error = "Qo'llab-quvvatlanmaydigan fayl formati. Faqat .doc va .docx ishlaydi.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ParseResult(error = "Faylni o'qishda xatolik yuz berdi: ${e.message}")
        }

        if (fullText.isBlank()) return ParseResult(error = "Fayl bo'sh")

        return parseContent(fullText)
    }

    private fun parseContent(content: String): ParseResult {
        val questions = mutableListOf<Question>()
        val warnings = mutableListOf<String>()
        
        // Normalize lines: remove tabs, replace multiple spaces with single space
        val lines = content.split("\n", "\r")
            .map { it.replace("\t", " ").replace(Regex("\\s+"), " ").trim() }
            .filter { it.isNotEmpty() }

        var currentQuestionText = ""
        val currentOptions = mutableListOf<String>()
        var currentCorrectIndex = -1
        var isBuildingOption = false
        var questionCounter = 0

        fun saveQuestion() {
            if (currentQuestionText.isBlank() && currentOptions.isEmpty()) return

            questionCounter++
            
            val trimmedText = currentQuestionText.trim()
            if (trimmedText.isBlank()) {
                if (currentOptions.isNotEmpty()) {
                    warnings.add("$questionCounter-savol matni bo'sh bo'lganligi sababli o'tkazib yuborildi.")
                }
            } else if (currentOptions.size < 2) {
                warnings.add("\"${trimmedText.take(30)}...\" savolida variantlar yetarli emas (kamida 2 ta bo'lishi kerak).")
            } else {
                val finalCorrectIndex = if (currentCorrectIndex == -1) {
                    warnings.add("\"${trimmedText.take(30)}...\" savolida to'g'ri javob belgilanmagan, default sifatida A varianti to'g'ri deb olindi.")
                    0
                } else {
                    currentCorrectIndex
                }
                questions.add(Question(trimmedText, currentOptions.toList(), finalCorrectIndex))
            }
        }

        // Improved Regex for questions: 1., 1), 1-savol., S1:, 1 - savol
        val questionRegex = Regex("""^(?:savol\s*|s\s*|vopros\s*|q\s*)?(\d+)(?:\s*-?\s*(?:savol|vopros))?\s*[\.\)\:]\s*(.*)""", RegexOption.IGNORE_CASE)
        // Improved Regex for options: A), A., 1-variant., V1)
        val optionRegex = Regex("""^(?:variant\s*|javob\s*|v\s*)?([a-h]|[1-9])(?:\s*-?\s*(?:variant|javob))?\s*[\)\.\:]\s*(.*)""", RegexOption.IGNORE_CASE)

        for (rawLine in lines) {
            val isStarred = rawLine.startsWith("*")
            val isEndStarred = rawLine.endsWith("*")
            
            var line = rawLine
            if (isStarred) line = line.removePrefix("*").trim()
            if (isEndStarred) line = line.removeSuffix("*").trim()

            var isQuestion = false
            var isOption = false
            var text = ""

            val optMatch = optionRegex.find(line)
            val qMatch = questionRegex.find(line)

            if (optMatch != null && qMatch != null) {
                val qText = qMatch.groupValues[2]
                val oText = optMatch.groupValues[2]
                val lowerLine = line.lowercase()
                
                if ((lowerLine.contains("variant") || lowerLine.contains("javob")) && !lowerLine.contains("savol")) {
                    isOption = true
                    text = oText
                } else if (lowerLine.contains("savol") || lowerLine.contains("vopros")) {
                    isQuestion = true
                    text = qText
                } else {
                    isQuestion = true
                    text = qText
                }
            } else if (optMatch != null) {
                isOption = true
                text = optMatch.groupValues[2]
            } else if (qMatch != null) {
                isQuestion = true
                text = qMatch.groupValues[2]
            }

            if (isQuestion) {
                saveQuestion()
                currentQuestionText = text.trim()
                currentOptions.clear()
                currentCorrectIndex = -1
                isBuildingOption = false
            } else if (isOption) {
                isBuildingOption = true
                val rawOpt = text.trim()
                
                if (isStarred || isEndStarred) {
                    currentCorrectIndex = currentOptions.size
                }
                
                currentOptions.add(rawOpt)
            } else {
                if (isBuildingOption && currentOptions.isNotEmpty()) {
                    val lastIdx = currentOptions.size - 1
                    val updatedOption = currentOptions[lastIdx] + " " + line
                    
                    if (isStarred || isEndStarred) {
                        currentCorrectIndex = lastIdx
                    }
                    currentOptions[lastIdx] = updatedOption.trim()
                } else {
                    if (currentQuestionText.isEmpty()) {
                        currentQuestionText = line
                    } else {
                        currentQuestionText += " " + line
                    }
                }
            }
        }

        saveQuestion()

        if (questions.isEmpty() && warnings.isEmpty()) {
            return ParseResult(error = "Faylda testlar topilmadi. Iltimos, formatni tekshiring (masalan: 1. Savol, A) Variant).")
        }

        return ParseResult(questions, warnings)
    }
}
