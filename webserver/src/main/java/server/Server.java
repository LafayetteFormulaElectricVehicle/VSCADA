package server;//import org.apache.commons.codec.binary.Base64;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server implements Runnable{

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Server.class.getName());

    public Server() {
        Thread t = new Thread(this,"server.Server");
        t.start();

    }

    public void run() {
        try {
            int cTosPortNumber = 3002;
            String str;
            ServerSocket servSocket = null;
            Socket sock = null;
            PrintWriter pw = null;
            BufferedReader br = null;
            try {
                servSocket = new ServerSocket(cTosPortNumber);

                while (true) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        LOG.info("Waiting for a connection on " + cTosPortNumber);
                        sock = servSocket.accept();
                        LOG.info("Connection from: "+sock.getRemoteSocketAddress());
                        pw = new PrintWriter(sock.getOutputStream(), true);

                        br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                        while ((str = br.readLine()) != null) {
                            //str = decrypt(key, initVector, str);
                            sb.append(str).append("\n");
                            LOG.info("The message: " + str);
                        }
                        String output = sb.toString();
                        String timestamp = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss.SSS zzz").format(new Date());
                        String remoteIP = sock.getRemoteSocketAddress().toString().replace("/", "");

                        //LOG.info(remoteIP + "\t" + timestamp + "\n\t\t\t\t\t\tReceived: " + output);
                    } finally {
                        if (pw != null) pw.close();
                        if (br != null) br.close();
                        if (sock != null) sock.close();
                    }
                }
            } finally {
                if (servSocket != null) servSocket.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

//    public static String decrypt(String key, String initVector, String encrypted) {
//        try {
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//
//            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
//
//            return new String(original);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return null;
//    }

    public static boolean writeFile(String file, String data) {
        try{
            Files.write(Paths.get(file), data.getBytes());
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}