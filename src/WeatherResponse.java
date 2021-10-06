import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherResponse {

    public WeatherResponse() {

    }

    public void writeWeatherResponse(String weatherJsonString, int day, String cityString) {
        ObjectMapper objectMapperWeather = new ObjectMapper();
        try {
            if (day == 1) {
            String weatherDate = objectMapperWeather.readTree(weatherJsonString).at("/DailyForecasts").get(0).at("/Date").asText();
            String weatherWeatherText = objectMapperWeather.readTree(weatherJsonString).at("/Headline/Text").asText();
            Integer weatherTemperatureMin = objectMapperWeather.readTree(weatherJsonString).at("/DailyForecasts").get(0).at("/Temperature/Minimum/Value").asInt();
            Integer weatherTemperatureMax = objectMapperWeather.readTree(weatherJsonString).at("/DailyForecasts").get(0).at("/Temperature/Maximum/Value").asInt();
            Integer weatherTemperature = (int)((((weatherTemperatureMax + weatherTemperatureMin) / 2) - 32) / 1.8);
            System.out.println("В городе " + cityString + " на дату " + weatherDate + " ожидается " + weatherWeatherText
                               + ", средняя температура воздуха " + weatherTemperature + " C");
            }
            else if (day == 5) {
                for (int i = 0; i < 5; i++) {
                    String weatherDate = objectMapperWeather.readTree(weatherJsonString).at("/DailyForecasts").get(i).at("/Date").asText();
                    String weatherWeatherText = objectMapperWeather.readTree(weatherJsonString).at("/Headline/Text").asText();
                    Integer weatherTemperatureMin = objectMapperWeather.readTree(weatherJsonString).at("/DailyForecasts").get(i).at("/Temperature/Minimum/Value").asInt();
                    Integer weatherTemperatureMax = objectMapperWeather.readTree(weatherJsonString).at("/DailyForecasts").get(i).at("/Temperature/Maximum/Value").asInt();
                    Integer weatherTemperature = (int)((((weatherTemperatureMax + weatherTemperatureMin) / 2) - 32) / 1.8);
                    System.out.println("В городе " + cityString + " на дату " + weatherDate + " ожидается " + weatherWeatherText
                            + ", средняя температура воздуха " + weatherTemperature + " C");
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}