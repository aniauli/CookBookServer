import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

public class ClientHandler extends Thread {

    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Connection connection;

    private DataBaseProviderForProducts dataBaseProviderForProducts;
    private DataBaseProviderForRecipes dataBaseProviderForRecipes;
    private DataBaseProviderForProductsInRecipes dataBaseProviderForProductsInRecipes;
    private DataBaseProviderForUsers dataBaseProviderForUsers;
    private DataBaseProviderForUsersRecipes dataBaseProviderForUsersRecipes;

    ClientHandler(DataInputStream dataInputStream, DataOutputStream dataOutputStream, Connection connection) throws SQLException {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.connection = connection;

        dataBaseProviderForProducts = new DataBaseProviderForProducts(this.connection);
        dataBaseProviderForRecipes = new DataBaseProviderForRecipes(this.connection);
        dataBaseProviderForProductsInRecipes = new DataBaseProviderForProductsInRecipes(this.connection);
        dataBaseProviderForUsers = new DataBaseProviderForUsers(this.connection);
        dataBaseProviderForUsersRecipes = new DataBaseProviderForUsersRecipes(this.connection);
    }

    @Override
    public void run() {
        String received;
        String toSend;

        while (true) {
            try {
                received = dataInputStream.readUTF();

                switch (received) {
                    case "Check if username is occupied":
                        received = dataInputStream.readUTF();
                        if(dataBaseProviderForUsers.checkIfUsernameIsOccupied(received)){
                            toSend = "Nick jest zajęty";
                        } else{
                            toSend = "Nick jest wolny";
                        }
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Check if password is correct":
                        String username = dataInputStream.readUTF();
                        String password = dataInputStream.readUTF();
                        if(password.equals(dataBaseProviderForUsers.checkIfPasswordIsCorrect(username))){
                            toSend = "Poprawne hasło";
                        } else {
                            toSend = "Niepoprawne hasło";
                        }
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Current user":
                        received = dataInputStream.readUTF();
                        dataBaseProviderForUsersRecipes.setCurrentUser(new User(received, dataBaseProviderForUsers.findPassword(received)));
                        break;
                        
                    case "Add user":
                        received = dataInputStream.readUTF();
                        dataOutputStream.writeUTF(resultOfAddingUserToDataBase(createUser(received)));
                        break;

                    case "Find product":
                        received = dataInputStream.readUTF();
                        String product = dataBaseProviderForProducts.showItemInfo(received);
                        toSend = itemCheckedIfNotNull(product);
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Find if product exists":
                        received = dataInputStream.readUTF();
                        if(dataBaseProviderForProducts.checkIfTheNameOfItemExistsInTable(received)){
                            dataOutputStream.writeUTF("exists");
                        } else {
                            dataOutputStream.writeUTF("not exists");
                        }
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

                    case "Find recipe":
                        received = dataInputStream.readUTF();
                        String recipeWithProducts = recipeWithProducts(received);
                        toSend = itemCheckedIfNotNull(recipeWithProducts);
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Add recipe with info":
                        received = dataInputStream.readUTF();
                        if(addRecipesToTables(received)){
                            toSend = "Pomyślnie dodano przepis do bazy";
                        }
                        else{
                            toSend = "Błąd! Nie udało się dodać produktu.";
                        }
                        dataOutputStream.writeUTF(toSend);
                        break;

                    case "Show breakfast":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesInMealGroup("śniadanie"));
                        break;

                    case "Show lunch":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesInMealGroup("lunch"));
                        break;

                    case "Show dinner":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesInMealGroup("obiad"));
                        break;

                    case "Show supper":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesInMealGroup("kolacja"));
                        break;

                    case "Show secondBreakfast":
                        dataOutputStream.writeUTF(dataBaseProviderForRecipes.selectAllNamesInMealGroup("drugie śniadanie"));
                        break;

                    default:
                        dataOutputStream.writeUTF("Błąd serwera!");
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
        String[] splittedProduct = product.split(";");
        if(splittedProduct.length == 4) {
            return (checkIfIsDouble(splittedProduct[1]) && checkIfIsDouble(splittedProduct[2]));
        }
        return false;
    }

    private boolean checkIfIsDouble(String text) {
        try {
            Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.out.println("Exception in checkIfIsDouble method -> To nie double!: " + text);
            return false;
        }
        return true;
    }

    private Product createProduct(String received) {
        String[] splittedProduct = received.split(";");
        return new Product(splittedProduct[0], Double.parseDouble(splittedProduct[1]),
                Double.parseDouble(splittedProduct[2]), splittedProduct[3]);
    }

    private String resultOfAddingProductToDataBase(Product receivedProduct) {
        if (isThereSuchProductInTable(receivedProduct)) {
            return "Podany produkt już jest w bazie danych";
        }
        if (dataBaseProviderForProducts.addProduct(receivedProduct)) {
            return "Pomyślnie dodano produkt do bazy";
        }
        return "Bład serwera! Nie udało się dodać produktu do bazy.";
    }

    private User createUser(String received) {
        String[] splittedReceived = received.split(";");
        return new User(splittedReceived[0], splittedReceived[1]);
    }

    private String resultOfAddingUserToDataBase(User user) {
        if(dataBaseProviderForUsers.addUser(user)){
            return "Pomyślnie dodano użytkownika do bazy";
        }
        return "Błąd serwera! Nie udało się dodać użytkownika do bazy.";
    }

    private boolean isThereSuchProductInTable(Product receivedProduct) {
        return dataBaseProviderForProducts.checkIfTheNameOfItemExistsInTable(receivedProduct.getName());
    }

    private String recipeWithProducts(String received) {
        String recipe = dataBaseProviderForRecipes.showItemInfo(received);
        if(recipe.equals("null")){
            return "null";
        }
        Integer idRecipe = dataBaseProviderForRecipes.findRecipeId(received);
        String products = dataBaseProviderForProductsInRecipes.findProductsInRecipe(idRecipe);
        String grams = dataBaseProviderForProductsInRecipes.findGramsInRecipe(idRecipe);
        String calories = dataBaseProviderForProductsInRecipes.findCaloriesInRecipe(idRecipe);
        return recipe + "!" + products + "!" + grams + "!" + calories;
    }

    private boolean addRecipesToTables(String received) {
        String[] receivedSplitted = received.split("!");
        String recipeName = receivedSplitted[0];
        String[] ingredientsWithGrams = receivedSplitted[1].split(";");
        String instructions = receivedSplitted[2];
        String meal = receivedSplitted[3];

        User user = dataBaseProviderForUsersRecipes.getCurrentUser();
        Recipe recipe = new Recipe(recipeName, instructions, meal);

        if(!dataBaseProviderForRecipes.addRecipe(recipe)){
            return false;
        }

        Integer id_recipe = dataBaseProviderForRecipes.findRecipeId(recipe.getName());
        Integer id_user = dataBaseProviderForUsers.findUserId(user.getUsername());

        if(!dataBaseProviderForUsersRecipes.addUserRecipe(id_user, id_recipe)){
            return false;
        }

        String[] ingredientsWithGramsSplitted;
        Integer id_product;

        for (String ingredientsWithGram : ingredientsWithGrams) {
            ingredientsWithGramsSplitted = ingredientsWithGram.split("-");
            id_product = dataBaseProviderForProducts.findProductId(ingredientsWithGramsSplitted[0]);
            if(!dataBaseProviderForProductsInRecipes.addProductInRecipe(id_recipe, id_product, Double.parseDouble(ingredientsWithGramsSplitted[1]))){
                return false;
            }
        }

        return true;
    }
}
