package ma.emsi.lakhsadi.tp2.tests;

import ma.emsi.lakhsadi.tp2.outil.meteo.MeteoTool;

public class TestMeteo {

    public static void main(String[] args) {
        MeteoTool meteo = new MeteoTool();

        // Paris : séjour de 3 jours commençant dans 2 jours.
        boolean pluie = meteo.isPluiePrevue(48.85, 2.35, 2, 3);

        System.out.println("Pluie prévue pendant le séjour : " + pluie);
    }
}