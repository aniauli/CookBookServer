import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseProviderForRecipes extends DataBaseProvider {

    public final static String CREATE_TABLE_RECIPES =  "CREATE TABLE recipes (" +
            "   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "   name VARCHAR(50) UNIQUE," +
            "   instructions VARCHAR(1000)," +
            "   CONSTRAINT recipePrimaryKey PRIMARY KEY(id, name)" +
            "   )";

    public DataBaseProviderForRecipes() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
    }

    @Override
    void selectAllFromTable() {
        try {
            String query = "SELECT id, name, instructions, meal FROM recipes";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                printAllFromTable(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Can't view all products from table recipes: " + e.getMessage());
        }
    }

    @Override
    void printAllFromTable(ResultSet resultSet) throws SQLException {
        System.out.printf("%-5d \t %-50s \t %-300s \t %-50s \n",
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4)
        );
    }

    @Override
    String findInTable(String itemToFind) {
        try {
            String query = "SELECT id, name, instructions, meal FROM products WHERE name LIKE '" + itemToFind + "'";
            ResultSet resultSet = statement.executeQuery(query);
            String result = "null";
            if(resultSet.next()) {
                result = String.format("%s;%s;%s", resultSet.getString(2),
                        resultSet.getDouble(3), resultSet.getString(4));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Can't find products from table recipes: " + e.getMessage());
            return "SQL Error";
        }
    }

    public boolean insertIntoTable(Recipe recipe) {
        try {
            statement.execute("INSERT INTO recipes(name, instructions)" + "VALUES ('" +
                    recipe.getName() + "', '" + recipe.getInstructions() + "')");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        DataBaseProviderForRecipes dataBaseProviderForRecipes = new DataBaseProviderForRecipes();
        dataBaseProviderForRecipes.selectAllFromTable();
        dataBaseProviderForRecipes.shutDownDataBase();
    }

}
