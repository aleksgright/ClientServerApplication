package moviesApp.moviesAppServer.services;

import moviesApp.entities.Movie;
import moviesApp.entities.Person;

import java.sql.*;
import java.util.Hashtable;

public class SqlService {
    private final Connection connection;
    private static final String addMovie = "INSERT INTO movies (" +
            "id, name, coordinateX, coordinateY, creationDate, oscarsCount, movieGenre, mpaaRating," +
            " directorName, directorPassportId, directorEyeColor, directorHairColor, directorNationality, ownertoken)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String registerUser = "INSERT INTO users (token, login, passwordhash) VALUES (?, ?, ?)";
    private static final String checkLogin = "SELECT * FROM users WHERE login = ?";
    private static final String removeMovieWithoutToken = "DELETE FROM movies WHERE id = ?";
    private static final String removeMovieWithToken = "DELETE FROM movies WHERE id = ? AND ownertoken = ?";

    public SqlService(String url, String login, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, login, password);
//        Statement statement = connection.createStatement();
//        ResultSet rs = statement.executeQuery("SELECT * from movies");
//        int columns = rs.getMetaData().getColumnCount();
//        while(rs.next()){
//            for (int i = 1; i <= columns; i++){
//                System.out.print(rs.getString(i) + "\t");
//            }
//            System.out.println();
//        }
    }

    public ResultSet findUserWithLogin(String login) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(checkLogin);
        statement.setString(1, login);
        return statement.executeQuery();
    }

    public void registerUser(String token, String login, String passwordHash) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(registerUser);
        preparedStatement.setString(1, token);
        preparedStatement.setString(2, login);
        preparedStatement.setString(3, passwordHash);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void load(Hashtable<Integer, Movie> movieHashtable, Hashtable<String, Person> personHashtable) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM movies");
        while (resultSet.next()) {
            String[] values = new String[14];
            for (int i = 1; i < 15; i++) {
                values[i - 1] = resultSet.getString(i);
            }
            Movie movie = MovieStringConverter.readFromDb(values);
            movieHashtable.put(movie.getId(), movie);
            personHashtable.put(movie.getDirector().getPassportID(), movie.getDirector());
        }
        statement.close();
    }

    public void addMovie(Movie movie, String token) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(addMovie);
        preparedStatement.setInt(1, movie.getId());
        preparedStatement.setString(2, movie.getName());
        preparedStatement.setFloat(3, (float) movie.getCoordinates().getX());
        preparedStatement.setFloat(4, (float) movie.getCoordinates().getY());
        preparedStatement.setString(5, movie.getCreationDateString());
        preparedStatement.setInt(6, movie.getOscarsCount());
        preparedStatement.setString(7, movie.getGenre().toString());
        preparedStatement.setString(8, movie.getMpaaRating().toString());
        Person director = movie.getDirector();
        preparedStatement.setString(9, director.getName());
        preparedStatement.setString(10, director.getPassportID());
        preparedStatement.setString(11, director.getEyeColor().toString());
        preparedStatement.setString(12, director.getHairColor().toString());
        preparedStatement.setString(13, director.getNationality().toString());
        preparedStatement.setString(14, token);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void removeMovieById(int id, String token) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(removeMovieWithToken);
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, token);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void removeMovieById(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(removeMovieWithoutToken);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void clearDb() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM movies");
        statement.close();
    }
}
