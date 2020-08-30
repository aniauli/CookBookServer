import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class DataBaseProvider {
    private final static String CREATE_TABLE_PRODUCTS =  "CREATE TABLE products (" +
            "   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "   name VARCHAR(50) UNIQUE," +
            "   caloriesPer100Grams DOUBLE," +
            "   gramsPerServing DOUBLE," +
            "   caloriesPerServing DOUBLE, " +
            "   mainIngredient VARCHAR(20)," +
            "   CONSTRAINT primaryKey PRIMARY KEY(id, name)" +
            "   )";

    private final static String CREATE_TABLE_RECIPIES =  "CREATE TABLE recipes (" +
            "   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "   name VARCHAR(20) UNIQUE," +
            "   caloriesPer100Grams DOUBLE," +
            "   gramsPerPiece DOUBLE," +
            "   mainIngredient VARCHAR(20)," +
            "   CONSTRAINT primaryKey PRIMARY KEY(id, name)" +
            "   )";

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

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

    public void createTableProducts()  {
        try {
            statement.execute(CREATE_TABLE_PRODUCTS);
        } catch (SQLException ex) {
            if(! "X0Y32".equals(ex.getSQLState())){
                throw new RuntimeException(ex);
            }
        }
    }

    public void dropTableProducts() {
        try {
            statement.execute("DROP TABLE products");
        } catch (SQLException e) {
            System.out.println("Can't delete the table");
        }
    }

    public void dropColumn(String tableName, String columnName){
        try{
            statement.execute("ALTER TABLE " + tableName + " DROP COULUMN " + columnName);
        } catch (SQLException e) {
            System.out.println("Can't delete column " + columnName + " form " + tableName);
        }
    }

    public void deleteRows(String tableName, String condition){
        try{
            statement.execute("DELETE FROM " + tableName + " WHERE " + condition);
        } catch (SQLException e){
            System.out.println("Can't delete rows from " + tableName);
        }
    }

    public void insertIntoTableProduct(Product product) {
        try {
            statement.execute("INSERT INTO products(name, caloriesPer100Grams, gramsPerServing, caloriesPerServing, mainIngredient)" +
                    " VALUES ('" + product.getName() + "', " +
                    product.getCaloriesPer100Grams() + ", " +
                    product.getGramsPerServing() + ", " +
                    ((product.getCaloriesPer100Grams() * product.getGramsPerServing())/100.0) + ", '" +
                    product.getMainIngredient() + "')");
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
        }
    }

    public void selectAllFromTableProduct() {
        try {
            String query = "SELECT id, name, caloriesPer100Grams, gramsPerServing, caloriesPerServing, mainIngredient FROM products";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                System.out.printf("%-5d \t %-50s \t %-3.2f \t %-3.2f \t %-3.2f \t %-20s \n",
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getDouble(5),
                        resultSet.getString(6)
                );
            }
        } catch (SQLException e) {
            System.out.println("Can't view all products from table Products");
        }
    }

    public void changeTheColumnLength(String tableName, String columnName, Integer length){
        try {
            statement.executeUpdate("ALTER TABLE " + tableName + " ALTER " + columnName + " SET DATA TYPE VARCHAR(" +length + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void selectAllFromRecipe() {
        System.out.println("dupa");
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

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DataBaseProvider dataBaseProvider = new DataBaseProvider();
        Product product = new Product("Margaryna Smakowita z masłem", 360.0, 5.0, "Tłuszcze");
        dataBaseProvider.insertIntoTableProduct(product);
        dataBaseProvider.selectAllFromTableProduct();
        dataBaseProvider.shutDownDataBase();
    }
}

