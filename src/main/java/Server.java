import Controller.RequestProcessor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

    public static void main(String[] args) {
        try {
            int id=999;
            ServerSocket serverSocket=new ServerSocket(8080);
            Socket socket=null;
            while (true){
                socket=serverSocket.accept();
                id++;
                System.out.println("client with id "+id+" connected!");
                DataOutputStream dataOutputStream=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                DataInputStream dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                new ClientHandler(socket,dataOutputStream,dataInputStream,id).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


static class ClientHandler extends Thread{
   int clientId=0;
   DataInputStream dataInputStream;
   DataOutputStream dataOutputStream;
   Socket socket;
   public ClientHandler(Socket socket,DataOutputStream dataOutputStream,DataInputStream dataInputStream , int clientId){
       this.socket=socket;
       this.dataInputStream=dataInputStream;
       this.dataOutputStream=dataOutputStream;
       this.clientId=clientId;
   }
    @Override
    public void run() {
       String received="";
       String toBeSend="";
       while (true) {
           try {
                   received = dataInputStream.readUTF();
                   System.out.println("from client: " + received);
                   toBeSend = RequestProcessor.getInstance().process(received);
                   dataOutputStream.writeUTF(toBeSend);
                   dataOutputStream.flush();
                   System.out.println("from server: " + toBeSend);
               if(received.equals("exit")){
                   dataOutputStream.close();
                   dataInputStream.close();
                   socket.close();
                   return;
               }
           } catch (SocketException socketException){
               System.out.println("client with id "+clientId+" disconnected!");
               return;
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }
}




}
