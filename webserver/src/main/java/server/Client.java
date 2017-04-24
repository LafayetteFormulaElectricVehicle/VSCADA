package server;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;


public class Client {

    private String SERVER;// = "127.0.0.1";  // must change on starting
    private int SOCKET_PORT;// = 3002;
    private String IV = "WARRIORROAD12345";

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Client.class.getName());

    private FileInputStream fis;// = null;
    private BufferedInputStream bis;// = null;
    private OutputStream os;// = null;
    private Socket sock;// = null;

    public Client() {
        this.fis = null;
        this.bis = null;
        this.os = null;
        this.sock = null;
    }

    public boolean connect(String server, int port) {
        try{
            this.sock = new Socket();
            LOG.info("Connecting to " + SERVER + ":" + SOCKET_PORT + "...");
            this.sock.connect(new InetSocketAddress(SERVER, SOCKET_PORT),10000);
            LOG.info("Connected");
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void send(String data) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream(), "UTF8"));
            out.append(data).append("\n");
            out.flush();
            LOG.info("Data sent");
        }catch (Exception ioe){
            LOG.error("Could not send data");
            ioe.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (os != null) os.close();
                if (sock != null) sock.close();
            } catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }


//    public byte[] encrypt(String plainText, String encryptionKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES", "SunJCE");
//        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
//        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
//        return cipher.doFinal(plainText.getBytes("UTF-8"));
//    }
//
//    public String decrypt(byte[] cipherText, String encryptionKey) throws Exception{
//        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
//        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
//        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
//        return new String(cipher.doFinal(cipherText),"UTF-8");
//    }

}