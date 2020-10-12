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

    public String findProductsInRecipe(Integer idRecipe) {
        return findInRecipe(idRecipe, "products");
    }

    public String findGramsInRecipe(Integer idRecipe) {
        return findInRecipe(idRecipe, "grams");
    }

    public String findCaloriesInRecipe(Integer idRecipe) {
        return findInRecipe(idRecipe, "calories");
    }

    public String findInRecipe(Integer idRecipe, String findWhat) {
        try {
            CallableStatement callableStatement = null;
            String SQL = null;

            if(findWhat.equals("products")) {
                SQL = "{call findProductsInRecipe (?)}";
            } else if(findWhat.equals("grams")) {
                SQL = "{call findGramsInRecipe (?)}";
            } else {
                SQL = "{call findCaloriesInRecipe (?)}";
            }

            callableStatement = connection.prepareCall(SQL);
            callableStatement.setInt(1, idRecipe);

            ResultSet resultSet = callableStatement.executeQuery();
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                result.append(String.format("%s;", resultSet.getString(1)));
            }
            return result.toString();
        } catch (SQLException e) {
            System.out.println("Can't find " + findWhat + " from table productsInRecipes: " + e.getMessage());
            return "SQL Error";
        }
    }
}