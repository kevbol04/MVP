package com.example.mvp.domain.model

enum class PlayerLevel(val label: String, val base: Int) {
    LIMITADO("Limitado", 58),
    NORMAL("Normal", 66),
    BUENO("Bueno", 74),
    DESTACADO("Destacado", 82),
    ESTRELLA("Estrella", 90)
}