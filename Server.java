import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server
{
    public static void main(String[] args) throws IOException, NoSuchMethodException, InstantiationException,
            SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("The server is running... ");

        DataBaseInitialization dataBaseInitialization = new DataBaseInitialization();
        dataBaseInitialization.setUpConnection();


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

                Thread t = new ClientHandler(socket, dataInputStream, dataOutputStream, dataBaseInitialization.getConnection());

                t.start();

            }
            catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
}


