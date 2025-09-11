package io.gropp.fruehtau.io.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingDownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(value: PendingDownloadEntity): Long

    @Query("select * from pending_downloads where id = :id") suspend fun getById(id: Long): PendingDownloadEntity?

    @Query("delete from pending_downloads where id = :id") suspend fun deleteById(id: Long)

    @Query("select * from pending_downloads order by id desc") fun streamAll(): Flow<List<PendingDownloadEntity>>
}
