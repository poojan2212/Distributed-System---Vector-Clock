
// Name : Poojanbhai N Patel
// Student ID : 1001827807
// Reference : https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/?ref=rp

package com.company;

// import packages
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static com.company.Main.storeLogData;

// this is clienthandler class
public class ClientHandler implements Runnable {
    final DataInputStream dis;
    final DataOutputStream dos;
    Scanner scn = new Scanner(System.in);
    Socket s;
    boolean isloggedin;
    int portNumber;
    private String name;

    // constructor of the class
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos, int portNumber) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        try {
            dos.writeUTF(portNumber + "#port");
            HandleSeparately h = new HandleSeparately(portNumber);
            h.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
// this will take new client
class HandleSeparately extends Thread {
    int portNumber;

    HandleSeparately(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        try {
            ServerSocket sssSocket = new ServerSocket(portNumber);
            Socket sstSocket;

            // running infinite loop for getting
            // client request
            sstSocket = sssSocket.accept();
            // obtain input and output streams
            DataInputStream dis = new DataInputStream(sstSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(sstSocket.getOutputStream());

            HandleSeparatelyWrite hsw = new HandleSeparatelyWrite(dos, portNumber);
            hsw.start();

            HandleSeparatelyRead hsr = new HandleSeparatelyRead(dis, dos, portNumber);
            hsr.start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
// new thread to accept the client request
class HandleSeparatelyRead extends Thread {
    DataInputStream dis;
    DataOutputStream dos;

    HandleSeparatelyRead(DataInputStream dis, DataOutputStream dos, int portNumber) {
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        while (true) {
                try {
                    // read the message sent to this client
                    String msg = dis.readUTF();
                    System.out.println(msg);
                    String[] msgSplit = msg.split("#");
                    Main.whoIsThis = msgSplit[0];
                    if (msgSplit[1].equals("vector")) {
                        System.out.println(msg);
                    }
                    System.out.println(Main.clientWriter);
                    if (msgSplit[1].equals("client")) {
                        System.out.println(msgSplit[0]);
                        boolean isThere = false;
                        for (String s :
                                Main.clientListStatus) {
                            System.out.println(s);
                            if (s.equals(msgSplit[0])){
                                isThere = true;
                            }
                        }
                        if (!isThere){
                            Main.clientListStatus.add(msgSplit[0]);
                            Main.clientWriter.put(msgSplit[0], dos);
                            Main.numberOfClient++;
                            storeLogData.add(msgSplit[0] + " Is connected.");
                        }
                        else {
                            dos.writeUTF("Sorry client there#error");
                        }
                    }
                    // this is clock logic
                    if (msgSplit[1].equals("clock") && ( Main.numberOfClient == 3 || Main.numberOfClient > 2)){
                        Thread.sleep(2000);
                        System.out.println(msgSplit[0]);
                        String[] msgClock = msgSplit[0].split(":");
                        System.out.println(msgClock[0]);
                        System.out.println(msgClock[msgClock.length-2]);
                        Main.clientWriter.get(msgClock[msgClock.length-2]).writeUTF(msgSplit[0] + "#clock");
                        storeLogData.add("Sending to id: " + msgClock[msgClock.length-2] + " by id: " + msgClock[msgClock.length-1] + "data: " + msgSplit[0]);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
        }

        }
    }

class HandleSeparatelyWrite extends Thread {
    DataOutputStream dos;
    int portNumber;

    HandleSeparatelyWrite(DataOutputStream dos, int portNumber) {
        this.dos = dos;
        this.portNumber = portNumber;
    }

    @Override
    public void run() {

    }
}