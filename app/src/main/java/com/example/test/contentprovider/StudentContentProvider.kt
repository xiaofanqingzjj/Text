package com.example.test.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

/**
 *
 * ContentProvider允许其他的App访问该App提供的数据
 *
 */
class StudentContentProvider : ContentProvider() {

    companion object {
        const val TAG = "StudentContentProvider"


        //这里的AUTHORITY就是我们在AndroidManifest.xml中配置的authorities

        //
        private const val AUTHORITY = "com.example.test.studentProvider"


        val matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        private const val CODE_INSERT = 0
        private const val CODE_DELETE = 1
        private const val CODE_UPDATE = 2
        const val CODE_QUERY_ALL = 3
        const val CODE_QUERY_ONE  = 4

        const val TABLE_NAME = StudentOpenHelper.TABLE_PERSON


        init {
//            matcher.addURI(AUTHORITY, "person", CODE_INSERT)
//            matcher.addURI(AUTHORITY, "person", CODE_DELETE)
//            matcher.addURI(AUTHORITY, "person", CODE_UPDATE)
            matcher.addURI(AUTHORITY, "person", CODE_QUERY_ALL)
            matcher.addURI(AUTHORITY, "person/#", CODE_QUERY_ONE)
        }
    }

    lateinit var dbHelper: StudentOpenHelper

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate")
        dbHelper = StudentOpenHelper(context = context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        val code = matcher.match(uri)
        Log.d(TAG, "query uri:$uri, projection:$projection, selection:$selection, selectionArgs:$selectionArgs, sortOrder:$sortOrder, code:$code")
        val db = dbHelper.readableDatabase
        when (code) {
            CODE_QUERY_ALL-> {
                return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            CODE_QUERY_ONE -> {
                val id = ContentUris.parseId(uri)
                return db.query(TABLE_NAME, projection, "id=?", arrayOf("$id"), null, null, sortOrder)
            }
        }
        return null;
    }

    override fun getType(uri: Uri): String? {
        return "";
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert uri:$uri, values:$values")
        val db = dbHelper.writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return ContentUris.withAppendedId(uri, id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "delete uri:$uri, selection:$selection selectionArgs:$selectionArgs,")
        val db = dbHelper.writableDatabase
        val count = db.delete(TABLE_NAME, selection, selectionArgs)
        db.close()
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "update uri:$uri, selection:$selection selectionArgs:$selectionArgs,")
        val db = dbHelper.writableDatabase
        val count = db.update(TABLE_NAME, values, selection, selectionArgs)
        db.close()
        return count
    }
}