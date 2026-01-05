package com.example.resol.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.resol.data.*

@Database(
    entities = [Track::class, Playlist::class, HistoryTrack::class, PlaylistTrackCrossRef::class],
    version = 6,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 5, to = 6)
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "resol_database" // Tên database mới
                )
                    // Nếu bạn muốn xóa dữ liệu cũ khi lỗi migration, hãy bỏ comment dòng dưới:
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}