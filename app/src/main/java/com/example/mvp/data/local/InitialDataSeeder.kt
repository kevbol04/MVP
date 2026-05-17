package com.example.mvp.data.local

import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.dao.ClubDao
import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.dao.TrainingDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * IMPORTANTE:
 *
 * Esta clase existe SOLO para desarrollo y pruebas.
 *
 * Su función es cargar automáticamente el usuario demo:
 *
 *      user@gmail.com / 1234
 *
 * junto con jugadores, partidos y entrenamientos de ejemplo.
 *
 * Se ejecuta antes de mostrar Login/Dashboard para evitar que el usuario demo
 * tarde en crearse y falle el primer intento de login.
 *
 * ANTES DE PUBLICAR LA APP:
 *
 * 1. Eliminar esta clase: InitialDataSeeder.kt
 * 2. Eliminar SamplePayLoad.kt si ya no se usan datos demo.
 * 3. Quitar InitialDataSeeder del constructor de SessionViewModel.
 * 4. Quitar la llamada initialDataSeeder.seedDataIfNeeded() en SessionViewModel.
 *
 * No usar esta clase en producción si no quieres que la app cree usuarios/datos demo.
 */
@Singleton
class InitialDataSeeder @Inject constructor(
    private val authUserDao: AuthUserDao,
    private val playerDao: PlayerDao,
    private val matchDao: MatchDao,
    private val trainingDao: TrainingDao,
    private val clubDao: ClubDao
) {
    private val mutex = Mutex()

    /**
     * Carga los datos de prueba solo si todavía no existe el usuario demo.
     *
     * SamplePayLoad.seed(...) ya comprueba internamente si user@gmail.com existe,
     * por lo que no duplica datos en cada arranque.
     */
    suspend fun seedDataIfNeeded() {
        mutex.withLock {
            SamplePayLoad.seed(
                authUserDao = authUserDao,
                playerDao = playerDao,
                matchDao = matchDao,
                trainingDao = trainingDao,
                clubDao = clubDao
            )
        }
    }
}
