package org.example;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        try {
            String apiKey = "a776301b-1d50-4284-ab76-ef85aba2ac6b";
            String lat = "55.75";
            String lon = "37.62";
            int limit = 5;

            String url = String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&limit=%d", lat, lon, limit);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Yandex-API-Key", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.body());

                System.out.println("Все данные: " + rootNode.toPrettyString());

                getCurrentTemp(rootNode);

                getAverageTemp(rootNode, (double) limit);
            } else {
                System.out.println("Ошибка: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCurrentTemp(JsonNode json) {
        JsonNode factNode = json.path("fact");
        int currentTemp = factNode.path("temp").asInt();
        System.out.println("Текущая температура: " + currentTemp);
    }

    public static void getAverageTemp(JsonNode json, Double limit) {
        JsonNode forecastsNode = json.path("forecasts");
        double sumTemp = 0;
        for (JsonNode dayNode : forecastsNode) {
            int dayTemp = dayNode.path("parts").path("day").path("temp_avg").asInt();
            sumTemp += dayTemp;
        }

        double avgTemp = sumTemp / limit;
        System.out.println("Средняя температура за период: " + avgTemp);
    }
}
