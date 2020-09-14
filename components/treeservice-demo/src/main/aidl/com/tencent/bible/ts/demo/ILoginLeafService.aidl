// ILoginLeafService.aidl
package com.tencent.bible.ts.demo;

// Declare any non-default types here with import statements

interface ILoginLeafService {


    String getAccessToken();
    long getUserId();
    int getUserType();


}
