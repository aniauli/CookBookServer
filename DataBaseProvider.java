import java.lang.reflect.InvocationTargetException;
import java.sql.*;

abstract class DataBaseProvider {

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

    protected void createTable(String createTableStatement) {
        try {
            statement.execute(createTableStatement);
        } catch (SQLException ex) {
            System.out.println("Can't create the table: " + ex.getMessage());
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw new RuntimeException(ex);
            }
        }
    }

    protected void dropTable(String tableName) {
        try {
            statement.execute("DROP TABLE " + tableName);
        } catch (SQLException e) {
            System.out.println("Can't delete the table: " + e.getMessage());
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

    protected boolean checkIfTheNameOfItemExistsInTable(String itemName, String tableName){
        try {
            String query = "SELECT name FROM " + tableName + " WHERE name LIKE '" + itemName + "'";
            ResultSet resultSet = statement.executeQuery(query);
            String result = "null";
            if(resultSet.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Can't check if the product exists in table: " + tableName + " -> " + e.getMessage());
            return false;
        }
    }

    public void addColumn(String tableName, String columnName, String dataType) {
        try {
            statement.execute("ALTER TABLE " + tableName + " ADD " + columnName + " " + dataType);
        } catch (SQLException e) {
            System.out.println("Can't add column " + columnName + " to table " + tableName + " : " + e.getMessage());
        }
    }

    public void updateTable(String tableName, String afterSet){
        try {
            statement.execute("UPDATE " + tableName + " SET " + afterSet );
        } catch (SQLException e) {
            System.out.println("Can't update " + tableName + " : " + e.getMessage());
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

