// IRemoteServiceInterface.aidl
package com.example.test.ipc;

import com.example.test.ipc.Book;
import com.example.test.ipc.IRequestBooksCallback;

// Declare any non-default types here with import statements

interface IRemoteServiceInterface {

    // 正常的普通方法
    int add(int a, int b);

    // 方法传递了一个自定义的类
    void setBookName(out Book book, in Book book2, inout Book book3,  String name);

    // 一个异步请求方法，
    void requestAllBooks(IRequestBooksCallback callback);


}
