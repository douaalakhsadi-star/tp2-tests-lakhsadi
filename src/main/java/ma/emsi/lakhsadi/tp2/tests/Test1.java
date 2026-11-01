package ma.emsi.lakhsadi.tp2.tests;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
public class Test1 {

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        String question1 = "Comment s'appelle le chat de Pierre ?";
        String reponse1 = modele.chat(question1);
        System.out.println("Question 1 : " + question1);
        System.out.println("Réponse 1 : " + reponse1);

        String question2 = "Comment est-ce que je m'appelle ?";
        String reponse2 = modele.chat(question2);
        System.out.println("Question 2 : " + question2);
        System.out.println("Réponse 2 : " + reponse2);
    }
}