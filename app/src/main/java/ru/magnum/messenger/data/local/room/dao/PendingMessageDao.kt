package ru.magnum.messenger.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.magnum.messenger.data.local.entity.PendingMessageEntity

@Dao
interface PendingMessageDao {

    @Query("SELECT * FROM pending_messages")
    suspend fun getPendingMessages(): List<PendingMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        message: PendingMessageEntity
    )

    @Delete
    suspend fun delete(
        message: PendingMessageEntity
    )
}