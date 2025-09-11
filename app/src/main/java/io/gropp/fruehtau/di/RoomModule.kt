package io.gropp.fruehtau.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.gropp.fruehtau.io.db.AppDatabase
import io.gropp.fruehtau.io.db.PendingDownloadDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "app.db")
            // .fallbackToDestructiveMigration()
            .build()

    @Provides fun providePendingDownloadDao(db: AppDatabase): PendingDownloadDao = db.pendingDownloadDao()
}
