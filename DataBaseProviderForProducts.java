import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseProviderForProducts extends DataBaseProvider{

    public DataBaseProviderForProducts(Connection connection) throws SQLException {
        super(connection);
    }

    String showItemInfo(String itemToFind) {
        try {
            String query = "SELECT id_product, name, caloriesPer100Grams, gramsPerServing, " +
                    "mainIngredient FROM products WHERE name LIKE '" + itemToFind + "'";
            ResultSet resultSet = statement.executeQuery(query);
            String result = "null";
            if(resultSet.next()) {
                result = String.format("%s;%s;%s;%s", resultSet.getString(2),
                        resultSet.getDouble(3), resultSet.getDouble(4),
                        resultSet.getString(5));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Can't find products from table products: " + e.getMessage());
            return "SQL Error";
        }
    }

    public Integer findProductId(String productName){
        try {
            String query = "SELECT id_product FROM products WHERE name LIKE '" + productName + "'";
            ResultSet resultSet = statement.executeQuery(query);
            Integer result = 0;
            if(resultSet.next()) {
                result = Integer.parseInt(resultSet.getString(1));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Can't find products from table recipes: " + e.getMessage());
            return 0;
        }
    }

    public boolean addProduct(Product product) {
        try {
            statement.execute("INSERT INTO products(name, caloriesPer100Grams, gramsPerServing," +
                    " mainIngredient)" + "VALUES ('" + product.getName() + "', " +
                    product.getCaloriesPer100Grams() + ", " +
                    product.getGramsPerServing() + ", '" +
                    product.getMainIngredient() + "')");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
            return false;
        }
    }

}
