package com.example.lib;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Handler;

public class ServerSocketThread extends Thread{
    private BufferedReader in1;
    private BufferedReader in2;
    private PrintWriter pw1;
    private PrintWriter pw2;
    private Socket socket1;
    private Socket socket2;
    private int score1;
    private int score2;
    private boolean isEnd1 = false;
    private boolean isEnd2 = false;
    public ServerSocketThread(Socket socket1, Socket socket2){
        this.socket1 = socket1;
        this.socket2 = socket2;
    }

    @Override
    public void run(){
        try {
            in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream(),"UTF-8"));
            in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream(),"UTF-8"));
            pw1 = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket1.getOutputStream(), "UTF-8")), true);
            pw2 = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket2.getOutputStream(), "UTF-8")), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pw1.println("start");
        pw2.println("start");

        new Thread(()->{
            try {
                String content;
                while((content = in1.readLine()) != null){
                    if(content.equals("end")){
                        isEnd1 = true;
                        break;
                    }else {
                        this.score1 = Integer.parseInt(content);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(()->{
            try {
                String content;
                while((content = in2.readLine()) != null){
                    if(content.equals("end")){
                        isEnd2 = true;
                        break;
                    }else {
                        this.score2 = Integer.parseInt(content);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(()->{
            while (!isEnd1 || !isEnd2) {
                if (!isEnd1){
                    pw2.println(score1);
                }
                if (!isEnd2) {
                    pw1.println(score2);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            pw1.println("end");
            pw2.println("end");
        }).start();

    }

}
