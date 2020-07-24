import Controller.RequestProcessor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket=new ServerSocket(8080);
            Socket socket=null;
            while (true){
                socket=serverSocket.accept();
                DataOutputStream dataOutputStream=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                DataInputStream dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                new ClientHandler(socket,dataOutputStream,dataInputStream).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


static class ClientHandler extends Thread{
   DataInputStream dataInputStream;
   DataOutputStream dataOutputStream;
   Socket socket;
   public ClientHandler(Socket socket,DataOutputStream dataOutputStream,DataInputStream dataInputStream){
       this.socket=socket;
       this.dataInputStream=dataInputStream;
       this.dataOutputStream=dataOutputStream;
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
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }
}




}
