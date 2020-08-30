import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{

    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Socket socket;

    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream)
    {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    @Override
    public void run()
    {
        String received;
        String tosend;

        while (true)
        {
            try {
                received = dataInputStream.readUTF();

                if(received.equals("Exit"))
                {
                    System.out.println("Client " + this.socket.getLocalAddress() + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }

                switch (received) {
                    case "Produkt" :
                        tosend = "Wyszukuje produkt...";
                        dataOutputStream.writeUTF(tosend);
                        break;

                    case "Time" :
                        System.out.println("ble");
                        break;

                    default:
                        dataOutputStream.writeUTF("ble");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error in while: " + e.getMessage());
                if(e.getMessage().equals("Connection reset")){
                    break;
                }
            }
        }

        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();

        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
