package YAMSABU.BreatheLion_backend.global.ai.service;

import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawListDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.SOA_DTO;
import YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forANSWER;
import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forLAWS;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService{

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Override
    @Transactional
    public String ChatAnswer(String message){
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4.1-nano")
                .temperature(0.7)
                .build();

        return chatClient.prompt()
                .system(forANSWER)
                .user(message)
                .options(openAiChatOptions)
                .call()
                .content();
    }
    @Override
    @Transactional
    public SOA_DTO helpAnswer(String answer){
        return null;
    }

    @Override
    @Transactional
    public LawListDTO lawSearch(List<String> summaries) {
        String merged = String.join("\n", summaries);

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.65)
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();


        // JSON → DTO 변환기 준비
        var outputConverter = new BeanOutputConverter<>(LawListDTO.class);
        var jsonSchema = outputConverter.getJsonSchema();

        // OpenAI 옵션 (구조화된 출력 적용)
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4.1-nano")
                .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                .build();

        // OpenAI 호출
        String response = chatClient
                .prompt()
                .user(userSpec -> userSpec
                        .text(forLAWS)
                        .param("summaries", merged))
                .advisors(retrievalAugmentationAdvisor)
                .options(openAiChatOptions)
                .call()
                .content();

        // JSON → DTO 변환
        if (response == null) {
            throw new RuntimeException("LLM 응답이 비어 있습니다.");
        }
        return outputConverter.convert(response);
    }
}
