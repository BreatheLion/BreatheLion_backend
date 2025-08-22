package YAMSABU.BreatheLion_backend.global.ai.service;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Law;
import YAMSABU.BreatheLion_backend.domain.organization.entity.Organization;
import YAMSABU.BreatheLion_backend.domain.organization.repository.OrganizationRepository;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.ChatSummaryDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawListDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.SOA_DTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forANSWER;
import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forCHATSUMMARY;
import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forHELP;
import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forLAWS;
import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forRECORDSUMMARY;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService{

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final OrganizationRepository organizationRepository;

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
    public void helpAnswer(Drawer drawer, String summaries){

        List<Organization> organizations = organizationRepository.findAll();

        // 각 기관 이름에 번호를 붙이고
        String organString = organizations.stream()
                .map(org -> org.getId() + ". " + org.getName())
                .collect(Collectors.joining("\n"));

        var outputConverter = new BeanOutputConverter<>(SOA_DTO.class);
        var jsonSchema = outputConverter.getJsonSchema();

        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4.1-nano")
                .temperature(0.4)
                .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                .build();

        String response = chatClient
                .prompt()
                .user(userSpec -> userSpec
                        .text(forHELP)
                        .param("summaries", summaries)
                        .param("organizations", organString))
                .options(openAiChatOptions)
                .call()
                .content();

        if (response == null) {
            throw new RuntimeException("LLM 응답이 비어 있습니다.");
        }
        return outputConverter.convert(response);
    }

    @Override
    @Transactional
    public void lawSearch(Drawer drawer,String summaries) {

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.25)
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
        LawListDTO lawListDTO = chatClient
                .prompt()
                .user(userSpec -> userSpec
                        .text(forLAWS)
                        .param("summaries", summaries))
                .advisors(retrievalAugmentationAdvisor)
                .options(openAiChatOptions)
                .call()
                .entity(LawListDTO.class);

        for (LawDTO dto : lawListDTO.getLaws()) {
            if (dto == null) continue;
            String lawName = dto.getLawName();
            String article = dto.getArticle();
            String content = dto.getContent();

            Law law = Law.builder()
                    .lawName(lawName)
                    .article(article)
                    .content(content)
                    .drawer(drawer)
                    .build();

            drawer.addLaw(law);
        }
    }

    @Override
    @Transactional
    public ChatSummaryDTO chatSummary(String chattings){

        var outputConverter = new BeanOutputConverter<>(ChatSummaryDTO.class);
        var jsonSchema = outputConverter.getJsonSchema();

        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4.1-nano")
                .temperature(0.30)
                .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                .build();

        String response = chatClient
                .prompt()
                .user(userSpec -> userSpec
                        .text(forCHATSUMMARY)
                        .param("chattings", chattings))
                .options(openAiChatOptions)
                .call()
                .content();

        if (response == null) {
            throw new RuntimeException("LLM 응답이 비어 있습니다.");
        }
        return outputConverter.convert(response);
    }
    @Override
    @Transactional
    public String recordSummary(Record record){
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4.1-nano")
                .temperature(0.45)
                .build();

        String recordInfo =
                "내용: " + record.getContent() + "\n" +
                "장소: " + record.getLocation() + "\n" +
                "발생일시: " + (record.getOccurredAt() != null ? record.getOccurredAt().toString() : "") + "\n" +
                "카테고리: " + String.join(", ", record.getCategories().stream().map(Enum::name).toList());

        return chatClient
                .prompt()
                .user(userSpec -> userSpec
                        .text(forRECORDSUMMARY)
                        .param("title", record.getTitle())
                        .param("info",recordInfo))
                .options(openAiChatOptions)
                .call()
                .content();
    }

}
