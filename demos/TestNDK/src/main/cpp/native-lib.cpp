#include <jni.h>
#include <string>


using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_testndk_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */, jstring param) {


    string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_testndk_MainActivity_add(JNIEnv *env, jobject thiz, jint a, jint b) {
    jint c = a + b;
    return c;

}