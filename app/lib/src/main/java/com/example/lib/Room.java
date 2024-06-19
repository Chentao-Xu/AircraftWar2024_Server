package com.example.lib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Room {

    private ArrayList<Socket> sockets;
    private int id;
    private boolean isStart;

    public Room(int id, Socket host){
        this.id = id;
        this.sockets = new ArrayList<>();
        this.sockets.add(host);
        isStart = false;
    }

    public void addPlayer(Socket socket){
        sockets.add(socket);
    }

    public int getPlayerNum(){
        return sockets.size();
    }

    public int getId(){
        return id;
    }

    public void start(){
        isStart = true;
    }

    public boolean IsStart() {
        return isStart;
    }

    public ArrayList<Socket> getSockets() {
        return sockets;
    }

}
