
// Name : Poojanbhai N Patel
// Student ID : 1001827807
// Reference: https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/?ref=rp

package com.company;

// Import all the packages
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

// this is Main class
public class Main {
    static public Vector<ClientHandler> ar = new Vector<>();
    static public HashMap<String, DataOutputStream> clientWriter = new HashMap<>();
    static public ArrayList<String> clientListStatus = new ArrayList<>();
    static public ArrayList<String> storeLogData = new ArrayList<>();
    static public int numberOfClient = 0;
    static String whoIsThis;
    static int i = 0;

    // This is the entry point of server
    public static void main(String[] args) throws IOException {

        // setting the value to UI
        ServerView.ServerViewSetArray(storeLogData);
        ServerView.main2();

        // run server on the separate thread
        Thread serverHit = new Thread(new Runnable() {
            @Override
            public void run() {
                // adding the log
                storeLogData.add("Starting server");
                System.out.println("Starting server");
                int portFixCount = 4444;
                int portCounter = 6565;
                ServerSocket ss = null;
                try {
                    ss = new ServerSocket(portFixCount);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Socket s;

                //this loop will be iterating continuously
                while (true) {
                    try {
                        // accept the client request
                        s = ss.accept();
                        System.out.println("New client request received : " + s);

                        // obtain input and output streams
                        DataInputStream dis = new DataInputStream(s.getInputStream());
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                        System.out.println("Creating a new handler for this client...");

                        // Create a new handler object for handling this request.
                        ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos, portCounter);
                        portCounter++;

                        // Create a new Thread with this object.
                        Thread t = new Thread(mtch);

                        System.out.println("Adding this client to active client list");

                        // add this client to active client list
                        ar.add(mtch);

                        // starting the thread.
                        t.start();

                        // increment i for new client.
                        i++;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // start the server
        serverHit.start();
    }
}

