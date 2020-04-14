package com.example.prm_project_dusigrosz.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.prm_project_dusigrosz.database.BasicCommands
import com.example.prm_project_dusigrosz.database.TableInfo

class DatabaseHelper (context: Context): SQLiteOpenHelper(context, TableInfo.TABLE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(BasicCommands.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(BasicCommands.SQL_DELETE_TABLE)
        onCreate(db)
    }
}