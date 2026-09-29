package ru.magnum.messenger.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.magnum.messenger.data.local.entity.MessageEntity
import ru.magnum.messenger.data.local.entity.PendingMessageEntity
import ru.magnum.messenger.data.local.room.dao.MessageDao

@Database(
    entities = [
        MessageEntity::class,
        PendingMessageEntity::class
               ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun messageDao(): MessageDao
}