package com.mygdx.game;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class MainClient {

    private LinkedBlockingQueue<String> sendqueue;
    private String ip;
    public String hostMap;
    public String moveInput = "";
    public String eInput = "";

    public MainClient(String ip) {
        this.sendqueue = new LinkedBlockingQueue<String>();
        this.ip = ip;
    }

    public void run() {
        Scanner input = new Scanner(System.in);
        try {
            Socket connector = new Socket(); // Makes a new socket object
            connector.connect(new InetSocketAddress(ip, 3000), 5000); // Connecting to the server
            System.out.println("Connected");
            PrintWriter out = new PrintWriter(connector.getOutputStream(), true); // Creates a writer to decode the inbound data
            BufferedReader in = new BufferedReader(new InputStreamReader(connector.getInputStream())); // Create a buffer reader to write outbound data
            Thread reader = new Thread(() -> {
                
                try {
                	
                    hostMap = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (connector.isConnected()) {
                    try{
                    	String i = in.readLine();
                    	if(i.split(" ")[0].equals("p")) {
                    		moveInput = i.substring(1);
                    	} else if(i.split(" ")[0].equals("e")) {
                    		eInput = i.substring(1);
                    	}

                    } catch (Exception e) {
                    }
                }
            });

            reader.start();

            Thread writer = new Thread(() -> {
                while (connector.isConnected()) {
                    try{
                        out.write(this.sendqueue.take());
                        out.flush();
                    } catch (Exception e) {
                    }
                }
            });
            connector.close();
            writer.start();

        } catch (IOException e){
            e.printStackTrace();
        }
        input.close();
    }

    public void send(String str) {
        try {
            sendqueue.put(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}