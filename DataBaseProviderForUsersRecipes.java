import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseProviderForUsersRecipes extends DataBaseProvider {
    private User currentUser;

    public DataBaseProviderForUsersRecipes(Connection connection) throws SQLException {
        super(connection);
        this.currentUser = new User("guest", "");
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public boolean addUserRecipe(Integer id_user, Integer id_recipe) {
        try {
            statement.execute("INSERT INTO usersRecipes(id_user, id_recipe) VALUES (" + id_user + " , " + id_recipe + ")");
            System.out.println("Dodano do usersrecipes");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't find products in addUserRecipes method: " + e.getMessage());
            return false;
        }
    }
}
