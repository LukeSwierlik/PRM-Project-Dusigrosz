package com.example.prm_project_dusigrosz.database

import android.provider.BaseColumns

object TableInfo: BaseColumns {
    const val TABLE_NAME = "Niggard"
    const val TABLE_COLUMN_FIRST_NAME = "FirstName"
    const val TABLE_COLUMN_LAST_NAME = "LastName"
    const val TABLE_COLUMN_PHONE_NUMBER = "PhoneNumber"
    const val TABLE_COLUMN_DEBT = "Debt"
}

object BasicCommands {
    const val SQL_CREATE_TABLE = "CREATE TABLE ${TableInfo.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "${TableInfo.TABLE_COLUMN_FIRST_NAME} TEXT NOT NULL, " +
            "${TableInfo.TABLE_COLUMN_LAST_NAME} TEXT NOT NULL, " +
            "${TableInfo.TABLE_COLUMN_PHONE_NUMBER} TEXT NOT NULL, " +
            "${TableInfo.TABLE_COLUMN_DEBT} DOUBLE NOT NULL)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${TableInfo.TABLE_NAME}"
}