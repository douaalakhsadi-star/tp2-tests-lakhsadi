package ma.emsi.lakhsadi.tp2.outil.meteo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class MeteoTool {

    @Tool("""
            Retourne true si la probabilité d'avoir de la pluie pour la ville dont on donne la latitude et la longitude
            est importante durant une période de nbJours jours. La période commence nbJoursDebut jours après aujourd'hui.
            Par exemple, si nbJoursDebut = 1 et nbJours = 2, la période de 2 jours commence demain.
            Si nbJoursDebut = 0 et nbJours = 3, la période de 3 jours commence aujourd'hui.
            Retourne false sinon.
            """)
    public boolean isPluiePrevue(
            @P(value = "Latitude", name = "latitude") double latitude,
            @P(value = "Longitude", name = "longitude") double longitude,
            @P(value = "Nombre de jours après aujourd'hui pour indiquer le début de la période",
                    name = "nbJoursDebut") int nbJoursDebut,
            @P(value = "Nombre de jours de la période",
                    name = "nbJours") int nbJours) {

        double seuilPluie = 25.0;

        try {
            String baseUrl = "https://api.open-meteo.com/v1/forecast";
            int finPeriode = Math.min(nbJoursDebut + nbJours, 7);

            String apiUri = String.format(
                    Locale.US,
                    "%s?latitude=%f&longitude=%f&daily=precipitation_probability_max&forecast_days=%d",
                    baseUrl, latitude, longitude, finPeriode
            );

            HttpURLConnection connection =
                    (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            Gson gson = new Gson();
            JsonObject jsonResponse =
                    gson.fromJson(response, JsonObject.class);

            // System.out.println("Réponse de l'API météo : " + response);

            JsonObject daily = jsonResponse.getAsJsonObject("daily");
            int[] precipitationProbabilities = gson.fromJson(
                    daily.get("precipitation_probability_max"),
                    int[].class
            );

            for (int i = nbJoursDebut; i < finPeriode; i++) {
                if (precipitationProbabilities[i] > seuilPluie) {
                    return true;
                }
            }

            return false;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération de la météo pour ("
                            + latitude + ", " + longitude + ") : "
                            + e.getMessage()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Tool("""
            Retourne la latitude et la longitude d'une ville.
            Le nom de la ville et, éventuellement, du pays doivent être en anglais.
            Le résultat contient la latitude à l'indice 0 et la longitude à l'indice 1.
            Si plusieurs villes correspondent, retourne la première.
            """)
    public double[] getCoordonnees(
            @P(value = "Nom de la ville en anglais, éventuellement suivi du pays",
                    name = "ville") String ville) {

        try {
            String baseUrl =
                    "https://geocoding-api.open-meteo.com/v1/search?name=";

            // Encoder le nom pour accepter les espaces et les accents.
            String apiUri = baseUrl
                    + URLEncoder.encode(ville, StandardCharsets.UTF_8);

            HttpURLConnection connection =
                    (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // System.out.println("Réponse de l'API de géocodage : " + response);

            Gson gson = new Gson();
            JsonObject jsonResponse =
                    gson.fromJson(response, JsonObject.class);

            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");

                if (!results.isEmpty()) {
                    JsonObject premierResultat =
                            results.get(0).getAsJsonObject();

                    double latitude =
                            premierResultat.get("latitude").getAsDouble();
                    double longitude =
                            premierResultat.get("longitude").getAsDouble();

                    return new double[]{latitude, longitude};
                }
            }

            throw new RuntimeException(
                    "Aucune coordonnée trouvée pour la ville : " + ville
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des coordonnées pour la ville : "
                            + ville,
                    e
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}