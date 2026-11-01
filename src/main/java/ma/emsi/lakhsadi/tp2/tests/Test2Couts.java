package ma.emsi.lakhsadi.tp2.tests;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

public class Test2Couts {

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        String question = "Je dois partir dans 2 jours à Paris ; j'y resterai 3 jours. Est-ce que je dois mettre un parapluie dans mes valises ?";

        ChatResponse reponse = modele.chat(
                UserMessage.from(question)
        );

        System.out.println("Question : " + question);
        System.out.println("Réponse : " + reponse.aiMessage().text());
        System.out.println("Tokens d'entrée : "
                + reponse.tokenUsage().inputTokenCount());
        System.out.println("Tokens de sortie : "
                + reponse.tokenUsage().outputTokenCount());
        System.out.println("Total des tokens : "
                + reponse.tokenUsage().totalTokenCount());
        double cout = (
                reponse.tokenUsage().inputTokenCount() * 0.30
                        + reponse.tokenUsage().outputTokenCount() * 2.50
        ) / 1_000_000;

        System.out.printf("Coût théorique : %.10f $%n", cout);
        System.out.println("Nombre de requêtes pour 1 $ : " + (long) (1 / cout));
    }
}