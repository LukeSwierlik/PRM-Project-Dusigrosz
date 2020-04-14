package com.example.prm_project_dusigrosz.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm_project_dusigrosz.R
import com.example.prm_project_dusigrosz.adapters.CardViewAdapter
import com.example.prm_project_dusigrosz.database.DatabaseHelper
import com.example.prm_project_dusigrosz.database.TableInfo
import com.example.prm_project_dusigrosz.models.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.hasExtra("sumDebt")) {
            SUM_DEBT_VALUE.text = intent.getStringExtra("sumDebt")
        }
    }

    override fun onResume() {
        super.onResume()

        val dbHelper = DatabaseHelper(applicationContext)
        val db = dbHelper.writableDatabase

        val cursor = db.query(TableInfo.TABLE_NAME, null, null, null, null, null, null)
        val users = ArrayList<UserViewModel>()

        if (cursor.count > 0) {
            EMPTY_STATE_CONTAINER.visibility = View.INVISIBLE
            LIST_CONTAINER.visibility = View.VISIBLE

            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
                val user =
                    UserViewModel()

                user.id = cursor.getInt(0)
                user.firstName = cursor.getString(1)
                user.lastName = cursor.getString(2)
                user.phoneNumber = cursor.getString(3)
                user.debt = cursor.getDouble(4)

                users.add(user)
                cursor.moveToNext()
            }
        } else {
            EMPTY_STATE_CONTAINER.visibility = View.VISIBLE
            LIST_CONTAINER.visibility = View.INVISIBLE
        }

        cursor.close()

        val sumDebt: Double = users.fold(0.0) { initial, user -> initial + user.debt  }
        SUM_DEBT_VALUE.text = sumDebt.toString()

        RECYCLER_VIEW_CARDS.layoutManager = LinearLayoutManager(applicationContext)
        RECYCLER_VIEW_CARDS.adapter =
            CardViewAdapter(
                applicationContext,
                db,
                users
            )
    }

    fun onClickUserDetails(view: View) {
        val intent = Intent(applicationContext, DetailsActivity::class.java)
        startActivity(intent)
    }
}
