package com.example.test.ipc

import android.os.Parcel
import android.os.Parcelable


data class Book(var bookId: Long = 0, var name: String? = null) : Parcelable {





    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()) {
    }

    fun readFromParcel(parcel: Parcel) {
        bookId = parcel.readLong()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(bookId)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}