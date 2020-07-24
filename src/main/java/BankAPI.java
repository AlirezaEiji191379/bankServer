import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class BankAPI {


    public static void main(String[] args) {
        try {
            Socket socket=new Socket("localHost",8080);
            DataInputStream dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dataOutputStream=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            String sent="";
            String received="";
            Scanner scanner=new Scanner(System.in);
            while (true){
                sent=scanner.nextLine();
                dataOutputStream.writeUTF(sent);
                dataOutputStream.flush();
                received=dataInputStream.readUTF();
                System.out.println(received);
                if(sent.equals("exit")){
                    dataInputStream.close();
                    dataOutputStream.close();
                    socket.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
