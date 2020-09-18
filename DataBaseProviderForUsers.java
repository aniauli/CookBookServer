import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseProviderForUsers extends DataBaseProvider {

    public DataBaseProviderForUsers(Connection connection) throws SQLException {
        super(connection);
    }

    public String findPassword(String username){
        try {
            String query = "SELECT password FROM users WHERE username LIKE '" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                return resultSet.getString(1);
            }
            return "";
        } catch (SQLException e) {
            System.out.println("Can't find username: " + e.getMessage());
            return "";
        }
    }

    public boolean checkIfUsernameIsOccupied(String username){
        try {
            String query = "SELECT username FROM users WHERE username LIKE '" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Can't find username: " + e.getMessage());
            return false;
        }
    }

    public String checkIfPasswordIsCorrect(String username){
        try {
            String query = "SELECT password FROM users WHERE username LIKE '" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                return resultSet.getString(1);
            }
            return "";
        } catch (SQLException e) {
            System.out.println("Cannot check if password is correct: " + e.getMessage());
            return "";
        }
    }

    public boolean addUser(User user) {
        try {
            statement.execute("INSERT INTO users(username, password)" + "VALUES ('" +
                    user.getUsername() + "', '" + user.getPassword() + "')");
            return true;
        } catch (SQLException e) {
            System.out.println("Can't insert this user. Error: " + e.getMessage());
            return false;
        }
    }

    public Integer findUserId(String username) {
        try {
            String query = "SELECT id_user FROM users WHERE username LIKE '" + username + "'";
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
}
