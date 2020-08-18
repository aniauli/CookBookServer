import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("The server is running ... ");

        ServerSocket serverSocket = new ServerSocket(8080);

        while(true){
            Socket clientSocket = serverSocket.accept();
            Executor executor = Executors.newCachedThreadPool();
            Runnable runnable = () -> {
                System.out.println("New connection from: " + clientSocket);
                 try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
                    if(bufferedReader.readLine().equals("hello!")){
                        System.out.println("Sb joined us...");
                    }
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        send(clientSocket, "This is my answer for " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            executor.execute(runnable);
        }
    }

    private static void send(Socket clientSocket, String answer) throws IOException {
        clientSocket.getOutputStream().write(answer.getBytes());
    }
}

