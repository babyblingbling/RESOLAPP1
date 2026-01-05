package com.example.resol.data.local

import androidx.room.InvalidationTracker

class AppDatabaseImpl : AppDatabase() {
    override fun musicDao(): MusicDao {
        TODO("Not yet implemented")
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }
}