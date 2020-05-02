package com.example.prm_project_dusigrosz.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.example.prm_project_dusigrosz.database.TableInfo.TABLE_NAME


class MyContentProvider : ContentProvider() {
    private var dbHelper: DatabaseHelper? = null

    companion object {
        private const val ALL_DEBT = 1
        private const val SINGLE_DEBT = 2

        private const val AUTHORITY = "com.example.prm_project_dusigrosz.provider"

        val CONTENT_URI: Uri =
            Uri.parse("content://$AUTHORITY/niggard")

        private var uriMatcher: UriMatcher? = null

        init {
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher!!.addURI(
                AUTHORITY,
                "niggard",
                ALL_DEBT
            )
            uriMatcher!!.addURI(
                AUTHORITY,
                "niggard/#",
                SINGLE_DEBT
            )
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = DatabaseHelper(context!!)
        return false
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher!!.match(uri)) {
            ALL_DEBT -> "vnd.android.cursor.dir/vnd.${AUTHORITY}.niggard"
            SINGLE_DEBT -> "vnd.android.cursor.item/vnd.${AUTHORITY}.niggard"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val db: SQLiteDatabase? = dbHelper?.writableDatabase

        when (uriMatcher!!.match(uri)) {
            ALL_DEBT -> {
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }

        val id = db?.insert(TABLE_NAME, null, values)
        context!!.contentResolver.notifyChange(uri, null)

        return Uri.parse("$CONTENT_URI/$id")
    }

    override fun query(
        uri: Uri, projection: Array<String?>?, selection: String?,
        selectionArgs: Array<String?>?, sortOrder: String?
    ): Cursor {
        val db: SQLiteDatabase? = dbHelper?.writableDatabase
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = TABLE_NAME

        when (uriMatcher!!.match(uri)) {
            ALL_DEBT -> {
            }
            SINGLE_DEBT -> {
                val id: String = uri.pathSegments[1]
                queryBuilder.appendWhere("_ID=$id")
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }

        return queryBuilder.query(
            db, projection, selection,
            selectionArgs, null, null, sortOrder
        )
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        var selection = selection
        val db: SQLiteDatabase? = dbHelper?.writableDatabase
        when (uriMatcher!!.match(uri)) {
            ALL_DEBT -> {
            }
            SINGLE_DEBT -> {
                val id: String = uri.pathSegments[1]
                selection = ("_ID=$id"
                        + if (!TextUtils.isEmpty(selection)) " AND ($selection)" else "")
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        val deleteCount = db!!.delete(TABLE_NAME, selection, selectionArgs)
        context!!.contentResolver.notifyChange(uri, null)

        return deleteCount
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        var selection = selection
        val db: SQLiteDatabase? = dbHelper?.writableDatabase
        when (uriMatcher!!.match(uri)) {
            ALL_DEBT -> {
            }
            SINGLE_DEBT -> {
                val id: String = uri.pathSegments[1]
                selection = ("_ID=$id"
                        + if (!TextUtils.isEmpty(selection)) " AND ($selection)" else "")
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        val updateCount = db!!.update(TABLE_NAME, values, selection, selectionArgs)
        context!!.contentResolver.notifyChange(uri, null)

        return updateCount
    }
}