import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseProviderForRecipes extends DataBaseProvider {

    public DataBaseProviderForRecipes(Connection connection) throws SQLException {
        super(connection);
    }

    String showItemInfo(String itemToFind) {
        try {
            String query = "SELECT id_recipe, name, instructions, meal FROM recipes WHERE name LIKE '" + itemToFind + "'";
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

    public Integer findRecipeId(String recipe){
        try {
            String query = "SELECT id_recipe FROM recipes WHERE name LIKE '" + recipe + "'";
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

    public boolean addRecipe(Recipe recipe) {
        try {
            statement.execute("INSERT INTO recipes(name, instructions, meal)" + "VALUES ('" +
                    recipe.getName() + "', '" + recipe.getInstructions() + "', '" + recipe.getMeal() + "')");
            System.out.println("Dodano do recipes");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't insert this product. Error: " + e.getMessage());
            return false;
        }
    }

    public String selectAllNamesInMealGroup(String mealGroup){
        try {
            String query = "SELECT name FROM recipes WHERE meal = '" + mealGroup + "' ORDER BY name";
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
