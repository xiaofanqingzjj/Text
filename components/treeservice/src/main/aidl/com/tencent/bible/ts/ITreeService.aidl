// ITreeService.aidl
package com.tencent.bible.ts;

// Declare any non-default types here with import statements

interface ITreeService {

   IBinder getLeafServiceBinder(String serviceName);
   void sayHello();
}
