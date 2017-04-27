#include <jni.h>
#include <stdio.h>
#include "interfaces_can_Can.h"
#include "CanImpl.h"
 
JNIEXPORT jstring JNICALL Java_interfaces_can_Can_read(JNIEnv *env, jobject thisObj) {
    return can_read(env);
}

JNIEXPORT int JNICALL Java_interfaces_can_Can_open_1port(JNIEnv *env, jobject thisObj, jstring javaString) {
    const char *port = (*env)->GetStringUTFChars(env, javaString, 0);
    int i = can_open_port(port);
    (*env)->ReleaseStringUTFChars(env, javaString, port);
    return i;
}

JNIEXPORT void JNICALL Java_interfaces_can_Can_send_1port(JNIEnv *env, jobject thisObj) {
    can_send_port();
    return;
}

JNIEXPORT jstring JNICALL Java_interfaces_can_Can_read_1port(JNIEnv *env, jobject thisObj) {
    
    return can_read_port(env);;
}

JNIEXPORT jint JNICALL Java_interfaces_can_Can_close_1port(JNIEnv *env, jobject thisObj) {
    return can_close_port();
}

JNIEXPORT jint JNICALL Java_interfaces_can_Can_init(JNIEnv *env, jobject thisObj) {
   return can_init();
}