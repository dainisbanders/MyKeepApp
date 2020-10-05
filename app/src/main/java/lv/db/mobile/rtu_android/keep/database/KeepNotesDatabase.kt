package lv.db.mobile.rtu_android.keep.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [KeepNote::class])
abstract class KeepNotesDatabase : RoomDatabase() {

    abstract fun keepNotesDao(): KeepNotesDao

}

object Database {

    private var instance: KeepNotesDatabase? = null

    fun getInstance(context: Context) = instance
        ?: Room.databaseBuilder(
        context.applicationContext, KeepNotesDatabase::class.java, "keep-notes-db"
    )
        .allowMainThreadQueries()
        .build()
        .also { instance = it }
}