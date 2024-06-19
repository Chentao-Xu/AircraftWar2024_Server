package com.example.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyClass {
    private static int roomId = 1000;
    private List<Room> rooms = new ArrayList<>();
    public static void main(String[] args) throws InterruptedException {
        new MyClass();
    }

    public MyClass() throws InterruptedException {

        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("--Listener Port: 9999--");

            while(true){
                new CreatePlayerThread(serverSocket.accept()).start();
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private class CreatePlayerThread extends Thread{

        private Socket socket;
        private BufferedReader in;
        private PrintWriter pw;
        private Room room;
        private boolean isHost;
        private boolean findRoom;

        public CreatePlayerThread(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            this.pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            String content;
            try {
                content = in.readLine();
                if(content.equals("host")){
                    room = new Room(++roomId, socket);
                    rooms.add(room);
                    pw.println(room.getId());
                    findRoom = true;
                    isHost = true;
                }else{
                    int roomId = Integer.parseInt(content);
                    findRoom = false;
                    isHost = false;
                    for(Room r : rooms){
                        if(roomId==r.getId()){
                            room = r;
                            r.addPlayer(socket);
                            pw.println("find");
                            findRoom = true;
                            break;
                        }
                    }
                    if(!findRoom) {
                        pw.println("notfind");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run(){

            if(findRoom) {

                new Thread(() -> {
                    while (!room.IsStart()) {
                        pw.println(room.getPlayerNum());
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

                if(isHost) {

                    new Thread(() -> {
                        try {
                            String content;
                            while((content = in.readLine()) != null){
                                if(content.equals("start")){
                                    room.start();
                                    //创建游戏线程
                                    new GameThread(room.getSockets()).start();
                                    rooms.remove(room);
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();

                }

            }
        }


    }

}