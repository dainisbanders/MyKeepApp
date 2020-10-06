package lv.db.mobile.rtu_android.keep.database

import androidx.room.*

@Entity(tableName = "keep_notes")
data class KeepNote(
    val title: String,
    val text: String,
    val color_id: Int,
    val image_path: String,
    @PrimaryKey(autoGenerate = true) var id: Long = 0
)

@Dao
interface KeepNotesDao {
    @Query("SELECT * FROM keep_notes ORDER BY id DESC")
    fun getAll(): List<KeepNote>

    @Query("SELECT * FROM keep_notes WHERE id = :noteId")
    fun getItemById(noteId: Long): KeepNote

    @Insert
    fun insertAll(vararg notes: KeepNote): List<Long>

    @Update
    fun update(note: KeepNote)

    @Delete
    fun delete(note: KeepNote)
}