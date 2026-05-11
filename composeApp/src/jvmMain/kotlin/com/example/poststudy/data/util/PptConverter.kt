package com.example.poststudy.data.util

import org.apache.poi.hslf.usermodel.HSLFSlideShow
import org.apache.poi.xslf.usermodel.XMLSlideShow
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream

object PptConverter {
    fun convertSlidesToImages(filePath: String): List<BufferedImage> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return if (filePath.endsWith(".pptx", ignoreCase = true)) {
            renderPptx(file)
        } else if (filePath.endsWith(".ppt", ignoreCase = true)) {
            renderPpt(file)
        } else {
            emptyList()
        }
    }

    private fun renderPptx(file: File): List<BufferedImage> {
        val images = mutableListOf<BufferedImage>()
        FileInputStream(file).use { fis ->
            val ppt = XMLSlideShow(fis)
            val pageSize = ppt.pageSize
            ppt.slides.forEach { slide ->
                val img = BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB)
                val graphics = img.createGraphics()
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
                graphics.scale(1280.0 / pageSize.width, 720.0 / pageSize.height)
                slide.draw(graphics)
                graphics.dispose()
                images.add(img)
            }
        }
        return images
    }

    private fun renderPpt(file: File): List<BufferedImage> {
        val images = mutableListOf<BufferedImage>()
        FileInputStream(file).use { fis ->
            val ppt = HSLFSlideShow(fis)
            val pageSize = ppt.pageSize
            ppt.slides.forEach { slide ->
                val img = BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB)
                val graphics = img.createGraphics()
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
                graphics.scale(1280.0 / pageSize.width, 720.0 / pageSize.height)
                slide.draw(graphics)
                graphics.dispose()
                images.add(img)
            }
        }
        return images
    }
}
