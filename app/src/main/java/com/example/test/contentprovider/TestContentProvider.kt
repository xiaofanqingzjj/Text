package com.example.test.contentprovider

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.bedrock.module_base.MenuActivity

class TestContentProvider : MenuActivity() {

    companion object {

        const val TAG = "TestContentProviderTAG";


        // contentProvider uri的格式约定为：
        // content://(固定位content)，要访问的ContentProvider声明的authories/表名/操作
        private const val BASE_URI = "content://com.example.test.studentProvider/person"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("Insert") {
            val insertUri = Uri.parse("$BASE_URI")
            val insertEdData = contentResolver.insert(insertUri, ContentValues().apply {
                put("name", "xiaoxiao")
                put("phone", "1344444400")
                put("age", 30)
                put("address", "addrr")
            })
            Log.d(TAG, "insertedData:$insertEdData")
        }

        addMenu("Delete") {
            val uri = Uri.parse("$BASE_URI")
            val deleteId = contentResolver.delete(uri, "id=?", arrayOf("3"))
            Log.d(TAG, "deleteId:$deleteId")
        }

        addMenu("update") {
            val uri = Uri.parse("$BASE_URI")
            val count = contentResolver.update(uri, ContentValues().apply {
                put("name", "中文")
            }, "id=?", arrayOf("4") )
            Log.d(TAG,"updateCount:$count")
        }

        addMenu("Query All") {
            val uri = Uri.parse("$BASE_URI")
            val cursor = contentResolver.query(uri, arrayOf("id", "name", "age", "phone", "address"), null, null, null)
            val datas = cursor?.toList {
                mutableMapOf<String, Any>().apply {
                    this["id"] = it.getInt(0)
                    this["name"] = it.getString(1)
                    this["age"] = it.getInt(2)
                    this["phone"] = it.getString(3)
                    this["address"] = it.getString(4)
                }
            }
            Log.d(TAG, "queryData:$datas")
        }

        addMenu("Query One") {
            val uri = Uri.parse("$BASE_URI/1")
            val cursor = contentResolver.query(uri, arrayOf("id", "name"), null, null, null)
            val datas = cursor?.toList {
                mutableMapOf<String, Any>().apply {
                    this["id"] = it.getInt(0)
                    this["name"] = it.getString(1)
                }
            }
            Log.d(TAG, "queryOneData:$datas")
        }


    }
}