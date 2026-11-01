package ma.emsi.lakhsadi.tp2.tests;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.util.Scanner;
/**
 * Le RAG facile !
 */
public class Test6Rag2 {

    // Assistant conversationnel
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String llmKey = System.getenv("GEMINI_KEY");

        if (llmKey == null) {
            System.out.println(
                    "La variable d'environnement GEMINI_KEY n'est pas définie."
            );
            return;
        }

        // Température inférieure ou égale à 0,3 pour le RAG.
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.3)
                .build();

        String nomDocument = "Machine learning.pdf";
        Document document =
                FileSystemDocumentLoader.loadDocument(nomDocument);

        EmbeddingStore<TextSegment> embeddingStore =
                new InMemoryEmbeddingStore<>();

        // Phase 1 : ingestion du document dans la base vectorielle.
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        // Assistant avec mémoire et recherche dans les documents.
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(
                        EmbeddingStoreContentRetriever.from(embeddingStore)
                )
                .build();

        // Phase 2 : recherche des informations et génération de la réponse.
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Posez votre question : ");
                String question = scanner.nextLine();

                if (question.isBlank()) {
                    continue;
                }

                System.out.println("==================================================");

                if ("fin".equalsIgnoreCase(question)) {
                    break;
                }

                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }
}