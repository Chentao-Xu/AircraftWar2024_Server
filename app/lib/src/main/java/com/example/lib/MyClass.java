package com.example.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;

public class MyClass {
    public static void main(String[] args) throws InterruptedException {
        new MyClass();
    }

    private boolean isWaiting = false;
    private boolean isStart = false;

    public MyClass() throws InterruptedException {

        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("--Listener Port: 9999--");

            while(true){
                Socket client1 = serverSocket.accept();
                Socket client2 = serverSocket.accept();
                new ServerSocketThread(client1, client2).start();
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

}