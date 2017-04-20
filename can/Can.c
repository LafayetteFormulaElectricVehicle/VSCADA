#include <jni.h>
#include <stdio.h>
#include "Can.h"
//#include "SocketCan.h"
#include "CanImpl.h"
 
// Implementation of native method sayHello() of HelloJNI class
JNIEXPORT void JNICALL Java_Can_read(JNIEnv *env, jobject thisObj) {
   read();
   return;
}

JNIEXPORT void JNICALL Java_Can_open_1port(JNIEnv *env, jobject thisObj) {
   open_port();
   return;
}

JNIEXPORT void JNICALL Java_Can_send_1port(JNIEnv *env, jobject thisObj) {
   send_port();
   return;
}

JNIEXPORT void JNICALL Java_Can_read_1port(JNIEnv *env, jobject thisObj) {
   read_port();
   return;
}

JNIEXPORT jint JNICALL Java_Can_close_1port(JNIEnv *env, jobject thisObj) {
   return close_port();
}

JNIEXPORT jint JNICALL Java_Can_init(JNIEnv *env, jobject thisObj) {
   return init();
}