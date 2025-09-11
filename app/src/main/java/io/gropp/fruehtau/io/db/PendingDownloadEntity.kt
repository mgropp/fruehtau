package io.gropp.fruehtau.io.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import io.gropp.fruehtau.io.download.DownloadPurpose

@Entity(tableName = "pending_downloads", indices = [Index("id")])
data class PendingDownloadEntity(@PrimaryKey val id: Long = 0, val purpose: DownloadPurpose, val target: String)
