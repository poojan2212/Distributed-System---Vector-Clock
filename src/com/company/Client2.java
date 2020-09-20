
// Name : Poojanbhai N Patel
// Student ID : 1001827807
//Reference : https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/?ref=rp
//Client UI reference : https://www.callicoder.com/javafx-registration-form-gui-tutorial/
//Another UI reference of client UI : https://www.geeksforgeeks.org/javafx-combobox-with-examples/
//Reference : https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadLocalRandom.html

package com.company;
// import packages
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

// client class handling the UI and sending data to server
public class Client2 extends Application {
    final static int ServerPort = 4444;

    // getting localhost IP
    static InetAddress ip = null;
    static DataInputStream dis = null;
    static Socket s = null;
    static DataOutputStream dos = null;
    static Socket sNew = null;
    static DataInputStream disNew = null;
    static DataOutputStream dosNew = null;
    static boolean updateStatus = false;
    static boolean goOnForClock = false;
    static HashMap<String, Integer> hv = new HashMap<>();
    static String myName="";
    static ArrayList<String> vectorList = new ArrayList<String>();

    // main method of the class
    public static void main(String[] args) throws IOException {
        // initialzing the vector clock
        hv.put("A", 0);
        hv.put("B", 0);
        hv.put("C", 0);
        Thread serverHit = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scn = new Scanner(System.in);

                try {
                    ip = InetAddress.getByName("localhost");
                    // establish the connection
                    s = new Socket(ip, ServerPort);

                    // obtaining input and out streams
                    dis = new DataInputStream(s.getInputStream());
                    dos = new DataOutputStream(s.getOutputStream());
                    String msg = dis.readUTF();
                    String[] msgSplit = msg.split("#");
                    int newPort = Integer.parseInt(msgSplit[0]);
                    // establish the connection
                    sNew = new Socket(ip, newPort);

                    // obtaining input and out streams
                    disNew = new DataInputStream(sNew.getInputStream());
                    dosNew = new DataOutputStream(sNew.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // send Message thread
                DataOutputStream finalDosNew = dosNew;
                Thread sendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        /*try {
                            finalDosNew.writeUTF("A#username");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        while (true) {
                            // read the message to deliver
//                            if (goOnForClock) {
                            String[] array = new String[2];
                            try {
                                int clockA = hv.get("A");
                                int clockB = hv.get("B");
                                int clockC = hv.get("C");
                                if (myName.equals("A")) {
                                    clockA++;
                                    hv.put("A", clockA);
                                    array[0] = "B";
                                    array[1] = "C";
                                } else if (myName.equals("B")) {
                                    clockB++;
                                    hv.put("B", clockB);
                                    array[0] = "A";
                                    array[1] = "C";
                                } else {
                                    clockC++;
                                    hv.put("C", clockC);
                                    array[0] = "A";
                                    array[1] = "B";
                                }
                                // write on the output stream
                                String temp = array[(ThreadLocalRandom.current().nextInt(0, 2))];
                                finalDosNew.writeUTF(hv.get("A") + ":" + hv.get("B") + ":" + hv.get("C") + ":" + temp + ":" +myName+ "#clock");
                                vectorList.add(hv.toString() + "sending to id: " + temp);
                                System.out.println(hv.toString() + "sending to id: " + temp);
                                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 10000));
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
//                            }
                        }
                    }
                });


                // readMessage thread
                DataInputStream finalDisNew = disNew;
                Thread readMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (true) {
                            try {
                                // read the message sent to this client
                                String msg = finalDisNew.readUTF();
                                String[] msgSplit = msg.split("#");
                                if (msgSplit[1].equals("error")) {
                                    updateStatus = true;
                                }
                                if (msgSplit[1].equals("clock")){
                                    String[] msgSplitForClock = msg.split(":");
                                    switch (myName){
                                        case "A":
                                            int updateA = Integer.parseInt(msgSplitForClock[0]);
                                            updateA++;
                                            hv.put("A", updateA);
                                            if (hv.get("B") < Integer.parseInt(msgSplitForClock[1])){
                                                hv.put("B", Integer.parseInt(msgSplitForClock[1]));
                                            }
                                            if (hv.get("C") < Integer.parseInt(msgSplitForClock[2])){
                                                hv.put("C", Integer.parseInt(msgSplitForClock[2]));
                                            }
                                            break;
                                        case "B":
                                            int updateB = Integer.parseInt(msgSplitForClock[1]);
                                            updateB++;
                                            hv.put("B", updateB);
                                            if (hv.get("A") < Integer.parseInt(msgSplitForClock[0])){
                                                hv.put("A", Integer.parseInt(msgSplitForClock[0]));
                                            }
                                            if (hv.get("C") < Integer.parseInt(msgSplitForClock[2])){
                                                hv.put("C", Integer.parseInt(msgSplitForClock[2]));
                                            }
                                            break;
                                        case "C":
                                            int updateC = Integer.parseInt(msgSplitForClock[2]);
                                            updateC++;
                                            hv.put("C", updateC);
                                            if (hv.get("A") < Integer.parseInt(msgSplitForClock[0])){
                                                hv.put("A", Integer.parseInt(msgSplitForClock[0]));
                                            }
                                            if (hv.get("B") < Integer.parseInt(msgSplitForClock[1])){
                                                hv.put("B", Integer.parseInt(msgSplitForClock[1]));
                                            }
                                            break;
                                        default:
                                    }
                                }
                                System.out.println(msg + ":::::::::::" + hv);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                sendMessage.start();
                readMessage.start();
            }
        });
        serverHit.start();
        launch(args);
    }

    // Launch the application
    public void start(Stage stage) {
        // Set title for the stage
        stage.setTitle("creating combo box ");

        // Create a tile pane
        TilePane r = new TilePane();

        // Create a label
        Label description_label =
                new Label("This is a combo box example ");

        // Weekdays
        String[] clients_name =
                {"A", "B", "C"};

        // Create a combo box
        ComboBox combo_box =
                new ComboBox(FXCollections
                        .observableArrayList(clients_name));

        // Label to display the selected menuitem
        Label selected = new Label("default item selected");

        // Create action event
        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        selected.setText(combo_box.getValue() + " selected");
                    }
                };

        // Set on action
        combo_box.setOnAction(event);

        Button b = new Button("button");

        // Create a tile pane
        TilePane tile_pane = new TilePane(combo_box, selected);

        GridPane gridPane = new GridPane();

        gridPane.add(tile_pane, 0, 4, 2, 1);
        GridPane.setHalignment(tile_pane, HPos.CENTER);
        GridPane.setMargin(tile_pane, new Insets(60, 0, 20, 0));

        Button submitButton = new Button("Submit");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        gridPane.add(submitButton, 0, 4, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(180, 0, 20, 0));

        //button handle event

        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println(dos);
                    dosNew.writeUTF(combo_box.getValue() + "#client");
                    System.out.println(combo_box.getValue());
                    Thread.sleep(1000);
                    if (updateStatus) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Please select other");
                        alert.setHeaderText(null);
                        alert.setContentText("This is been there");
                        alert.show();
                    } else {
                        goOnForClock = true;
                        myName = (String) combo_box.getValue();
                        // Create a scene
                        GridPane gg = new GridPane();
                        // Create the ListView for the fruits
                        ListView<String> fruits = new ListView<String>();
                        ListView<String> list = new ListView<String>();
                        list.getItems().add("A" + hv);
                        gg.add(list, 0, 4, 2, 1);
                        GridPane.setHalignment(list, HPos.CENTER);
                        GridPane.setMargin(list, new Insets(180, 0, 20, 0));

                        Button submitButton = new Button("Fetch data");
                        submitButton.setPrefHeight(40);
                        submitButton.setDefaultButton(true);
                        submitButton.setPrefWidth(100);
                        gg.add(submitButton, 0, 4, 2, 1);
                        GridPane.setHalignment(submitButton, HPos.RIGHT);
                        GridPane.setMargin(submitButton, new Insets(180, 0, 20, 0));

                        submitButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                // Create the ListView for the fruits
                                ListView<String> fruits = new ListView<String>();
                                ListView<String> list = new ListView<String>();
                                list.getItems().add("B" + hv);
                                gg.add(list, 0, 4, 2, 1);
                                GridPane.setHalignment(list, HPos.RIGHT);
                                GridPane.setMargin(list, new Insets(180, 0, 20, 0));
                                Button submitButton = new Button("Fetch data");
                                submitButton.setPrefHeight(40);
                                submitButton.setDefaultButton(true);
                                submitButton.setPrefWidth(100);
                                gg.add(submitButton, 0, 4, 2, 1);
                                GridPane.setHalignment(submitButton, HPos.RIGHT);
                                GridPane.setMargin(submitButton, new Insets(180, 0, 20, 0));

                                submitButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        // Create the ListView for the fruits
                                        ListView<String> fruits = new ListView<String>();
                                        ListView<String> list = new ListView<String>();
                                        list.getItems().add("B" + hv);
                                        gg.add(list, 0, 4, 2, 1);
                                        GridPane.setHalignment(list, HPos.RIGHT);
                                        GridPane.setMargin(list, new Insets(180, 0, 20, 0));

                                        Button submitButton = new Button("Fetch data");
                                        submitButton.setPrefHeight(40);
                                        submitButton.setDefaultButton(true);
                                        submitButton.setPrefWidth(100);
                                        gg.add(submitButton, 0, 4, 2, 1);
                                        GridPane.setHalignment(submitButton, HPos.RIGHT);
                                        GridPane.setMargin(submitButton, new Insets(180, 0, 20, 0));

                                        submitButton.setOnAction(new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent event) {
                                                // Create the ListView for the fruits
                                                ListView<String> fruits = new ListView<String>();
                                                ListView<String> list = new ListView<String>();
                                                list.getItems().add(hv.toString());
                                                gg.add(list, 0, 4, 2, 1);
                                                GridPane.setHalignment(list, HPos.RIGHT);
                                                GridPane.setMargin(list, new Insets(180, 0, 20, 0));

                                                Button submitButton = new Button("Fetch data");
                                                submitButton.setPrefHeight(40);
                                                submitButton.setDefaultButton(true);
                                                submitButton.setPrefWidth(100);
                                                gg.add(submitButton, 0, 4, 2, 1);
                                                GridPane.setHalignment(submitButton, HPos.RIGHT);
                                                GridPane.setMargin(submitButton, new Insets(180, 0, 20, 0));

                                                submitButton.setOnAction(new EventHandler<ActionEvent>() {
                                                    @Override
                                                    public void handle(ActionEvent event) {
                                                        // Create the ListView for the fruits
                                                        ListView<String> fruits = new ListView<String>();
                                                        ListView<String> list = new ListView<String>();
                                                        list.getItems().add("B" + hv);
                                                        gg.add(list, 0, 4, 2, 1);
                                                        GridPane.setHalignment(list, HPos.RIGHT);
                                                        GridPane.setMargin(list, new Insets(180, 0, 20, 0));
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });


                        Scene scene2 = new Scene(gg, 500, 500);

                        // Set the scene
                        stage.setScene(scene2);

                        stage.show();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        // Create a scene
        Scene scene = new Scene(gridPane, 500, 500);

        // Set the scene
        stage.setScene(scene);

        stage.show();
    }

}