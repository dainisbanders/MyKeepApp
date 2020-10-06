package lv.db.mobile.rtu_android.keep

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import lv.db.mobile.rtu_android.keep.adapter.KeepNotesRecyclerAdapter
import lv.db.mobile.rtu_android.keep.database.Database
import lv.db.mobile.rtu_android.keep.database.KeepNote


class MainActivity : AppCompatActivity(), AdapterClickListener {

    private val db get() = Database.getInstance(this)

    private val notes = mutableListOf<KeepNote>()

    private lateinit var adapter: KeepNotesRecyclerAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val theme =
            getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE)
                .getInt(APP_THEME, R.style.AppThemeLight)
        setTheme(theme)

        setContentView(R.layout.activity_main)

        layoutManager =
            StaggeredGridLayoutManager(
                resources.getInteger(R.integer.span_count), StaggeredGridLayoutManager.VERTICAL
            ).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }
        mainNotes.layoutManager = layoutManager

        notes.addAll(db.keepNotesDao().getAll())
        adapter = KeepNotesRecyclerAdapter(
            this,
            notes
        )
        mainNotes.adapter = adapter

        mainButtonAdd.setOnClickListener { appendItem() }
    }

    private fun appendItem() {
        val intent = Intent(this, DetailActivity::class.java)
            .putExtra(EXTRA_ACTION, EXTRA_ACTION_INSERT)
            .putExtra(EXTRA_ID, 0L)
        startActivityForResult(intent, REQUEST_CODE_DETAILS)
    }

    override fun itemClicked(note: KeepNote) {
        val intent = Intent(this, DetailActivity::class.java)
            .putExtra(EXTRA_ACTION, EXTRA_ACTION_VIEW)
            .putExtra(EXTRA_ID, note.id)
        startActivityForResult(intent, REQUEST_CODE_DETAILS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAILS && resultCode == RESULT_OK && data != null) {
            val id = data.getLongExtra(EXTRA_ID, 0L)
            when (data.getStringExtra(EXTRA_ACTION)) {
                EXTRA_ACTION_INSERT -> {
                    val note = db.keepNotesDao().getItemById(id)
                    notes.add(note)
                    notes.sortBy { it.id }
                    notes.reverse()
                    adapter.notifyDataSetChanged()
                }
                EXTRA_ACTION_DELETE -> {
                    val position = notes.indexOfFirst { it.id == id }
                    notes.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
                else -> {
                    val note = db.keepNotesDao().getItemById(id)
                    val position = notes.indexOfFirst { it.id == note.id }
                    notes[position] = note
                    adapter.notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.snack -> {
                val currentTheme = getSharedPreferences(
                    getString(R.string.preferences_file_key),
                    Context.MODE_PRIVATE
                )
                    .getInt(APP_THEME, R.style.AppThemeLight)

                val newTheme = if (currentTheme == R.style.AppThemeLight) {
                    R.style.AppThemeDark
                } else {
                    R.style.AppThemeLight
                }
                setTheme(newTheme)

                getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE)
                    .edit()
                    .putInt(APP_THEME, newTheme)
                    .apply()

                recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID = "lv.db.mobile.extras.keep_notes_id"
        const val EXTRA_ACTION = "lv.db.mobile.extras.keep_notes_action"
        const val EXTRA_ACTION_INSERT = "lv.db.mobile.extras.keep_notes_action_insert"
        const val EXTRA_ACTION_DELETE = "lv.db.mobile.extras.keep_notes_action_delete"
        const val EXTRA_ACTION_VIEW = "lv.db.mobile.extras.keep_notes_action_view"
        const val REQUEST_CODE_DETAILS = 1234
        const val APP_THEME = "lv.db.mobile.extras.keep_notes_theme"
        const val IMAGE_PICK_CODE = 1000
    }
}

interface AdapterClickListener {
    fun itemClicked(note: KeepNote)
}