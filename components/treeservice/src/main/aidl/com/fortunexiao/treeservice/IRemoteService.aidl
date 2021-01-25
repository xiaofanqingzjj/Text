// IRemoteService.aidl
package com.fortunexiao.treeservice;

// Declare any non-default types here with import statements

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    IBinder getRemoteService(String name);

    // 心跳
    void sayHello(String hello);
}
