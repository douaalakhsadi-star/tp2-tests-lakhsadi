package ma.emsi.lakhsadi.tp2.outil.meteo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Scanner;

public class MeteoTool {

    @Tool("""
            Retourne true si la probabilité d'avoir de la pluie pour la ville dont on donne la latitude et la longitude
            est importante durant une période de nbJours jours. La période commence nbJoursDebut jours après aujourd'hui.
            Par exemple, si nbJoursDebut = 1 et nbJours = 2, la période de 2 jours commence demain (1 jour après aujourd'hui).
            Si nbJoursDebut = 0 et nbJours = 3, la période de 3 jours commence aujourd'hui.
            Retourne false sinon.
            """)
    public boolean isPluiePrevue(
            @P(value = "Latitude", name = "latitude") double latitude,
            @P(value = "Longitude", name = "longitude") double longitude,
            @P(value = "Nombre de jours après aujourd'hui pour indiquer le début de la période",
                    name = "nbJoursDebut") int nbJoursDebut,
            @P(value = "Nombre de jours de la période", name = "nbJours") int nbJours) {

        // Seuil au-delà duquel on conseille de prendre un parapluie.
        double seuilPluie = 25.0;

        try {
            // Construire l'URL de l'API météo.
            String baseUrl = "https://api.open-meteo.com/v1/forecast";
            int finPeriode = Math.min(nbJoursDebut + nbJours, 7);

            String apiUri = String.format(
                    Locale.US,
                    "%s?latitude=%f&longitude=%f&daily=precipitation_probability_max&forecast_days=%d",
                    baseUrl, latitude, longitude, finPeriode
            );

            // Ouvrir une connexion HTTP.
            HttpURLConnection connection =
                    (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            // Lire la réponse.
            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Lire le document JSON.
            Gson gson = new Gson();
            JsonObject jsonResponse =
                    gson.fromJson(response, JsonObject.class);

            // System.out.println("Réponse de l'API météo : " + response);

            // Extraire les probabilités de pluie.
            JsonObject daily = jsonResponse.getAsJsonObject("daily");
            int[] precipitationProbabilities = gson.fromJson(
                    daily.get("precipitation_probability_max"),
                    int[].class
            );

            // Vérifier les jours de la période demandée.
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
}