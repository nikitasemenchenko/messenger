package ru.magnum.messenger.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.magnum.messenger.data.local.entity.PendingMessageEntity

@Dao
interface PendingMessageDao {

    @Query("SELECT * FROM pending_messages ORDER BY createdAt ASC")
    suspend fun getPendingMessages(): List<PendingMessageEntity>

    @Query("SELECT COUNT(*) FROM pending_messages")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: PendingMessageEntity)

    @Query("DELETE FROM pending_messages WHERE id = :id")
    suspend fun deleteById(id: String)
}
