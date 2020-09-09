// IRequestBooksCallback.aidl
package com.example.test.ipc;


import com.example.test.ipc.Book;
// Declare any non-default types here with import statements

interface IRequestBooksCallback {

    // 这里要设置成
   void onSuccess(in List<Book> books);

//   void onSuccess2(
}
