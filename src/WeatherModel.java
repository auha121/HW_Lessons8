import java.io.IOException;
import entity.Weather;
import java.util.List;

public interface WeatherModel {
    void getWeather(String city, Period period) throws IOException;
    public List<Weather> getSavedToDBWeather();
    public Weather getSavedToDBWeatherOne(String city) throws IOException;
}