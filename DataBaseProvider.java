import java.lang.reflect.InvocationTargetException;
import java.sql.*;

abstract class DataBaseProvider {

    private final static String CREATE_TABLE_RECIPIES =  "CREATE TABLE recipes (" +
            "   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "   name VARCHAR(20) UNIQUE," +
            "   caloriesPer100Grams DOUBLE," +
            "   gramsPerPiece DOUBLE," +
            "   mainIngredient VARCHAR(20)," +
            "   CONSTRAINT primaryKey PRIMARY KEY(id, name)" +
            "   )";

    private Connection connection;
    protected Statement statement;

    public DataBaseProvider() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").getDeclaredConstructor().newInstance();
        connectToDataBase("jdbc:derby:dbName; create=true");
    }

    private void connectToDataBase(String url) {
        try {
            connection = DriverManager.getConnection(url);
            createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createStatement() throws SQLException {
        statement = connection.createStatement();
    }

    abstract void createTable();

    protected void dropTable(String tableName) {
        try {
            statement.execute("DROP TABLE " + tableName);
        } catch (SQLException e) {
            System.out.println("Can't delete the table");
        }
    }

    protected void dropColumn(String tableName, String columnName){
        try{
            statement.execute("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
        } catch (SQLException e) {
            System.out.println("Can't delete column " + columnName + " form " + tableName + ": " + e.getMessage());
        }
    }

    protected void deleteRows(String tableName, String condition){
        try{
            statement.execute("DELETE FROM " + tableName + " WHERE " + condition);
        } catch (SQLException e){
            System.out.println("Can't delete rows from " + tableName);
        }
    }

    abstract void selectAllFromTable();
    abstract void printAllFromTable(ResultSet resultSet) throws SQLException;
    abstract String findInTable(String itemToFind);

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

