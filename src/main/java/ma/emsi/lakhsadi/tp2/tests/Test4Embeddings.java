package ma.emsi.lakhsadi.tp2.tests;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel.TaskTypeEnum;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

public class Test4Embeddings {

    public static void main(String[] args) {
        EmbeddingModel modeleLocal = new BgeSmallEnV15EmbeddingModel();

        EmbeddingModel modeleGoogle = GoogleGenAiEmbeddingModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-embedding-001")
                .taskType(TaskTypeEnum.SEMANTIC_SIMILARITY)
                .timeout(Duration.ofSeconds(30))
                .build();

        String[][] paires = {
                {"The cat is sleeping.", "The cat is sleeping."},
                {"The cat is sleeping.", "A kitten is resting."},
                {"The cat is sleeping.", "I am repairing my car."},
                {"I love programming.", "I enjoy writing code."},
                {"Le chat dort.", "Un chat est endormi."},
                {"Le chat dort.", "Je répare ma voiture."}
        };

        for (String[] paire : paires) {
            System.out.println("Texte 1 : " + paire[0]);
            System.out.println("Texte 2 : " + paire[1]);

            double scoreLocal =
                    calculerSimilarite(modeleLocal, paire[0], paire[1]);
            System.out.printf("Similarité locale : %.4f%n", scoreLocal);

            double scoreGoogle =
                    calculerSimilarite(modeleGoogle, paire[0], paire[1]);
            System.out.printf("Similarité Google : %.4f%n%n", scoreGoogle);
        }
    }

    private static double calculerSimilarite(
            EmbeddingModel modele, String texte1, String texte2) {

        Embedding embedding1 = modele.embed(texte1).content();
        Embedding embedding2 = modele.embed(texte2).content();

        return CosineSimilarity.between(embedding1, embedding2);
    }
}