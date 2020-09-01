import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class ClientHandler extends Thread {

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
    public void run() {
        String received;
        String toSend;

        while (true) {
            try {
                received = dataInputStream.readUTF();

                switch (received) {
                    case "Find product":
                        received = dataInputStream.readUTF();
                        String product = dataBaseProviderForProducts.findInTable(received);
                        toSend = productCheckedIfNotNull(product);
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Add product":
                        received = dataInputStream.readUTF();
                        if (checkIfProduct(received)) {
                            Product receivedProduct = createProduct(received);
                            if(dataBaseProviderForProducts.insertIntoTable(receivedProduct)) {
                                dataOutputStream.writeUTF("Product added");
                            }
                        } else {
                            dataOutputStream.writeUTF("Cannot add product");
                        }
                        break;

                    default:
                        dataOutputStream.writeUTF("ble");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error in while: " + e.getMessage());
                if (e.getMessage().equals("Connection reset")) {
                    break;
                }
            }
        }
        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();
            dataBaseProviderForProducts.shutDownDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String productCheckedIfNotNull(String product) {
        String toSend;
        if (product.equals("null")) {
            toSend = "There's no such product";
        } else {
            toSend = product;
        }
        return toSend;
    }

    private boolean checkIfProduct(String product) {
        String[] splittedProduct = splitIntoPieces(product);
        if(splittedProduct.length == 4) {
            return (checkIfIsDouble(splittedProduct[1]) && checkIfIsDouble(splittedProduct[2]));
        }
        System.out.println("Nie rozdzielilo");
        return false;
    }

    private String[] splitIntoPieces(String product) {
        String[] splittedProduct = product.split(";");
        return splittedProduct;
    }

    private boolean checkIfIsDouble(String text) {
        try {
            double checkedText = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.out.println("To nie double!: " + text);
            return false;
        }
        return true;
    }

    private Product createProduct(String received) {
        String[] splittedProduct = splitIntoPieces(received);
        Product receivedProduct = new Product(splittedProduct[0], Double.parseDouble(splittedProduct[1]),
                Double.parseDouble(splittedProduct[2]), splittedProduct[3]);
        return receivedProduct;
    }
}
