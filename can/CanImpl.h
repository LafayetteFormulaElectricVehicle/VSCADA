#ifndef _CAN_CPP_IMPL_H
#define _CAN_CPP_IMPL_H
 
#ifdef __cplusplus
        extern "C" {
#endif
        void read();
        void open_port();
        void send_port();
        void read_port();
        int close_port();
        int init();


#ifdef __cplusplus
        }
#endif
 
#endif