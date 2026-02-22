package com.example.mvp.di

import com.example.mvp.data.repository.AuthRepositoryImpl
import com.example.mvp.data.repository.MatchRepositoryImpl
import com.example.mvp.data.repository.TrainingRepositoryImpl
import com.example.mvp.domain.repository.AuthRepository
import com.example.mvp.domain.repository.MatchRepository
import com.example.mvp.domain.repository.TrainingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTrainingRepository(impl: TrainingRepositoryImpl): TrainingRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository
}