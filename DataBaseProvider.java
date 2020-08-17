import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class DataBaseProvider {
    private final static String CREATE_TABLE_QUERY =  "CREATE TABLE products (" +
            "   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "   name VARCHAR(20) UNIQUE," +
            "   caloriesPer100Grams DOUBLE," +
            "   gramsPerPiece DOUBLE," +
            "   mainIngredient VARCHAR(20)," +
            "   CONSTRAINT primaryKey PRIMARY KEY(id, name)" +
            "   )";

    private Connection connection;
    private Statement statement;

    public DataBaseProvider() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").getDeclaredConstructor().newInstance();
        try {
            connection = DriverManager.getConnection("jdbc:derby:dbName; create=true");
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createDataBase()  {
        try {
            statement.execute(CREATE_TABLE_QUERY);
        } catch (SQLException e) {
        }
    }

    public void insertProduct(Product product) {
        try {
            statement.execute("INSERT INTO products(name, caloriesPer100Grams, gramsPerPiece, mainIngredient)" +
                    " VALUES ('" + product.getName() + "', "  + product.getCaloriesPer100Gram() + ", 0, 'nic')");
        } catch (SQLException e) {
            //throw new RuntimeException(e);
        }
    }

    public void selectProduct() {
        try {
            String query = "SELECT id, name, caloriesPer100Grams, gramsPerPiece, mainIngredient FROM products";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                System.out.printf("%-5d \t %-20s \t %-3.3f \t %-3.3f \t %-20s \n",
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getString(5)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void shutDownDataBase(){
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if(!"XJ015".equals(e.getSQLState())){
                throw new RuntimeException();
            }
        }
    }
}

