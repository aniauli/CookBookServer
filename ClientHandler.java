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
    private DataBaseProviderForRecipes dataBaseProviderForRecipes;
    private DataBaseProviderForProductsInRecipes dataBaseProviderForProductsInRecipes;

    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.dataBaseProviderForProducts = new DataBaseProviderForProducts();
        this.dataBaseProviderForRecipes = new DataBaseProviderForRecipes();
        this.dataBaseProviderForProductsInRecipes = new DataBaseProviderForProductsInRecipes();
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
                        toSend = itemCheckedIfNotNull(product);
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Add product":
                        received = dataInputStream.readUTF();
                        if (checkIfProduct(received)) {
                            Product receivedProduct = createProduct(received);
                            dataOutputStream.writeUTF(resultOfAddingProductToDataBase(receivedProduct));
                        } else {
                            dataOutputStream.writeUTF("Błąd! Nie udało się dodać produktu. " +
                                    "Sprawdź, czy poprawnie uzupełniłeś wszystkie dane.");
                        }
                        break;

                    case "Show all products":
                        dataOutputStream.writeUTF(dataBaseProviderForProducts.selectAllNamesFromTable
                                ("products"));
                        break;

                    case "Find recipe":
                        received = dataInputStream.readUTF();
                        String recipeWithProducts = recipeWithProducts(received);
                        toSend = itemCheckedIfNotNull(recipeWithProducts);
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Show breakfast":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesFromTableInCondition
                                ("recipes", "śniadanie"));
                        break;

                    case "Show lunch":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesFromTableInCondition
                                ("recipes", "lunch"));
                        break;

                    case "Show dinner":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesFromTableInCondition
                                ("recipes", "obiad"));
                        break;

                    case "Show supper":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesFromTableInCondition
                                ("recipes", "kolacja"));
                        break;

                    case "Show secondBreakfast":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesFromTableInCondition
                                ("recipes", "drugie śniadanie"));
                        break;

                    default:
                        dataOutputStream.writeUTF("ble");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
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

    private String itemCheckedIfNotNull(String item) {
        String toSend;
        if (item.equals("null")) {
            toSend = "There's no such item";
        } else {
            toSend = item;
        }
        return toSend;
    }

    private boolean checkIfProduct(String product) {
        String[] splittedProduct = splitIntoPieces(product);
        if (splittedProduct.length == 4) {
            return (checkIfIsDouble(splittedProduct[1]) && checkIfIsDouble(splittedProduct[2]));
        }
        System.out.println("Nie rozdzielilo");
        return false;
    }

    private String[] splitIntoPieces(String product) {
        return product.split(";");
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
        return new Product(splittedProduct[0], Double.parseDouble(splittedProduct[1]),
                Double.parseDouble(splittedProduct[2]), splittedProduct[3]);
    }

    private String resultOfAddingProductToDataBase(Product receivedProduct) throws IOException {
        if (isThereSuchProductInTable(receivedProduct)) {
            return "Podany produkt już jest w bazie danych";
        }
        if (dataBaseProviderForProducts.insertIntoTable(receivedProduct)) {
            return "Pomyślnie dodano produkt do bazy";
        }
        return "Bład serwera! Nie udało się dodać produktu do bazy.";
    }

    private String recipeWithProducts(String received) {
        String recipe = dataBaseProviderForRecipes.findInTable(received);
        Integer idRecipe = dataBaseProviderForRecipes.findIdInTable(received);
        String products = dataBaseProviderForProductsInRecipes.findProductsWithGramsInRecipe(idRecipe, "products.name");
        String grams = dataBaseProviderForProductsInRecipes.findProductsWithGramsInRecipe(idRecipe, "productsInRecipes.productInGrams");
        String calories = dataBaseProviderForProductsInRecipes.findProductsWithGramsInRecipe(idRecipe, "products.caloriesPer100Grams");
        return recipe + "!" + products + "!" + grams + "!" + calories;
    }

    private boolean isThereSuchProductInTable(Product receivedProduct) {
        return dataBaseProviderForProducts.checkIfTheNameOfItemExistsInTable
                (receivedProduct.getName(), "products");
    }
}
