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

    public DataBaseInitialization() {}

    public void setUpConnection() throws SQLException, IOException {
        if (mySqlBaseExists()) {
            System.out.println("Connected to MySql database.");
            mySqlCreateTablesWithSamples();
            mySqlCreateStoredProcedures();
        } else {
            System.out.println("Can't connect to data base");
        }
    }

    private void mySqlCreateTablesWithSamples() throws SQLException, IOException {
        Statement statement = this.connection.createStatement();

        statement.execute("DROP TABLE IF EXISTS usersrecipes");
        statement.execute("DROP TABLE IF EXISTS users");
        statement.execute("DROP TABLE IF EXISTS productsinrecipes");
        statement.execute("DROP TABLE IF EXISTS recipes");
        statement.execute("DROP TABLE IF EXISTS products");

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

        statement.execute("DROP PROCEDURE IF EXISTS addProductInRecipe");
        statement.execute("DROP PROCEDURE IF EXISTS findProductsInRecipe");
        statement.execute("DROP PROCEDURE IF EXISTS findGramsInRecipe");
        statement.execute("DROP PROCEDURE IF EXISTS findCaloriesInRecipe");

        createStoredProcedure(statementText("MySqlCreateAddProductInRecipeStoredProcedure"), statement);
        createStoredProcedure(statementText("MySqlCreateFindProductsInRecipeStoredProcedure"), statement);
        createStoredProcedure(statementText("MySqlCreateFindGramsInRecipeStoredProcedure"), statement);
        createStoredProcedure(statementText("MySqlCreateFindCaloriesInRecipeStoredProcedure"), statement);
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
