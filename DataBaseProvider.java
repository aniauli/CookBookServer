import java.sql.*;

class DataBaseProvider {

    final Connection connection;
    protected Statement statement;

    DataBaseProvider(Connection connection) throws SQLException {
        this.connection = connection;
        statement = connection.createStatement();
    }

    protected boolean checkIfTheNameOfItemExistsInTable(String itemName){
        try {
            String query = "SELECT name FROM products WHERE name LIKE '" + itemName + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Can't check if the product exists in table: products" + " -> " + e.getMessage());
            return false;
        }
    }

}

