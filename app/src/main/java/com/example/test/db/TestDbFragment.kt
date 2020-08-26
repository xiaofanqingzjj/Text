package com.example.test.db

import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_test_db.*


class TestDbFragment : SimpleFragment() {

    companion object {
        const val TAG = "TestDbFragment"
    }

    private val mDatabase: SQLiteDatabase by lazy {
        DBHelper(context!!).writableDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_db)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn1.setOnClickListener {
            insert()
        }

        btn2.setOnClickListener {
            allPerson()
        }
    }

    private fun allPerson() {

        val cursor: Cursor? = mDatabase.query(PersonInfo.TABLE_NAME, PersonInfo.TABLE_COLUMNS, null, null, null, null, null)

        try {
            while (cursor != null && cursor.moveToNext()) {

                val student = PersonInfo()

                student.id = (cursor.getInt(cursor.getColumnIndex(PersonInfo.COLUMN_ID)))
                student.name = (cursor.getString(cursor.getColumnIndex(PersonInfo.COLUMN_NAME)))
                student.gender = (cursor.getInt(cursor.getColumnIndex(PersonInfo.COLUMN_GENDER)))
                student.number = (cursor.getString(cursor.getColumnIndex(PersonInfo.COLUMN_NUMBER)))
                student.score = (cursor.getInt(cursor.getColumnIndex(PersonInfo.COLUMN_SCORE)))

                Log.i(TAG, "queryData student: $student")
            }
        } catch (e: SQLException) {
            Log.e(TAG, "queryData exception", e)
        }
    }

    private fun insert() {
        val contentValues = ContentValues()
        contentValues.put("name", "peter")
        contentValues.put("gender", 0)
        contentValues.put("number", "201804081705")
        contentValues.put("score", "100")
        mDatabase.insertWithOnConflict(PersonInfo.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE)
    }


    override fun onDestroy() {
        super.onDestroy()
        mDatabase.close()
    }

}