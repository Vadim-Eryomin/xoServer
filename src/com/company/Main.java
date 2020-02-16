package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    static ArrayList<Game> games =  new ArrayList<>();
    static char[] fields = {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'};
    public static void main(String[] args) throws IOException {
	    ServerSocket serverSocket = new ServerSocket(55555);
	    createServer(serverSocket);
    }
    public static void createServer(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    read(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    createServer(serverSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void read(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        while (true){
            String read = dis.readUTF();
            String[] data = read.split(" ");
            if (data[0].equals("/cg")){
                games.add(new Game());
                games.get(games.size()-1).host = socket;
                new DataOutputStream(socket.getOutputStream()).writeUTF(true+"");
            }
            if (data[0].equals("/connect")){
                for (int i = 0; i < games.size(); i++) {
                    if (games.get(i).client == null){
                        games.get(i).client = socket;
                        new DataOutputStream(socket.getOutputStream()).writeUTF(true+"");
                    }
                }
            }
            if (data[0].equals("/click")){
                int num = Integer.parseInt(data[1]);
                Game host = findGameByHost(socket);
                Game client = findGameByClient(socket);
                fields[num] = host != null ? 'x' : client != null ? 'o' : 'n';
                String write = "";
                for (int i = 0; i < fields.length; i++) {
                    write += fields[i];
                }
                new DataOutputStream(socket.getOutputStream()).writeUTF(write);
            }
        }
    }
    public static Game findGameByHost(Socket socket){
        Game find;
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).host.equals(socket)) return games.get(i);
        }
        return null;
    }
    public static Game findGameByClient(Socket socket){
        Game find;
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).client.equals(socket)) return games.get(i);
        }
        return null;
    }
}
