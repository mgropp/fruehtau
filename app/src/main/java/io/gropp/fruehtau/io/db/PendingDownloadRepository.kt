package io.gropp.fruehtau.io.db

import io.gropp.fruehtau.io.download.DownloadPurpose
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingDownloadRepository @Inject constructor(private val dao: PendingDownloadDao) {
    suspend fun add(id: Long, purpose: DownloadPurpose, source: String, target: String?) =
        dao.insert(PendingDownloadEntity(id = id, purpose = purpose, source = source, target = target))

    suspend fun get(id: Long): PendingDownloadEntity? = dao.getById(id)

    suspend fun delete(id: Long) = dao.deleteById(id)
}
