package io.gropp.fruehtau.io.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PendingDownloadEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendingDownloadDao(): PendingDownloadDao
}
