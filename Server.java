import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("The server is running... ");


        while (true)
        {
            Socket socket = null;

            try
            {
                socket = serverSocket.accept();

                System.out.println("New connection from: " + socket.getLocalAddress());

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                System.out.println("Assigning new thread for client " + socket.getLocalAddress());

                Thread t = new ClientHandler(socket, dataInputStream, dataOutputStream);

                t.start();

            }
            catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
}


