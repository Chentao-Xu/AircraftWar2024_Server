package com.example.lib;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class GameThread extends Thread{
    private int playerNum;
    private ArrayList<Socket> sockets;
    private ArrayList<BufferedReader> ins;
    private ArrayList<PrintWriter> pws;
    private ArrayList<Integer> scores;
    private ArrayList<Boolean> isEnds;
    public GameThread(ArrayList<Socket> sockets){
        this.sockets = sockets;
        this.playerNum = sockets.size();
        ins = new ArrayList<>();
        pws = new ArrayList<>();
        scores = new ArrayList<>();
        isEnds = new ArrayList<>();
        try {
            for(Socket socket : sockets) {
                ins.add(new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8")));
                pws.add(new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true));
                scores.add(0);
                isEnds.add(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(){

        //为每个客户端发送开始标记
        for(PrintWriter pw : pws) {
            pw.println("start");
        }

        //接收客户端信息
        for(int i = 0; i < playerNum ; i++){
            int finalI = i;
            new Thread(()->{
                try {
                    String content;
                    while((content = ins.get(finalI).readLine()) != null){
                        if(content.equals("end")){
                            isEnds.set(finalI, true);
                            break;
                        }else {
                            scores.set(finalI, Integer.parseInt(content));
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        //为客户端发送信息
        new Thread(()->{
            boolean end = false;

            while (!end) {
                end = true;
                Integer maxScore = Collections.max(scores);
                for(int i=0 ; i < playerNum ; i++){
                        pws.get(i).println(maxScore);
                    if (!isEnds.get(i)){
                        end = false;
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            for(int i=0 ; i < playerNum ; i++){
                pws.get(i).println("end");
            }
        }).start();

    }

}
