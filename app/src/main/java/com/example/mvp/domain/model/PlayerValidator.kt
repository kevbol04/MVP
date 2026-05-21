package com.example.mvp.domain.model

object PlayerValidator {

    private val validNameRegex = Regex("""^[\p{L}' -]+$""")
    private val duplicatedSpacesRegex = Regex("""\s{2,}""")

    fun validateForSave(player: Player) {
        validateName(player.name)?.let { throw IllegalArgumentException(it) }
        validateAge(player.age)?.let { throw IllegalArgumentException(it) }
        validateNumber(player.number)?.let { throw IllegalArgumentException(it) }

        require(player.rating in PLAYER_MIN_RATING..PLAYER_MAX_RATING) {
            "La valoración debe estar entre $PLAYER_MIN_RATING y $PLAYER_MAX_RATING."
        }

        require(player.style in stylesFor(player.position)) {
            "El estilo ${player.style.label} no es válido para la posición ${player.position.label}."
        }
    }

    fun validateName(raw: String): String? {
        val name = raw.trim()

        if (name.isBlank()) return "El nombre es obligatorio."
        if (name.length < PLAYER_MIN_NAME_LENGTH) {
            return "El nombre debe tener al menos $PLAYER_MIN_NAME_LENGTH caracteres."
        }
        if (name.length > PLAYER_MAX_NAME_LENGTH) {
            return "El nombre no puede superar $PLAYER_MAX_NAME_LENGTH caracteres."
        }
        if (name.contains(duplicatedSpacesRegex)) return "Evita espacios dobles."
        if (!validNameRegex.matches(name)) {
            return "Solo letras y espacios (se permite ' y -)."
        }

        return null
    }

    fun validateAge(age: Int): String? =
        if (age in PLAYER_MIN_AGE..PLAYER_MAX_AGE) {
            null
        } else {
            "La edad debe estar entre $PLAYER_MIN_AGE y $PLAYER_MAX_AGE."
        }

    fun validateNumber(number: Int): String? =
        if (number in PLAYER_MIN_NUMBER..PLAYER_MAX_NUMBER) {
            null
        } else {
            "El dorsal debe estar entre $PLAYER_MIN_NUMBER y $PLAYER_MAX_NUMBER."
        }
}