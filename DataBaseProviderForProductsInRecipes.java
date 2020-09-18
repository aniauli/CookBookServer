import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseProviderForProductsInRecipes extends DataBaseProvider {

    public DataBaseProviderForProductsInRecipes(Connection connection) throws SQLException {
        super(connection);
    }

    public boolean addProductInRecipe(Integer idRecipe, Integer idProduct, Double productInGrams) {
        try {
            statement.execute("INSERT INTO productsInRecipes (id_recipe, id_product, productInGrams)" + "VALUES (" +
                    idRecipe + ", " + idProduct + ", " + productInGrams + ")");
            System.out.println("Dodano do productsInRecipes");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
            return false;
        }
    }

    public String selectAllNamesFromTable(String tableName) {
        try {
            String query = "SELECT id_recipe, id_product, productInGrams FROM " + tableName;
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

    public String findProductsWithGramsInRecipe(Integer idRecipe, String findWhat) {
        try {
            String query = "SELECT " + findWhat + " FROM products " +
                    "JOIN productsInRecipes ON products.id_product = " +
                    "productsInRecipes.id_product WHERE productsInRecipes.id_recipe = " + idRecipe + " ORDER BY products.name";
            ResultSet resultSet = statement.executeQuery(query);
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                result.append(String.format("%s;", resultSet.getString(1)));
            }
            return result.toString();
        } catch (SQLException e) {
            System.out.println("Can't find products from table productsInRecipes: " + e.getMessage());
            return "SQL Error";
        }
    }
}