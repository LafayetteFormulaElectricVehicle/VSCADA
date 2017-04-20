#ifndef _CAN_CPP_IMPL_H
#define _CAN_CPP_IMPL_H
 
#ifdef __cplusplus
        extern "C" {
#endif
        void can_read();
        int can_open_port(const char *port);
        void can_send_port();
        void can_read_port();
        int can_close_port();
        int can_init();


#ifdef __cplusplus
        }
#endif
 
#endif