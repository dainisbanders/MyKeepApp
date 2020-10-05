package lv.db.mobile.rtu_android.keep

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import lv.db.mobile.rtu_android.keep.MainActivity.Companion.EXTRA_ACTION
import lv.db.mobile.rtu_android.keep.MainActivity.Companion.EXTRA_ACTION_DELETE
import lv.db.mobile.rtu_android.keep.MainActivity.Companion.EXTRA_ACTION_INSERT
import lv.db.mobile.rtu_android.keep.MainActivity.Companion.EXTRA_ACTION_VIEW
import lv.db.mobile.rtu_android.keep.MainActivity.Companion.EXTRA_ID
import lv.db.mobile.rtu_android.keep.database.Database
import lv.db.mobile.rtu_android.keep.database.KeepNote


class DetailActivity : AppCompatActivity() {

    private val db get() = Database.getInstance(this)
    private val TAG = "DetailsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val theme =
            getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE)
                .getInt(MainActivity.APP_THEME, R.style.AppThemeLight)
        setTheme(theme)

        setContentView(R.layout.activity_detail)

        val id = intent.getLongExtra(EXTRA_ID, 0L)
        val action = intent.getStringExtra(EXTRA_ACTION)

        if (EXTRA_ACTION_VIEW == action) {
            val note = db.keepNotesDao().getItemById(id)
            detailsTitle.setText(note.title)
            detailsText.setText(note.text)
            detailsColor.setSelection(note.color_id)
            detailsUpsert.text = getString(R.string.btn_update)
            detailsDelete.visibility = View.VISIBLE
        } else {
            detailsDelete.visibility = View.INVISIBLE
            detailsShareToEmail.visibility = View.INVISIBLE
            detailsUpsert.text = getString(R.string.btn_insert)
        }

        detailsUpsert.setOnClickListener {
            if (EXTRA_ACTION_INSERT == action) {
                if (detailsTitle.text.toString() == "" && detailsText.text.toString() == "") {
                    val builder = AlertDialog.Builder(this)

                    builder.setTitle(getString(R.string.insert_alert_title))
                        .setMessage(getString(R.string.insert_alert_text))
                        .setPositiveButton(getString(R.string.insert_alert_button_ok)) { _, _ -> }

                    val dialog = builder.create()
                    dialog.show()
                } else {
                    val note = KeepNote(
                        detailsTitle.text.toString(),
                        detailsText.text.toString(),
                        detailsColor.selectedItemPosition
                    )
                    note.id = db.keepNotesDao().insertAll(note).first()
                    val intent = Intent().putExtra(EXTRA_ID, note.id).putExtra(EXTRA_ACTION, action)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else {
                val note = db.keepNotesDao().getItemById(id)
                db.keepNotesDao().update(
                    note.copy(
                        title = detailsTitle.text.toString(),
                        text = detailsText.text.toString(),
                        color_id = detailsColor.selectedItemPosition
                    )
                )
                val intent = Intent().putExtra(EXTRA_ID, note.id).putExtra(EXTRA_ACTION, action)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        detailsDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle(getString(R.string.delete_alert_title))
                .setMessage(getString(R.string.delete_alert_text))
                .setPositiveButton(getString(R.string.delete_alert_button_ok)) { _, _ ->
                    val note = db.keepNotesDao().getItemById(id)
                    db.keepNotesDao().delete(note)
                    val intent = Intent().putExtra(EXTRA_ID, note.id)
                        .putExtra(EXTRA_ACTION, EXTRA_ACTION_DELETE)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                .setNegativeButton(getString(R.string.delete_alert_button_cancel)) { _, _ -> }

            val dialog = builder.create()
            dialog.show()

        }


        detailsColor.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                Log.v(TAG, "setOnItemSelectedListener!!!" + position );
                detailsLayout.setBackgroundColor(when (position) {
                    1 -> Color.GREEN
                    2 -> Color.YELLOW
                    3 -> Color.parseColor("#FFCCCB")
                    4 -> Color.GRAY
                    else -> Color.parseColor("#E3F2FD")
                })
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        })

        detailsShareToEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                //putExtra(Intent.EXTRA_EMAIL, arrayOf(detailsEmail.text.toString()))
                putExtra(Intent.EXTRA_SUBJECT, detailsTitle.text.toString())
                putExtra(Intent.EXTRA_TEXT, detailsText.text.toString())
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }
}

