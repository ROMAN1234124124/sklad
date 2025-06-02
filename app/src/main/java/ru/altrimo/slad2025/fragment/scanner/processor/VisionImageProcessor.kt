package ru.altrimo.slad2025.fragment.scanner.processor

import androidx.camera.core.ImageProxy

interface VisionImageProcessor {
    fun processImageProxy(image: ImageProxy, graphicOverlay: GraphicOverlay)
    fun stop()
}
