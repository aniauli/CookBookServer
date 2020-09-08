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
    String findInTable(String itemToFind) {
        try {
            String query = "SELECT id, name, instructions, meal FROM recipes WHERE name LIKE '" + itemToFind + "'";
            ResultSet resultSet = statement.executeQuery(query);
            String result = "null";
            if(resultSet.next()) {
                result = String.format("%s;%s;%s", resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Can't find products from table recipes: " + e.getMessage());
            return "SQL Error";
        }
    }

    public Integer findIdInTable(String recipe){
        try {
            String query = "SELECT id FROM recipes WHERE name LIKE '" + recipe + "'";
            ResultSet resultSet = statement.executeQuery(query);
            Integer result = 0;
            if(resultSet.next()) {
                result = Integer.parseInt(resultSet.getString(1));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Can't find products from table recipes: " + e.getMessage());
            return 0;
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

    public String selectAllNamesFromTableInCondition(String tableName, String condition){
        try {
            String query = "SELECT name FROM " + tableName + " WHERE meal = '" + condition + "' ORDER BY name";
            ResultSet resultSet = statement.executeQuery(query);
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                result.append(String.format("%s;", resultSet.getString(1)));
            }
            return result.toString();
        } catch (SQLException ex) {
            System.out.println("Can't return all names from table: " + ex.getMessage());
            return "null";
        }
    }

}
