#include <jni.h>

#ifndef _CAN_CPP_IMPL_H
#define _CAN_CPP_IMPL_H
 
#ifdef __cplusplus
        extern "C" {
#endif
        jstring can_read(JNIEnv *env);
        int can_open_port(const char *port);
        void can_send_port();
        jstring can_read_port(JNIEnv *env);
        int can_close_port();
        int can_init();


#ifdef __cplusplus
        }
#endif
 
#endif