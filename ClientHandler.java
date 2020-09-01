import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class ClientHandler extends Thread{

    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Socket socket;
    private DataBaseProviderForProducts dataBaseProviderForProducts;

    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.dataBaseProviderForProducts = new DataBaseProviderForProducts();
    }

    @Override
    public void run()
    {
        String received;
        String toSend;

        while (true)
        {
            try {
                received = dataInputStream.readUTF();

                switch (received) {
                    case "Find product" :
                        received = dataInputStream.readUTF();
                        String product = dataBaseProviderForProducts.findInTable(received);
                        toSend = productCheckedIfNotNull(product);
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Add product" :
                        dataOutputStream.writeUTF("Chleb;450;30;60;Węglowodany");
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
            dataBaseProviderForProducts.shutDownDataBase();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private String productCheckedIfNotNull(String product) {
        String toSend;
        if(product.equals("null")) {
            toSend = "There's no such product";
        } else {
            toSend = product;
        }
        return toSend;
    }
}
