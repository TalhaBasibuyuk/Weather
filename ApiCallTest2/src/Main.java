import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try{
            Scanner scanner = new Scanner(System.in);
            String city;
            do {
                System.out.println("=========================================");
                System.out.print("City: ");
                city = scanner.nextLine();

                if(city.equalsIgnoreCase("No")) break;

                JSONObject cityLocationData = (JSONObject) getLocationData(city);
                double latitude = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");

                displayWeatherData(latitude,longitude);

            }while (!city.equalsIgnoreCase("No"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static JSONObject getLocationData(String city){
        city = city.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
         city + "&count=1&language=en&format=json";

        try {
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            // check response status
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.get(0);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConnection){
        try {

            StringBuilder resultsJson = new StringBuilder();

            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNext()){
                resultsJson.append(scanner.nextLine());
            }

            scanner.close();

            return  resultsJson.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static void displayWeatherData(double latitude, double longitude){
        try {

            String url = "https://api.open-meteo.com/v1/forecast?latitude=" +
             latitude +
              "&longitude=" +
               longitude +
                "&current=temperature_2m,relative_humidity_2m,is_day,rain,snowfall";
            HttpURLConnection apiConnection = fetchApiResponse(url);

            // check response status
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return;
            }

            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) resultsJsonObj.get("current");

            String time = (String) currentWeatherJson.get("time");
            System.out.println("Current Time: " + time);

            double temperature = (double) currentWeatherJson.get("temperature_2m");
            System.out.println("Current Temperature (C): " + temperature);

            long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
            System.out.println("Relatie Humidity: " + time);

            long isDay = (long) currentWeatherJson.get("is_day");
            if(isDay == 1) System.out.println("Time of Day: Day"); else System.out.println("Time of Day: Night");

            double rain = (double) currentWeatherJson.get("rain");
            double snow = (double) currentWeatherJson.get("snowfall");
            if(rain == 1) {
                System.out.println("Weather: Rainy");
            } else if(snow == 1) {
                System.out.println("Weather: Snowfall");
            } else {
                System.out.println("Weather: Clear");
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}