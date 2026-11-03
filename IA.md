# Utilisation de l’IA — TP 2


J’ai utilisé une IA pour comprendre certaines notions, écrire et
corriger certaines parties du code, résoudre des erreurs et préparer
le compte rendu.

Les principales aides concernaient les embeddings, le RAG 
 Le problème de timeout est détaillé ci-dessous.

## 1. Timeout du modèle d’embeddings Google

### Problème rencontré
L’appel au modèle Google échouait avec une erreur « timeout »
avec le délai de 100 ms demandé pour le premier essai.

### Raison de l’utilisation de l’IA
J’ai demandé de l’aide pour comprendre cette erreur et poursuivre le test.

### Prompt utilisé
J’ai transmis les logs contenant :
« Caused by: java.io.InterruptedIOException: timeout ».

### Extrait pertinent de la réponse de l’IA
« Le délai de 100 ms est trop court. »
L’IA a proposé de remplacer :
.timeout(Duration.ofMillis(100))
par :
.timeout(Duration.ofSeconds(30))

### Ce que j’ai appris
Le timeout est le temps maximal pendant lequel le programme attend
la réponse de l’API. Avec 100 ms, l’attente était trop courte et
l’appel échouait. En passant à 30 secondes, le programme a laissé
plus de temps au serveur pour répondre et le test a fonctionné.
## 2. Mise en place et compréhension du RAG

### Problèmes rencontrés
Je ne savais pas comment adapter le code du professeur pour utiliser
infos.txt, puis un PDF et une conversation dans la console.
J’ai aussi rencontré un conflit entre plusieurs modèles d’embeddings.

### Raison de l’utilisation de l’IA
J’ai demandé de l’aide pour adapter le code, résoudre le conflit
de dépendances et comprendre les résultats obtenus.

### Prompts utilisés
- « corrige et donne moi tout script »
- J’ai transmis l’erreur :
  « Conflict: multiple embedding models have been found in the classpath. »
- « on a fait ça ? », avec la consigne sur Assistant et AiServices.

### Réponses de l’IA — extraits pertinents
L’IA a fourni du code adapté pour Test5Rag1 et expliqué comment
désactiver la dépendance du modèle local pour éviter le conflit
avec EasyRAG. Elle m’a ensuite guidée pour charger le PDF dans
Test6Rag2 et ajouter la boucle de conversation avec Scanner.

Elle a expliqué :
« Le modèle s’est appuyé sur l’information récupérée dans le document,
même si elle est fausse. »

### Ce que j’ai appris
Le RAG recherche dans les documents les passages utiles à la question
et les transmet au modèle pour l’aider à répondre. Le modèle a répondu
« Marseille » parce que cette information figurait dans infos.txt.
J’ai compris que le RAG peut aussi transmettre une information fausse :
il faut donc vérifier la fiabilité des documents et des réponses.