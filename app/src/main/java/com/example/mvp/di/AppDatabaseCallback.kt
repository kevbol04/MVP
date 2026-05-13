package com.example.mvp.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDatabaseCallback @Inject constructor() : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // El seed de datos demo ya no se ejecuta desde Room.
        //
        // IMPORTANTE:
        // Antes se lanzaba aquí en segundo plano, pero eso provocaba que el login
        // pudiera aparecer antes de que user@gmail.com estuviera creado.
        //
        // Ahora se ejecuta de forma controlada desde SessionViewModel mediante InitialDataSeeder,
        // antes de mostrar Login/Dashboard.
        //
        // ANTES DE PUBLICAR:
        // Si eliminas InitialDataSeeder.kt y SamplePayLoad.kt, esta clase puede quedarse vacía
        // o eliminarse si ya no se usa en DatabaseModule.
    }
}
