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
                // If we found options but no question text
                if (currentOptions.isNotEmpty()) {
                    warnings.add("$questionCounter-savol matni bo'sh bo'lganligi sababli o'tkazib yuborildi.")
                }
            } else if (currentOptions.size < 2) {
                warnings.add("\"$trimmedText\" savolida variantlar yetarli emas (kamida 2 ta bo'lishi kerak).")
            } else {
                val finalCorrectIndex = if (currentCorrectIndex == -1) {
                    warnings.add("\"$trimmedText\" savolida to'g'ri javob belgilanmagan, birinchi variant (A) tanlandi.")
                    0
                } else {
                    currentCorrectIndex
                }
                questions.add(Question(trimmedText, currentOptions.toList(), finalCorrectIndex))
            }
        }

        // Improved Regex for questions: 1. or 1) or 1 )
        val questionRegex = Regex("""^(\d+)\s*[\.\)]\s*(.*)""")
        // Improved Regex for options: A) or A. or a) or a. or 1) or 1.
        val optionRegex = Regex("""^([a-hA-H]|[1-9])\s*[\)\.]\s*(.*)""")

        for (line in lines) {
            val questionMatch = questionRegex.find(line)
            val optionMatch = optionRegex.find(line)

            if (questionMatch != null) {
                // It's a new question
                saveQuestion()
                currentQuestionText = questionMatch.groupValues[2].trim()
                currentOptions.clear()
                currentCorrectIndex = -1
                isBuildingOption = false
            } else if (optionMatch != null) {
                // It's an option
                isBuildingOption = true
                var rawOption = optionMatch.groupValues[2].trim()
                
                // Handle '*' if it's at the start or end of the option text
                if (rawOption.startsWith("*") || rawOption.endsWith("*")) {
                    currentCorrectIndex = currentOptions.size
                    rawOption = rawOption.removePrefix("*").removeSuffix("*").trim()
                }
                
                currentOptions.add(rawOption)
            } else {
                // It's a continuation of previous question or option
                if (isBuildingOption && currentOptions.isNotEmpty()) {
                    val lastIdx = currentOptions.size - 1
                    var updatedOption = currentOptions[lastIdx] + " " + line
                    
                    if (updatedOption.startsWith("*") || updatedOption.endsWith("*")) {
                        currentCorrectIndex = lastIdx
                        updatedOption = updatedOption.removePrefix("*").removeSuffix("*").trim()
                    }
                    currentOptions[lastIdx] = updatedOption
                } else {
                    // Append to question text
                    if (currentQuestionText.isEmpty()) {
                        // If it doesn't match numbering but it's text, it might be a question without a number
                        currentQuestionText = line
                    } else {
                        currentQuestionText += " " + line
                    }
                }
            }
        }

        // Save the last question
        saveQuestion()

        if (questions.isEmpty() && warnings.isEmpty()) {
            return ParseResult(error = "Faylda testlar topilmadi. Iltimos, formatni tekshiring: 1. Savol, A) Variant*")
        }

        return ParseResult(questions, warnings)
    }
}
