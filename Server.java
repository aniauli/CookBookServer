import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("The server is running ... ");

        ServerSocket serverSocket = new ServerSocket(8080);

        while(true){
            Socket clientSocket = serverSocket.accept();
            Runnable runnable = () -> {
                try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
                    if(bufferedReader.readLine().equals("hello!")){
                        System.out.println("Sb joined us...");
                    }
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        //TODO: cos odpowiedz.
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
        }

    }
}

