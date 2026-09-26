package ru.magnum.messenger.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.magnum.messenger.data.repository.AuthRepositoryImpl
import ru.magnum.messenger.data.repository.ProfileRepositoryImpl
import ru.magnum.messenger.domain.repository.AuthRepository
import ru.magnum.messenger.domain.repository.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        implementation: ProfileRepositoryImpl
    ): ProfileRepository
}