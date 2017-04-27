#include "CanImpl.h"
#include <iostream>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <linux/can.h>
#include <linux/can/raw.h>
#include <unistd.h>

using namespace std;

int soc;
int read_can_port;

jstring can_read(JNIEnv *env) {
    std::string message = "HELLOfdfsd";
    return env->NewStringUTF(message.c_str());
}

int can_open_port(const char *port) {
    struct ifreq ifr;
    struct sockaddr_can addr;

    /* open socket */
    soc = socket(PF_CAN, SOCK_RAW, CAN_RAW);
    if(soc < 0)
    {
        cout << "Socket invalid";
        return (-1);
    }

    addr.can_family = AF_CAN;
    strcpy(ifr.ifr_name, port);

    if (ioctl(soc, SIOCGIFINDEX, &ifr) < 0)
    {
        cout << "No idea what the error is\n";
        return (-1);
    }

    addr.can_ifindex = ifr.ifr_ifindex;

    fcntl(soc, F_SETFL, O_NONBLOCK);

    if (bind(soc, (struct sockaddr *)&addr, sizeof(addr)) < 0)
    {

        return (-1);
    }

    cout << "Open port: " << port << "\n";
    return 0;
}

void can_send_port() {
    cout << "Send port\n";
    return;
}

jstring can_read_port(JNIEnv *env) {
    struct can_frame frame_rd;
    int recvbytes = 0;
    string str = "";
    read_can_port = 1;
    while(read_can_port)
    {
        struct timeval timeout = {1, 0};
        fd_set readSet;
        FD_ZERO(&readSet);
        FD_SET(soc, &readSet);
        str = "";
        if (select((soc + 1), &readSet, NULL, NULL, &timeout) >= 0)
        {
            if (!read_can_port)
            {
                break;
            }
            if (FD_ISSET(soc, &readSet))
            {
                recvbytes = read(soc, &frame_rd, sizeof(struct can_frame));
                if(recvbytes)
                {
		char buffer [200];
		//string str = "";
		for(int i = 0; i < frame_rd.can_dlc; i++) {
			char* tmp = new char[200];
			sprintf(tmp,"%s %.2X",str.c_str(),frame_rd.data[i]);
			str = tmp;
			delete[] tmp;
			
			//sprintf(buffer, "%.2X ", buffer, frame_rd.data[i]);
		}
		            //printf("C: can0\t%X\t[%d]\t%s\n", frame_rd.can_id, frame_rd.can_dlc, str.c_str());
                    char* tmp = new char[200];
                    sprintf(tmp, "can0\t%X\t[%d]\t%s", frame_rd.can_id, frame_rd.can_dlc, str.c_str());
                    str = tmp;
                    delete[] tmp;
                    return env->NewStringUTF(str.c_str());
                }
            }
        }

    }
    cout << "Read port\n";
    return env->NewStringUTF(str.c_str());
}

int can_close_port() {
    close(soc);
    cout << "Close port\n";
    return 0;
}

int can_init() {
    cout << "Init\n";
    return 0;
}
