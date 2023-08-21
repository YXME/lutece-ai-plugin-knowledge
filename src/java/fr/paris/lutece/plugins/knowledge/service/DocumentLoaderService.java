package fr.paris.lutece.plugins.knowledge.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.DocumentType;
import dev.langchain4j.data.document.splitter.SentenceSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.langchain4j.model.openai.OpenAiModelName.TEXT_EMBEDDING_ADA_002;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.joining;

import dev.langchain4j.data.document.parser.PdfDocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;

import java.io.IOException;

public final class DocumentLoaderService {

	static int maxResults = 5;
    static double minSimilarity = 0.5;
    
	static DocumentParser _pdfParser = new PdfDocumentParser();
	static DocumentParser _docparser = new TextDocumentParser(DocumentType.DOC);
	static DocumentSplitter _splitter = new SentenceSplitter();
	static EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
	
	static EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .modelName(TEXT_EMBEDDING_ADA_002)
            .timeout(ofSeconds(600))
            .logRequests(true)
            .logResponses(true)
            .build();
	
	static ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .modelName(GPT_3_5_TURBO)
            .temperature(0.0)
            .timeout(ofSeconds(600))
            .maxRetries(3)
            .logResponses(true)
            .logRequests(true)
            .build();
	

    static ChatMemory chatMemory = TokenWindowChatMemory
    		.builder().maxTokens(1, new OpenAiTokenizer(GPT_3_5_TURBO)).build();

	
	public static void loadFile(FileItem file) 
	{
		try 
		{
			Document document = _pdfParser.parse(file.getInputStream());
			document.metadata().add("file_name", file.getName());
			document.metadata();
			
		    
		    List<TextSegment> segments = _splitter.split(document);
		    List<Embedding> embeddings = embeddingModel.embedAll(segments);
		
		    embeddingStore.addAll(embeddings, segments);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static String askQuestion(String question) {
		
	    Embedding questionEmbedding = embeddingModel.embed(question);

	    List<EmbeddingMatch<TextSegment>> relevantEmbeddings
	            = embeddingStore.findRelevant(questionEmbedding, maxResults, minSimilarity);

	    PromptTemplate promptTemplate = PromptTemplate.from(
	            "Réponds à la requête entre double guillemets du mieux possible.\n"
	            + "Base ta réponse sur les données présente entre triples guillemets.\n"
	                    + "\"\"{{question}}\"\""
	                    + "\n"
	                    + "\"\"\"{{information}}\"\"\""
	                    + "Si les données présente entre triples guillemets ne contiennent pas d'information pour répondre à la requête, "
	                    + "réponds la phrase présente entre quadruple guillemets"
	                    + "\"\"\"\"Je suis désolé, les informations données ne me permettent pas de vous répondre.\"\"\"\"");

	    String information = relevantEmbeddings.stream()
	            .map(match -> match.embedded().text())
	            .collect(joining("\n\n"));

	    Map<String, Object> variables = new HashMap<>();
	    variables.put("question", question);
	    variables.put("information", information);

	    Prompt prompt = promptTemplate.apply(variables);

	    AiMessage aiMessage = chatModel.sendUserMessage(prompt.toUserMessage());

	    String answer = aiMessage.text();
	    chatMemory.add(aiMessage);
	    return answer;

	}
}