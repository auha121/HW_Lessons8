import entity.Weather;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataBaseRepository {
    private String insertWeather = "insert into weather (city, local_date, temperature) values (?, ?, ?)";
    private String getWeather = "select * from weather";
    private static final String DB_PATH = "jdbc:sqlite:geekbrains.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean saveWeatherToDataBase(Weather weather) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement saveWeather = connection.prepareStatement(insertWeather);
            saveWeather.setString(1, weather.getCity());
            saveWeather.setString(2, weather.getLocalDate());
            saveWeather.setInt(3, weather.getTemperature());
            return saveWeather.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new SQLException("Сохранение погоды в базу данных не выполнено!");
    }

    public void saveWeatherToDataBase(List<Weather> weatherList) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement saveWeather = connection.prepareStatement(insertWeather);
            for (Weather weather : weatherList) {
                saveWeather.setString(1, weather.getCity());
                saveWeather.setString(2, weather.getLocalDate());
                saveWeather.setInt(3, weather.getTemperature());
                saveWeather.addBatch();
            }
            saveWeather.executeBatch();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Weather getSavedToDBWeatherOne(String cityString) {
        Weather weather = null;
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getWeather);
            while (resultSet.next()) {
                if (cityString.equals(resultSet.getString("city"))) {
                    System.out.print("id " + resultSet.getInt("id"));
                    System.out.print("; city " + resultSet.getString("city"));
                    System.out.print("; date " + resultSet.getString("local_date"));
                    System.out.println("; temperature " + resultSet.getInt("temperature"));
                    weather = new Weather(resultSet.getString("city"),
                            resultSet.getString("local_date"),
                            resultSet.getInt("temperature"));
                    break;
                }
            }
            if (weather == null) System.out.println("запись не найдена");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return weather;
    }

    public List<Weather> getSavedToDBWeather() {
        List<Weather> weathers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getWeather);
            while (resultSet.next()) {
                System.out.print("id " + resultSet.getInt("id"));
                System.out.print("; city " + resultSet.getString("city"));
                System.out.print("; date " + resultSet.getString("local_date"));
                System.out.println("; temperature " + resultSet.getInt("temperature"));
                weathers.add(new Weather(resultSet.getString("city"),
                        resultSet.getString("local_date"),
                        resultSet.getInt("temperature")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return weathers;
    }
}