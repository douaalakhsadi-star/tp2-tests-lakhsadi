package ma.emsi.lakhsadi.tp2.tests;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test3Traducteur {

    public static void main(String[] args) {
        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        PromptTemplate template = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}} : {{texte}}"
        );

        Prompt prompt = template.apply(
                Map.of(
                        "langue", "espagnol",
                        "texte", "Bonjour, comment allez-vous ?"
                )
        );

        String reponse = modele.chat(prompt.text());

        System.out.println("Prompt : " + prompt.text());
        System.out.println("Traduction : " + reponse);
    }
}