import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseProviderForProductsInRecipes extends DataBaseProvider {

    public final static String CREATE_TABLE_PRODUCTSINRECIPES = "CREATE TABLE productsInRecipes (" +
            "   idRecipe INTEGER," +
            "   idProduct INTEGER," +
            "   productInGrams DOUBLE," +
            "   CONSTRAINT productsInRecipiesPrimaryKey PRIMARY KEY(idRecipe, idProduct) " +
            "   )";


    public DataBaseProviderForProductsInRecipes() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
    }

    @Override
    String findInTable(String itemToFind) {
        return null;
    }


    public boolean insertIntoTable(Integer idRecipe, Integer idProduct, Double productInGrams) {
        try {
            statement.execute("INSERT INTO productsInRecipes (idRecipe, idProduct, productInGrams)" + "VALUES (" +
                    idRecipe + ", " + idProduct + ", " + productInGrams + ")");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
            return false;
        }
    }

    public String selectAllNamesFromTable(String tableName){
        try {
            String query = "SELECT idRecipe, idProduct, productInGrams FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(query);
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                result.append(String.format("%s;%s;%s", resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3)));
            }
            return result.toString();
        } catch (SQLException ex) {
            System.out.println("Can't return all names from table: " + ex.getMessage());
            return "null";
        }
    }

    public String findProductsWithGramsInRecipe(Integer idRecipe, String findWhat){
        try {
            String query = "SELECT " + findWhat +" FROM products " +
                    "JOIN productsInRecipes ON products.id = " +
                    "productsInRecipes.idProduct WHERE productsInRecipes.idRecipe = " + idRecipe + "ORDER BY products.name";
            ResultSet resultSet = statement.executeQuery(query);
            StringBuilder result = new StringBuilder();
            while(resultSet.next()) {
                result.append(String.format("%s;", resultSet.getString(1)));
            }
            return result.toString();
        } catch (SQLException e) {
            System.out.println("Can't find products from table productsInRecipes: " + e.getMessage());
            return "SQL Error";
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DataBaseProviderForProductsInRecipes dataBaseProviderForProductsInRecipes = new DataBaseProviderForProductsInRecipes();
        dataBaseProviderForProductsInRecipes.insertIntoTable(3, 33, 20.0);
        System.out.println(dataBaseProviderForProductsInRecipes.selectAllNamesFromTable("productsInRecipes"));
        dataBaseProviderForProductsInRecipes.shutDownDataBase();
    }
}