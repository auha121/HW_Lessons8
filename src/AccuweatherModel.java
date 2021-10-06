import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import entity.Weather;
import java.sql.SQLException;
import java.util.List;

public class AccuweatherModel implements WeatherModel {
    //http://dataservice.accuweather.com/forecasts/v1/daily/1day/349727
    //http://dataservice.accuweather.com/forecasts/v1/daily/5day/349727
    private static final String PROTOKOL = "https";
    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECASTS = "forecasts";
    private static final String VERSION = "v1";
    private static final String DAILY = "daily";
    private static final String ONE_DAY = "1day";
    private static final String FIVE_DAYS = "5day";
    private static final String API_KEY = "pXJd8MokcZCdrd2MsoGl2DBZAyCa0zvv";
    //AYecuUDdgEGi3U12dZxG39uXxrW0uAI3
    //juVJVVRsxPbWysISq5SRqoR1BzV4yr3Q
    private static final String API_KEY_QUERY_PARAM = "apikey";
    private static final String LOCATIONS = "locations";
    private static final String CITIES = "cities";
    private static final String AUTOCOMPLETE = "autocomplete";
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String cityString;

    private DataBaseRepository dataBaseRepository = new DataBaseRepository();

    @Override
    public void getWeather(String city, Period period) throws IOException {
        String date;
        Integer temperatureMin;
        Integer temperatureMax;
        Integer temperature;
        switch (period) {
            case NOW:
                HttpUrl oneDayHttpUrl = new HttpUrl.Builder()
                        .scheme(PROTOKOL)
                        .host(BASE_HOST)
                        .addPathSegment(FORECASTS)
                        .addPathSegment(VERSION)
                        .addPathSegment(DAILY)
                        .addPathSegment(ONE_DAY)
                        .addPathSegment(detectCityKey(city))
                        .addQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                        .build();
                Request oneDayRequest = new Request.Builder()
                        .url(oneDayHttpUrl)
                        .build();
                Response oneDayForecastResponse = okHttpClient.newCall(oneDayRequest).execute();
                String oneDayWeatherResponse = oneDayForecastResponse.body().string();
                WeatherResponse oneDayWeatherRespon = new WeatherResponse();
                oneDayWeatherRespon.writeWeatherResponse(oneDayWeatherResponse, 1, cityString);
                date = objectMapper.readTree(oneDayWeatherResponse).at("/DailyForecasts").get(0).at("/Date").asText();
                temperatureMin = objectMapper.readTree(oneDayWeatherResponse).at("/DailyForecasts").get(0).at("/Temperature/Minimum/Value").asInt();
                temperatureMax = objectMapper.readTree(oneDayWeatherResponse).at("/DailyForecasts").get(0).at("/Temperature/Maximum/Value").asInt();
                temperature = (int)((((temperatureMax + temperatureMin) / 2) - 32) / 1.8);
                //System.out.println(oneDayWeatherResponse);
                try {
                    dataBaseRepository.saveWeatherToDataBase(new Weather(cityString, date, temperature));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case FIVE_DAYS:
                HttpUrl fiveDayHttpUrl = new HttpUrl.Builder()
                        .scheme(PROTOKOL)
                        .host(BASE_HOST)
                        .addPathSegment(FORECASTS)
                        .addPathSegment(VERSION)
                        .addPathSegment(DAILY)
                        .addPathSegment(FIVE_DAYS)
                        .addPathSegment(detectCityKey(city))
                        .addQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                        .build();
                Request fiveDayRequest = new Request.Builder()
                        .url(fiveDayHttpUrl)
                        .build();
                Response fiveDayForecastResponse = okHttpClient.newCall(fiveDayRequest).execute();
                String fiveDayWeatherResponse = fiveDayForecastResponse.body().string();
                WeatherResponse fiveDayWeatherRespon = new WeatherResponse();
                fiveDayWeatherRespon.writeWeatherResponse(fiveDayWeatherResponse, 5, cityString);
                //System.out.println(fiveDayWeatherResponse);
                for (int i = 0; i < 5; i++) {
                    date = objectMapper.readTree(fiveDayWeatherResponse).at("/DailyForecasts").get(i).at("/Date").asText();
                    temperatureMin = objectMapper.readTree(fiveDayWeatherResponse).at("/DailyForecasts").get(i).at("/Temperature/Minimum/Value").asInt();
                    temperatureMax = objectMapper.readTree(fiveDayWeatherResponse).at("/DailyForecasts").get(i).at("/Temperature/Maximum/Value").asInt();
                    temperature = (int)((((temperatureMax + temperatureMin) / 2) - 32) / 1.8);
                    try {
                        dataBaseRepository.saveWeatherToDataBase(new Weather(cityString, date, temperature));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public List<Weather> getSavedToDBWeather() {
        return dataBaseRepository.getSavedToDBWeather();
    }

    @Override
    public Weather getSavedToDBWeatherOne(String city) throws IOException {
        String cityKey = detectCityKey(city);
        return dataBaseRepository.getSavedToDBWeatherOne(cityString);
    }

    private String detectCityKey(String city) throws IOException {
        //http://dataservice.accuweather.com/locations/v1/cities/autocomplete
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(PROTOKOL)
                .host(BASE_HOST)
                .addPathSegment(LOCATIONS)
                .addPathSegment(VERSION)
                .addPathSegment(CITIES)
                .addPathSegment(AUTOCOMPLETE)
                .addQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                .addQueryParameter("q", city)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .addHeader("accept", "application/json")
                .build();
        Response locationResponse = okHttpClient.newCall(request).execute();
        String locationResponseString = locationResponse.body().string();
        String cityKey = objectMapper.readTree(locationResponseString).get(0).at("/Key").asText();
        cityString = objectMapper.readTree(locationResponseString).get(0).at("/LocalizedName").asText();
        return cityKey;
    }
}