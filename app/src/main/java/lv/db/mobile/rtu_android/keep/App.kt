package lv.db.mobile.rtu_android.keep

import android.app.Application
import androidx.room.Room
import lv.db.mobile.rtu_android.keep.database.KeepNotesDatabase

class App : Application() {

    val db by lazy {
        Room.databaseBuilder(this, KeepNotesDatabase::class.java, "keep-notes-db")
            .allowMainThreadQueries()
            .build()
    }
}