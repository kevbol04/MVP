package com.example.mvp.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PlayerValidatorTest {

    @Test
    fun `normalized does not silently change invalid age or number`() {
        val player = validPlayer(age = 99, number = 150).normalized()

        assertEquals(99, player.age)
        assertEquals(150, player.number)
    }

    @Test
    fun `validateForSave rejects invalid age instead of correcting it`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            PlayerValidator.validateForSave(validPlayer(age = 99))
        }

        assertEquals("La edad debe estar entre $PLAYER_MIN_AGE y $PLAYER_MAX_AGE.", exception.message)
    }

    @Test
    fun `validateForSave rejects invalid number instead of correcting it`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            PlayerValidator.validateForSave(validPlayer(number = 150))
        }

        assertEquals("El dorsal debe estar entre $PLAYER_MIN_NUMBER y $PLAYER_MAX_NUMBER.", exception.message)
    }

    @Test
    fun `repairedFromStorage can repair legacy corrupted values`() {
        val repaired = validPlayer(name = "   ", age = 99, number = 150).repairedFromStorage()

        assertEquals("Jugador sin nombre", repaired.name)
        assertEquals(PLAYER_MAX_AGE, repaired.age)
        assertEquals(PLAYER_MAX_NUMBER, repaired.number)
    }

    private fun validPlayer(
        name: String = "Kevin Cortes",
        age: Int = 22,
        number: Int = 10
    ) = Player(
        name = name,
        position = PlayerPosition.MED,
        age = age,
        number = number,
        status = PlayerStatus.DISPONIBLE,
        level = PlayerLevel.BUENO,
        style = defaultStyleFor(PlayerPosition.MED)
    )
}