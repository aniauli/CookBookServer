import java.sql.*;

public class DataBaseProviderForProductsInRecipes extends DataBaseProvider {

    public DataBaseProviderForProductsInRecipes(Connection connection) throws SQLException {
        super(connection);
    }

    public boolean addProductInRecipe(Integer idRecipe, Integer idProduct, Double productInGrams) {
        try {
            CallableStatement callableStatement = null;
            String SQL = "{call addProductInRecipe (?, ?, ?)}";

            callableStatement = connection.prepareCall(SQL);
            callableStatement.setInt(1, idRecipe);
            callableStatement.setInt(2, idProduct);
            callableStatement.setDouble(3, productInGrams);

            callableStatement.execute();

            System.out.println("Dodano do productsInRecipes");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
            return false;
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