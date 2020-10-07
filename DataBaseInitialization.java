import com.google.common.io.Resources;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class DataBaseInitialization {

    private static final String URL = "jdbc:mysql://localhost:3306/cookbook";
    private Connection connection;
    private boolean connectWithMySql = true;

    public DataBaseInitialization() {}

    public void setUpConnection() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, SQLException, IOException {
        if (connectWithMySql && mySqlBaseExists()) {
            System.out.println("Connected to MySql database.");
            mySqlCreateTablesWithSamples();
            mySqlCreateStoredProcedures();
        } else {
            System.out.println("Connected to ApacheDerby database.");
            createApacheBase();
            apacheCreateTablesWithSamples();
        }
    }

    private void createApacheBase() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").getDeclaredConstructor().newInstance();
        try {
            this.connection = DriverManager.getConnection("jdbc:derby:dbCookBook; create=true");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void mySqlCreateTablesWithSamples() throws SQLException, IOException {
        Statement statement = this.connection.createStatement();

        createTable(statementText("MySqlCreateTableProducts"), statement);
        createTable(statementText("MySqlCreateTableRecipes"), statement);
        createTable(statementText("MySqlCreateTableProductsInRecipes"), statement);
        createTable(statementText("MySqlCreateTableUsers"), statement);
        createTable(statementText("MySqlCreateTableUsersRecipes"), statement);

        fillTable(statementText("MySqlFillTableProducts"), statement);
        fillTable(statementText("MySqlFillTableRecipes"), statement);
        fillTable(statementText("MySqlFillTableProductsInRecipes"), statement);
        fillTable(statementText("MySqlFillTableUsers"), statement);
        fillTable(statementText("MySqlFillTableUsersRecipes"), statement);
    }

    private void mySqlCreateStoredProcedures() throws SQLException, IOException {
        Statement statement = this.connection.createStatement();

        createStoredProcedure(statementText("MySqlCreateAddProductInRecipeStoredProcedure"), statement);
    }

    private void apacheCreateTablesWithSamples() throws SQLException, IOException {
        Statement statement = this.connection.createStatement();

        statement.execute("DROP TABLE usersrecipes");
        statement.execute("DROP TABLE users");
        statement.execute("DROP TABLE productsinrecipes");
        statement.execute("DROP TABLE recipes");
        statement.execute("DROP TABLE products");

        createTable(statementText("ApacheCreateTableProducts"), statement);
        createTable(statementText("ApacheCreateTableRecipes"), statement);
        createTable(statementText("ApacheCreateTableProductsInRecipes"), statement);
        createTable(statementText("ApacheCreateTableUsers"), statement);
        createTable(statementText("ApacheCreateTableUsersRecipes"), statement);

        fillTable(statementText("ApacheFillTableProducts"), statement);
        fillTable(statementText("ApacheFillTableRecipes"), statement);
        fillTable(statementText("ApacheFillTableProductsInRecipes"), statement);
        fillTable(statementText("ApacheFillTableUsers"), statement);
        fillTable(statementText("ApacheFillTableUsersRecipes"), statement);
    }

    private boolean mySqlBaseExists() {
        try {
            this.connection = DriverManager.getConnection(URL, "root", "");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    private void createTable(String createTableStatement, Statement statement) {
        try {
            statement.execute(createTableStatement);
        } catch (SQLException ex) {
            System.out.println("Can't create the table: " + ex.getMessage());
        }
    }

    private void fillTable(String fillTableStatement, Statement statement) {
        try {
            statement.execute(fillTableStatement);
        } catch (SQLException ex) {
            System.out.println("Can't fill the table: " + ex.getMessage());
        }
    }

    private void createStoredProcedure(String createStoredProcedureStatement, Statement statement) {
        try {
            statement.execute(createStoredProcedureStatement);
        } catch (SQLException ex) {
            System.out.println("Can't create stored procedure: " + ex.getMessage());
        }
    }

    private String statementText(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), StandardCharsets.UTF_8);
    }
}
