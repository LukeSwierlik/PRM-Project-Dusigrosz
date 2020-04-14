package com.example.prm_project_dusigrosz.activities

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.prm_project_dusigrosz.R
import com.example.prm_project_dusigrosz.database.DatabaseHelper
import com.example.prm_project_dusigrosz.database.TableInfo
import com.example.prm_project_dusigrosz.objects.Fields
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        viewExtraParamsInActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.SAVE_BUTTON) {
            val dbHelper =
                DatabaseHelper(
                    applicationContext
                )
            val db = dbHelper.writableDatabase

            val firstName = FIRST_NAME_EDIT_TEXT_DETAILS.text.toString()
            val lastName = LAST_NAME_EDIT_TEXT_DETAILS.text.toString()
            val phoneNumber = PHONE_NUMBER_EDIT_TEXT_DETAILS.text.toString()
            val debt = DEBT_EDIT_TEXT_DETAILS.text.toString()

            val value = ContentValues()
            value.put(TableInfo.TABLE_COLUMN_FIRST_NAME, firstName)
            value.put(TableInfo.TABLE_COLUMN_LAST_NAME, lastName)
            value.put(TableInfo.TABLE_COLUMN_PHONE_NUMBER, phoneNumber)
            value.put(TableInfo.TABLE_COLUMN_DEBT, debt)

            if (intent.hasExtra(Fields.ID)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Update user information")

                builder.setMessage("Are you sure you want to update user information?")

                builder.setPositiveButton("Yes"){ _, _ ->
                    updateUser(db, value)
                }

                builder.setNegativeButton("No"){ _, _ ->
                    Toast.makeText(applicationContext,"Canceled.",Toast.LENGTH_SHORT).show()
                }

                val dialog: AlertDialog = builder.create()

                dialog.show()

            } else {
                saveUser(firstName, lastName, phoneNumber, debt, db, value)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun moveToSimulationView(view: View) {
        val intent = Intent(applicationContext, SimulationActivity::class.java)

        val firstName = FIRST_NAME_EDIT_TEXT_DETAILS.text.toString()
        val lastName = LAST_NAME_EDIT_TEXT_DETAILS.text.toString()
        val phoneNumber = PHONE_NUMBER_EDIT_TEXT_DETAILS.text.toString()
        val debt = DEBT_EDIT_TEXT_DETAILS.text.toString()

        intent.putExtra(Fields.FIRST_NAME, firstName)
        intent.putExtra(Fields.LAST_NAME, lastName)
        intent.putExtra(Fields.PHONE_NUMBER, phoneNumber)
        intent.putExtra(Fields.DEBT, debt)

        startActivity(intent)
    }

    fun onClickReminderUser(view: View) {
        val smsIntent = Intent()
        val phoneNumber = PHONE_NUMBER_EDIT_TEXT_DETAILS.text.toString()

        smsIntent.data = Uri.parse("sms:$phoneNumber")
        smsIntent.action = Intent.ACTION_VIEW

        ContextCompat.startActivity(view.context, smsIntent, null)
    }

    private fun updateUser(db: SQLiteDatabase, value: ContentValues) {
        val where = BaseColumns._ID + "=?"
        val whereArgs = arrayOf(intent.getStringExtra(Fields.ID))

        db.update(TableInfo.TABLE_NAME, value, where, whereArgs)

        Toast.makeText(applicationContext,"Updated!",Toast.LENGTH_SHORT).show()

        db.close()
        onBackPressed()
    }

    private fun saveUser(firstName: String, lastName: String, phoneNumber:String, debt: String, db: SQLiteDatabase, value: ContentValues) {
        if (firstName.isNotEmpty() || lastName.isNotEmpty() || phoneNumber.isNotEmpty() || debt.isNotEmpty()) {
            db.insertOrThrow(TableInfo.TABLE_NAME, null, value)

            Toast
                .makeText(applicationContext, "New user saved!", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast
                .makeText(applicationContext, "Please complete all required data!", Toast.LENGTH_SHORT)
                .show()
        }

        db.close()
        onBackPressed()
    }

    private fun viewExtraParamsInActivity() {
        if (intent.hasExtra(Fields.FIRST_NAME) &&
            intent.hasExtra(Fields.LAST_NAME) &&
            intent.hasExtra(Fields.PHONE_NUMBER) &&
            intent.hasExtra(Fields.DEBT)) {

            FIRST_NAME_EDIT_TEXT_DETAILS.setText(intent.getStringExtra(Fields.FIRST_NAME))
            LAST_NAME_EDIT_TEXT_DETAILS.setText(intent.getStringExtra(Fields.LAST_NAME))
            PHONE_NUMBER_EDIT_TEXT_DETAILS.setText(intent.getStringExtra(Fields.PHONE_NUMBER))
            DEBT_EDIT_TEXT_DETAILS.setText(intent.getStringExtra(Fields.DEBT))

            SIMULATE_BTN.visibility = View.VISIBLE
            REMINDER_BTN.visibility = View.VISIBLE
        }
    }
}
