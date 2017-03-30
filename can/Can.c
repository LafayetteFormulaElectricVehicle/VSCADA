#include <jni.h>
#include <stdio.h>
#include "Can.h"
#include "SocketCan.h"
 
// Implementation of native method sayHello() of HelloJNI class
JNIEXPORT void JNICALL Java_Can_read(JNIEnv *env, jobject thisObj) {
   printf("Hello Worldasdf!\n");
   return;
}