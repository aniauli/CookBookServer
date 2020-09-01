import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseProviderForProducts extends DataBaseProvider{

    private final static String CREATE_TABLE_PRODUCTS =  "CREATE TABLE products (" +
            "   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "   name VARCHAR(50) UNIQUE," +
            "   caloriesPer100Grams DOUBLE," +
            "   gramsPerServing DOUBLE," +
            "   mainIngredient VARCHAR(20)," +
            "   CONSTRAINT primaryKey PRIMARY KEY(id, name)" +
            "   )";

    public DataBaseProviderForProducts() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, 
            InvocationTargetException, InstantiationException {
    }

    @Override
    void createTable() {
        try {
            statement.execute(CREATE_TABLE_PRODUCTS);
        } catch (SQLException ex) {
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    void selectAllFromTable() {
        try {
            String query = "SELECT id, name, caloriesPer100Grams, gramsPerServing, " +
                    "mainIngredient FROM products";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                printAllFromTable(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Can't view all products from table Products: " + e.getMessage());
        }
    }

    @Override
    void printAllFromTable(ResultSet resultSet) throws SQLException {
        System.out.printf("%-5d \t %-50s \t %-3.2f \t %-3.2f \t %-20s \n",
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getDouble(3),
                resultSet.getDouble(4),
                resultSet.getString(5)
        );
    }

    @Override
    String findInTable(String itemToFind) {
        try {
            String query = "SELECT id, name, caloriesPer100Grams, gramsPerServing, " +
                    "mainIngredient FROM products WHERE name LIKE '" + itemToFind + "'";
            ResultSet resultSet = statement.executeQuery(query);
            String result = "null";
            if(resultSet.next()) {
                result = String.format("%s;%s;%s;%s", resultSet.getString(2),
                        resultSet.getDouble(3), resultSet.getDouble(4),
                        resultSet.getDouble(5));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Can't find products from table Products: " + e.getMessage());
            return "SQL Error";
        }
    }

    public void insertIntoTable(Product product) {
        try {
            statement.execute("INSERT INTO products(name, caloriesPer100Grams, gramsPerServing," +
                    " mainIngredient)" + "VALUES ('" + product.getName() + "', " +
                    product.getCaloriesPer100Grams() + ", " +
                    product.getGramsPerServing() + ", " +
                    product.getMainIngredient() + "')");
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
        }
    }
}
