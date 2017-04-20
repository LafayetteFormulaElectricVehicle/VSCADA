#include <jni.h>
#include <stdio.h>
#include "Can.h"
#include "CanImpl.h"
 
JNIEXPORT void JNICALL Java_Can_read(JNIEnv *env, jobject thisObj) {
   
   can_read();
   
   return;
}

JNIEXPORT int JNICALL Java_Can_open_1port(JNIEnv *env, jobject thisObj, jstring javaString) {
    const char *port = (*env)->GetStringUTFChars(env, javaString, 0);
   int i = can_open_port(port);
   (*env)->ReleaseStringUTFChars(env, javaString, port);
   return i;
}

JNIEXPORT void JNICALL Java_Can_send_1port(JNIEnv *env, jobject thisObj) {
   can_send_port();
   return;
}

JNIEXPORT void JNICALL Java_Can_read_1port(JNIEnv *env, jobject thisObj) {
   can_read_port();
   return;
}

JNIEXPORT jint JNICALL Java_Can_close_1port(JNIEnv *env, jobject thisObj) {
   return can_close_port();
}

JNIEXPORT jint JNICALL Java_Can_init(JNIEnv *env, jobject thisObj) {
   return can_init();
}